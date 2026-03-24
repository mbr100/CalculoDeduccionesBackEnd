package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TipoCotizacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.TipoCotizacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BonificacionesTrabajador;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.CosteHoraPersonal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CosteHoraService {
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final int SCALE = 6;

    private final PersonalRepository personalRepository;
    private final RetribucionRepository retribucionRepository;
    private final BasesCotizacionRepository basesCotizacionRepository;
    private final HorasEmpleadoRepository horasEmpleadoRepository;
    private final BonificacionesTrabajadorRepository bonificacionesTrabajadorRepository;
    private final TipoCotizacionRepository tipoCotizacionRepository;

    private final Logger logger = LoggerFactory.getLogger(CosteHoraService.class);

    public CosteHoraService(PersonalRepository personalRepository, RetribucionRepository retribucionRepository,
                            BasesCotizacionRepository basesCotizacionRepository, HorasEmpleadoRepository horasEmpleadoRepository,
                            BonificacionesTrabajadorRepository bonificacionesTrabajadorRepository,
                            TipoCotizacionRepository tipoCotizacionRepository) {
        this.personalRepository = personalRepository;
        this.retribucionRepository = retribucionRepository;
        this.basesCotizacionRepository = basesCotizacionRepository;
        this.horasEmpleadoRepository = horasEmpleadoRepository;
        this.bonificacionesTrabajadorRepository = bonificacionesTrabajadorRepository;
        this.tipoCotizacionRepository = tipoCotizacionRepository;
    }

    public void calcularCosteHoraEconomico(Economico economico) {
        logger.info("Iniciando cálculo del coste por hora para el económico con ID: {}", economico.getIdEconomico());
        economico.getPersonal().forEach(personal -> this.calcularCosteHoraPersonal(personal, economico));
    }

    private void calcularCosteHoraPersonal(Personal personal, Economico economico) {
        CosteHoraPersonal ch = personal.getCosteHoraPersonal();
        String cnae = String.valueOf(economico.getCNAE());
        int anualidad = Math.toIntExact(economico.getAnualidad());

        // 1. Retribución anual
        BigDecimal retribucionAnual = BigDecimal.valueOf(
                retribucionRepository.findById(personal.getIdPersona())
                        .orElseThrow(() -> new RuntimeException("No se encontró la retribución para el personal con ID: " + personal.getIdPersona()))
                        .getPercepcionesSalariales());
        ch.setRetribucionTotal(retribucionAnual);

        // 2. Horas anuales efectivas
        BigDecimal horasAnuales = BigDecimal.valueOf(
                horasEmpleadoRepository.findById(personal.getIdPersona())
                        .orElseThrow(() -> new RuntimeException("No se encontraron las horas para el personal con ID: " + personal.getIdPersona()))
                        .getHorasMaximasAnuales());
        ch.setHorasMaximas(horasAnuales);

        // 3. Base CC anual (suma de 12 meses, viene de los RNTs)
        BigDecimal baseCCAnual = BigDecimal.valueOf(
                basesCotizacionRepository.findById(personal.getIdPersona())
                        .orElseThrow(() -> new RuntimeException("No se encontró la base de cotización para el personal con ID: " + personal.getIdPersona()))
                        .getBasesCotizacionContingenciasComunesAnual());

        // 4. Porcentajes desde TipoCotizacion (por CNAE + anualidad)
        TipoCotizacion cotizacion = tipoCotizacionRepository.findByCnaeAndAnualidad(cnae, anualidad)
                .orElseThrow(() -> new RuntimeException("No se encontró tipo de cotización para CNAE " + cnae + " y anualidad " + anualidad));

        BigDecimal pctCC = cotizacion.getContingenciasComunes();
        BigDecimal pctATEP = cotizacion.getAccidentesTrabajoTotal();
        BigDecimal pctFOGASA = cotizacion.getFogasa();
        BigDecimal pctFP = cotizacion.getFormacionProfesional();
        BigDecimal pctMEI = cotizacion.getMei();

        // 5. Desempleo según tipo de contrato del trabajador
        BigDecimal pctDesempleo = personal.isEsContratoIndefinido()
                ? cotizacion.getDesempleoIndefinido()
                : cotizacion.getDesempleoTemporal();

        // 6. SS empresa = baseCCAnual × (CC + AT/EP + desempleo + FOGASA + FP + MEI) / 100
        BigDecimal porcentajeTotal = pctCC.add(pctATEP).add(pctDesempleo).add(pctFOGASA).add(pctFP).add(pctMEI);
        BigDecimal ssEmpresa = baseCCAnual.multiply(porcentajeTotal).divide(CIEN, SCALE, RoundingMode.HALF_UP);

        logger.info("Personal ID {}: porcentaje total SS = {}%, SS empresa = {}", personal.getIdPersona(), porcentajeTotal, ssEmpresa);

        // 7. Bonificación: descuento sobre cuota de CC empresa
        BonificacionesTrabajador bonificacion = personal.getBonificacionesTrabajador();
        if (bonificacion != null && bonificacion.getPorcentajeBonificacion() != null) {
            BigDecimal pctBonificacion = bonificacion.getPorcentajeBonificacion();

            // descuento = baseCCAnual × (CC/100) × (bonificacion/100)
            BigDecimal cuotaCC = baseCCAnual.multiply(pctCC).divide(CIEN, SCALE, RoundingMode.HALF_UP);
            BigDecimal descuento = cuotaCC.multiply(pctBonificacion).divide(CIEN, SCALE, RoundingMode.HALF_UP);

            ssEmpresa = ssEmpresa.subtract(descuento);
            logger.info("Personal ID {}: bonificación {}% sobre cuota CC, descuento = {}, SS final = {}",
                    personal.getIdPersona(), pctBonificacion, descuento, ssEmpresa);
        }

        ch.setCosteSS(ssEmpresa);

        // 8. Coste/hora = (retribución + SS empresa) / horas anuales
        if (horasAnuales.compareTo(BigDecimal.ZERO) > 0) {
            ch.setCosteHora(retribucionAnual.add(ssEmpresa).divide(horasAnuales, SCALE, RoundingMode.HALF_UP));
        } else {
            ch.setCosteHora(BigDecimal.ZERO);
        }

        personal.setCosteHoraPersonal(ch);
        personalRepository.save(personal);
        logger.info("Cálculo del coste por hora completado para el personal con ID: {}", personal.getIdPersona());
    }
}
