package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveOcupacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ConfiguracionAnualSS;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TarifaPrimasCnae;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveOcupacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ConfiguracionAnualSSRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.TarifaPrimasCnaeRepository;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.CosteHoraPersonal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.*;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.BonificacionResultDTO;
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
    private final ConfiguracionAnualSSRepository configuracionAnualSSRepository;
    private final TarifaPrimasCnaeRepository tarifaPrimasCnaeRepository;
    private final ClaveOcupacionRepository claveOcupacionRepository;
    private final BonificacionService bonificacionService;

    private final Logger logger = LoggerFactory.getLogger(CosteHoraService.class);

    public CosteHoraService(PersonalRepository personalRepository, RetribucionRepository retribucionRepository,
                            BasesCotizacionRepository basesCotizacionRepository, HorasEmpleadoRepository horasEmpleadoRepository,
                            ConfiguracionAnualSSRepository configuracionAnualSSRepository,
                            TarifaPrimasCnaeRepository tarifaPrimasCnaeRepository,
                            ClaveOcupacionRepository claveOcupacionRepository,
                            BonificacionService bonificacionService) {
        this.personalRepository = personalRepository;
        this.retribucionRepository = retribucionRepository;
        this.basesCotizacionRepository = basesCotizacionRepository;
        this.horasEmpleadoRepository = horasEmpleadoRepository;
        this.configuracionAnualSSRepository = configuracionAnualSSRepository;
        this.tarifaPrimasCnaeRepository = tarifaPrimasCnaeRepository;
        this.claveOcupacionRepository = claveOcupacionRepository;
        this.bonificacionService = bonificacionService;
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

        // 4. Obtener configuración anual SS (tipos comunes a todos los CNAEs)
        ConfiguracionAnualSS config = configuracionAnualSSRepository.findByAnio(anualidad)
                .orElseThrow(() -> new RuntimeException("No se encontró configuración SS para el año " + anualidad));

        BigDecimal pctCC = config.getCcEmpresa();
        BigDecimal pctFOGASA = config.getFogasa();
        BigDecimal pctFP = config.getFpEmpresa();
        BigDecimal pctMEI = config.getMeiEmpresa();

        // 5. AT/EP: si el trabajador tiene clave de ocupación (Cuadro II), usar ese tipo en vez del CNAE
        BigDecimal pctATEP;
        String origenTipoATEP;
        String claveOcupacion = personal.getClaveOcupacion();
        if (claveOcupacion != null && !claveOcupacion.isBlank()) {
            ClaveOcupacion ocupacion = claveOcupacionRepository.findByClaveAndActivaTrue(claveOcupacion.toLowerCase())
                    .orElseThrow(() -> new RuntimeException("No se encontró clave de ocupación activa '" + claveOcupacion + "'"));
            pctATEP = ocupacion.getTipoTotal();
            origenTipoATEP = "CUADRO_II_CLAVE_" + claveOcupacion.toUpperCase();
            logger.info("Personal ID {}: usando tipo AT/EP del Cuadro II (clave '{}') = {}%", personal.getIdPersona(), claveOcupacion, pctATEP);
        } else {
            TarifaPrimasCnae tarifa = tarifaPrimasCnaeRepository.findByCnaeAndAnio(cnae, anualidad)
                    .orElseThrow(() -> new RuntimeException("No se encontró tarifa de primas para CNAE " + cnae + " y año " + anualidad));
            pctATEP = tarifa.getTipoTotal();
            origenTipoATEP = "CUADRO_I_CNAE_" + cnae;
        }

        // 6. Desempleo según tipo de contrato del trabajador
        BigDecimal pctDesempleo = personal.isEsContratoIndefinido()
                ? config.getDesempleoEmpresaIndefinido()
                : config.getDesempleoEmpresaTemporal();

        // 7. Calcular cada cuota SS empresa por separado
        BigDecimal baseCPAnual = baseCCAnual;

        BigDecimal cuotaCC = baseCCAnual.multiply(pctCC).divide(CIEN, SCALE, RoundingMode.HALF_UP);
        BigDecimal cuotaATEP = baseCPAnual.multiply(pctATEP).divide(CIEN, SCALE, RoundingMode.HALF_UP);
        BigDecimal cuotaDesempleo = baseCPAnual.multiply(pctDesempleo).divide(CIEN, SCALE, RoundingMode.HALF_UP);
        BigDecimal cuotaFogasa = baseCPAnual.multiply(pctFOGASA).divide(CIEN, SCALE, RoundingMode.HALF_UP);
        BigDecimal cuotaFP = baseCPAnual.multiply(pctFP).divide(CIEN, SCALE, RoundingMode.HALF_UP);
        BigDecimal cuotaMEI = baseCCAnual.multiply(pctMEI).divide(CIEN, SCALE, RoundingMode.HALF_UP);

        BigDecimal ssEmpresaBruta = cuotaCC.add(cuotaATEP).add(cuotaDesempleo).add(cuotaFogasa).add(cuotaFP).add(cuotaMEI);

        logger.info("Personal ID {}: SS empresa bruta = {} (CC={}, ATEP={}, Desemp={}, FOGASA={}, FP={}, MEI={})",
                personal.getIdPersona(), ssEmpresaBruta, cuotaCC, cuotaATEP, cuotaDesempleo, cuotaFogasa, cuotaFP, cuotaMEI);

        // 8. Calcular ahorro por bonificaciones (período-based)
        BonificacionResultDTO bonificaciones = bonificacionService.calcularAhorroBonificaciones(
                personal.getIdPersona(), cuotaCC, anualidad);

        BigDecimal ahorroBonificaciones = bonificaciones.getAhorroTotalAnual();

        // 9. SS empresa neta (coste real soportado)
        BigDecimal ssEmpresaNeta = ssEmpresaBruta.subtract(ahorroBonificaciones);
        if (ssEmpresaNeta.compareTo(BigDecimal.ZERO) < 0) {
            ssEmpresaNeta = BigDecimal.ZERO;
        }

        // Aplicar descuento a la cuota CC para el desglose
        BigDecimal cuotaCCNeta = cuotaCC.subtract(ahorroBonificaciones);
        if (cuotaCCNeta.compareTo(BigDecimal.ZERO) < 0) {
            cuotaCCNeta = BigDecimal.ZERO;
        }

        logger.info("Personal ID {}: bonificaciones ahorro = {}, SS neta = {}",
                personal.getIdPersona(), ahorroBonificaciones, ssEmpresaNeta);

        // Guardar desglose y trazabilidad
        ch.setCuotaCC(cuotaCCNeta);
        ch.setCuotaATEP(cuotaATEP);
        ch.setCuotaDesempleo(cuotaDesempleo);
        ch.setCuotaFogasa(cuotaFogasa);
        ch.setCuotaFP(cuotaFP);
        ch.setCuotaMEI(cuotaMEI);
        ch.setTipoATEPAplicado(pctATEP);
        ch.setOrigenTipoATEP(origenTipoATEP);
        ch.setSsEmpresaBruta(ssEmpresaBruta);
        ch.setAhorroBonificaciones(ahorroBonificaciones);
        ch.setAhorroInvestigador(bonificaciones.getAhorroInvestigador());
        ch.setAhorroOtrasBonificaciones(bonificaciones.getAhorroOtrasBonificaciones());
        ch.setCosteSS(ssEmpresaNeta);

        // 10. Coste/hora = (retribución + SS empresa neta) / horas anuales
        if (horasAnuales.compareTo(BigDecimal.ZERO) > 0) {
            ch.setCosteHora(retribucionAnual.add(ssEmpresaNeta).divide(horasAnuales, SCALE, RoundingMode.HALF_UP));
        } else {
            ch.setCosteHora(BigDecimal.ZERO);
        }

        personal.setCosteHoraPersonal(ch);
        personalRepository.save(personal);
        logger.info("Cálculo del coste por hora completado para el personal con ID: {}", personal.getIdPersona());
    }
}
