package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.PorcentajeCotizacionEmpresaRepository;
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
    private final PersonalRepository personalRepository;
    private final RetribucionRepository retribucionRepository;
    private final BasesCotizacionRepository basesCotizacionRepository;
    private final HorasEmpleadoRepository horasEmpleadoRepository;
    private final BonificacionesTrabajadorRepository bonificacionesTrabajadorRepository;
    private final PorcentajeCotizacionEmpresaRepository porcentajeCotizacionEmpresaRepository;

    private final Logger logger = LoggerFactory.getLogger(CosteHoraService.class);

    public CosteHoraService(PersonalRepository personalRepository, RetribucionRepository retribucionRepository, BasesCotizacionRepository basesCotizacionRepository, HorasEmpleadoRepository horasEmpleadoRepository, BonificacionesTrabajadorRepository bonificacionesTrabajadorRepository, PorcentajeCotizacionEmpresaRepository porcentajeCotizacionEmpresaRepository) {
        this.personalRepository = personalRepository;
        this.retribucionRepository = retribucionRepository;
        this.basesCotizacionRepository = basesCotizacionRepository;
        this.horasEmpleadoRepository = horasEmpleadoRepository;
        this.bonificacionesTrabajadorRepository = bonificacionesTrabajadorRepository;
        this.porcentajeCotizacionEmpresaRepository = porcentajeCotizacionEmpresaRepository;
    }

    public void calcularCosteHoraEconomico(Economico economico) {
        logger.info("Iniciando cálculo del coste por hora para el económico con ID: {}", economico.getIdEconomico());
        economico.getPersonal().forEach(personal -> this.calcularCosteHoraPersonal(personal, Math.toIntExact(economico.getCNAE())));
    }

    private void calcularCosteHoraPersonal(Personal personal, int cnae) {
        CosteHoraPersonal  ch= personal.getCosteHoraPersonal();
        BigDecimal retribucionAnual = BigDecimal.valueOf(retribucionRepository.findById(personal.getIdPersona()).orElseThrow(() -> new RuntimeException("No se encontró la retribución para el personal con ID: " + personal.getIdPersona())).getPercepcionesSalariales());
        ch.setRetribucionTotal(retribucionAnual);
        BigDecimal horasAnuales = BigDecimal.valueOf(horasEmpleadoRepository.findById(personal.getIdPersona()).orElseThrow(() -> new RuntimeException("No se encontraron las horas para el personal con ID: " + personal.getIdPersona())).getHorasMaximasAnuales());
        ch.setHorasMaximas(horasAnuales);
        BigDecimal SSAnual = BigDecimal.valueOf(basesCotizacionRepository.findById(personal.getIdPersona()).orElseThrow(() -> new RuntimeException("No se encontró la base de cotización para el personal con ID: " + personal.getIdPersona())).getBasesCotizacionContingenciasComunesAnual());
        BigDecimal porcentajeSS = porcentajeCotizacionEmpresaRepository.findbyCNAE(cnae).orElseThrow(() -> new RuntimeException("No se encontró el porcentaje de cotización para el CNAE: " + cnae)).getPorcentajeCotizacionEmpresa();
        BigDecimal porcentajeDecimal = porcentajeSS.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        logger.info("Porcentaje de cotización a la Seguridad Social para el personal con ID {}: {}%", personal.getIdPersona(), porcentajeDecimal);
        if (personal.getBonificacionesTrabajador()!=null){
            long porcentajeBonificacion = bonificacionesTrabajadorRepository.findById(personal.getIdPersona()).orElseThrow(() -> new RuntimeException("No se encontró la bonificación para el personal con ID: " + personal.getIdPersona())).getPorcentajeBonificacion().longValue();
            BigDecimal descuentoBonificacion = BigDecimal.valueOf(porcentajeBonificacion/100);
            BigDecimal SSAntesDeDescuentos = SSAnual.multiply(porcentajeDecimal);
            BigDecimal contigenciasComunes = porcentajeCotizacionEmpresaRepository.findbyCNAE(cnae).orElseThrow(() -> new RuntimeException("No se encontró el porcentaje de cotización para el CNAE: " + cnae)).getContingenciasComunes();
            BigDecimal baseAnual = BigDecimal.valueOf(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesAnual());
            BigDecimal descuentosSS = baseAnual
                    .multiply(contigenciasComunes)      // multiplica por % contingencias comunes
                    .multiply(descuentoBonificacion);   // multiplica por bonificación

            BigDecimal SSDespuesDeDescuentos = SSAntesDeDescuentos.subtract(descuentosSS);
            ch.setCosteSS(SSDespuesDeDescuentos);
            logger.info("Coste de la Seguridad Social después de bonificaciones para el personal con ID {}: {}", personal.getIdPersona(), SSDespuesDeDescuentos);
        } else {
            ch.setCosteSS(SSAnual.multiply(porcentajeDecimal));
        }
        ch.setCosteHora((ch.getRetribucionTotal().add(ch.getCosteSS())).divide(horasAnuales,6, RoundingMode.FLOOR));
        personal.setCosteHoraPersonal(ch);
        personalRepository.save(personal);
        logger.info("Cálculo del coste por hora completado para el personal con ID: {}", personal.getIdPersona());
    }
}
