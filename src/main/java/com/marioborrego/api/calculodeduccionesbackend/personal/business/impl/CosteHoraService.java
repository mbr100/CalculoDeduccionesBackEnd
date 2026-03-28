package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveContrato;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveOcupacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ConfiguracionAnualSS;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TarifaPrimasCnae;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveOcupacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ConfiguracionAnualSSRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.TarifaPrimasCnaeRepository;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.*;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.NaturalezaContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PeriodoContratoRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PersonalRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.BonificacionResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CosteHoraService {
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final int SCALE = 6;
    private static final BigDecimal DOCE = BigDecimal.valueOf(12);

    private final PersonalRepository personalRepository;
    private final ConfiguracionAnualSSRepository configuracionAnualSSRepository;
    private final TarifaPrimasCnaeRepository tarifaPrimasCnaeRepository;
    private final ClaveOcupacionRepository claveOcupacionRepository;
    private final BonificacionService bonificacionService;
    private final PeriodoContratoRepository periodoContratoRepository;

    private final Logger logger = LoggerFactory.getLogger(CosteHoraService.class);

    public CosteHoraService(PersonalRepository personalRepository,
                            ConfiguracionAnualSSRepository configuracionAnualSSRepository,
                            TarifaPrimasCnaeRepository tarifaPrimasCnaeRepository,
                            ClaveOcupacionRepository claveOcupacionRepository,
                            BonificacionService bonificacionService,
                            PeriodoContratoRepository periodoContratoRepository) {
        this.personalRepository = personalRepository;
        this.configuracionAnualSSRepository = configuracionAnualSSRepository;
        this.tarifaPrimasCnaeRepository = tarifaPrimasCnaeRepository;
        this.claveOcupacionRepository = claveOcupacionRepository;
        this.bonificacionService = bonificacionService;
        this.periodoContratoRepository = periodoContratoRepository;
    }

    public void calcularCosteHoraEconomico(Economico economico) {
        logger.info("Iniciando cálculo del coste por hora para el económico con ID: {}", economico.getIdEconomico());
        economico.getPersonal().forEach(personal -> this.calcularCosteHoraPersonal(personal, economico));
    }

    private void calcularCosteHoraPersonal(Personal personal, Economico economico) {
        CosteHoraPersonal ch = personal.getCosteHoraPersonal() != null
                ? personal.getCosteHoraPersonal()
                : new CosteHoraPersonal();

        String cnae = String.valueOf(economico.getCNAE());
        int anualidad = Math.toIntExact(economico.getAnualidad());

        // PASO 1: Configuración anual SS
        ConfiguracionAnualSS config = configuracionAnualSSRepository.findByAnio(anualidad)
                .orElseThrow(() -> new RuntimeException("No se encontró configuración SS para el año " + anualidad));

        // PASO 2: Resolver tipo AT/EP
        BigDecimal tipoAtep;
        String origenTipoATEP;
        String claveOcupacion = personal.getClaveOcupacion();
        if (claveOcupacion != null && !claveOcupacion.isBlank()) {
            ClaveOcupacion ocupacion = claveOcupacionRepository.findByClaveAndActivaTrue(claveOcupacion.toLowerCase())
                    .orElseThrow(() -> new RuntimeException("No se encontró clave de ocupación activa '" + claveOcupacion + "'"));
            tipoAtep = ocupacion.getTipoTotal();
            origenTipoATEP = "CUADRO_II_CLAVE_" + claveOcupacion.toUpperCase();
        } else {
            TarifaPrimasCnae tarifa = tarifaPrimasCnaeRepository.findByCnaeAndAnio(cnae, anualidad)
                    .orElseThrow(() -> new RuntimeException("No se encontró tarifa de primas para CNAE " + cnae + " y año " + anualidad));
            tipoAtep = tarifa.getTipoTotal();
            origenTipoATEP = "CUADRO_I_CNAE_" + cnae;
        }

        // PASO 3: Obtener períodos de contrato
        List<PeriodoContrato> periodos = periodoContratoRepository
                .findByPersonalIdPersonaAndAnioFiscalOrderByFechaAltaAsc(personal.getIdPersona(), anualidad);

        // Retribución total (de la tabla retribuciones, es la suma global)
        Retribucion retribucion = obtenerRetribucion(personal);
        BigDecimal retribucionAnual = BigDecimal.valueOf(retribucion.getPercepcionesSalariales());

        if (periodos.isEmpty()) {
            // FALLBACK: sin períodos, usar algoritmo legacy con BasesCotizacion
            calcularLegacy(personal, ch, config, tipoAtep, origenTipoATEP, retribucionAnual, cnae, anualidad);
        } else {
            // ALGORITMO NUEVO: multi-período con bases RNT
            calcularConPeriodos(personal, ch, config, tipoAtep, origenTipoATEP, retribucionAnual,
                    periodos, economico.getHorasConvenio(), anualidad);
        }

        personal.setCosteHoraPersonal(ch);
        personalRepository.save(personal);
        logger.info("Cálculo del coste por hora completado para el personal con ID: {}", personal.getIdPersona());
    }

    private void calcularConPeriodos(Personal personal, CosteHoraPersonal ch,
                                     ConfiguracionAnualSS config, BigDecimal tipoAtep,
                                     String origenTipoATEP, BigDecimal retribucionAnual,
                                     List<PeriodoContrato> periodos, Long horasConvenio,
                                     int anualidad) {
        BigDecimal horasAnualesConvenio = BigDecimal.valueOf(horasConvenio);
        int diasDelAnio = Year.of(anualidad).length();
        BigDecimal diasDelAnioDecimal = BigDecimal.valueOf(diasDelAnio);

        BigDecimal totalCuotaCc = BigDecimal.ZERO;
        BigDecimal totalCuotaAtep = BigDecimal.ZERO;
        BigDecimal totalCuotaDesempleo = BigDecimal.ZERO;
        BigDecimal totalCuotaFogasa = BigDecimal.ZERO;
        BigDecimal totalCuotaFp = BigDecimal.ZERO;
        BigDecimal totalCuotaMei = BigDecimal.ZERO;
        BigDecimal totalHorasEfectivas = BigDecimal.ZERO;
        long diasTotalesTrabajados = 0;

        LocalDate inicioAnio = LocalDate.of(anualidad, 1, 1);
        LocalDate finAnio = LocalDate.of(anualidad, 12, 31);

        for (PeriodoContrato periodo : periodos) {
            ClaveContrato clave = periodo.getClaveContrato();
            NaturalezaContrato naturaleza = clave.getNaturaleza();

            // Fechas efectivas dentro del año fiscal
            LocalDate fechaInicio = periodo.getFechaAlta().isBefore(inicioAnio) ? inicioAnio : periodo.getFechaAlta();
            LocalDate fechaFin = periodo.getFechaBaja() != null
                    ? (periodo.getFechaBaja().isAfter(finAnio) ? finAnio : periodo.getFechaBaja())
                    : finAnio;

            long diasPeriodo = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
            diasTotalesTrabajados += diasPeriodo;
            BigDecimal diasPeriodoDecimal = BigDecimal.valueOf(diasPeriodo);

            // Horas efectivas de este período
            BigDecimal horasPeriodo = horasAnualesConvenio
                    .multiply(diasPeriodoDecimal)
                    .divide(diasDelAnioDecimal, SCALE, RoundingMode.HALF_UP)
                    .multiply(periodo.getPorcentajeJornada())
                    .divide(CIEN, SCALE, RoundingMode.HALF_UP);
            totalHorasEfectivas = totalHorasEfectivas.add(horasPeriodo);

            BigDecimal proporcion = diasPeriodoDecimal.divide(diasDelAnioDecimal, SCALE, RoundingMode.HALF_UP);

            BigDecimal cuotaCcPeriodo;
            BigDecimal cuotaAtepPeriodo;
            BigDecimal cuotaDesempleoPeriodo = BigDecimal.ZERO;
            BigDecimal cuotaFogasaPeriodo = BigDecimal.ZERO;
            BigDecimal cuotaFpPeriodo = BigDecimal.ZERO;
            BigDecimal cuotaMeiPeriodo = BigDecimal.ZERO;

            if (naturaleza == NaturalezaContrato.BECARIO_NO_REMUNERADO) {
                // Cuota fija diaria, 95% reducción
                cuotaCcPeriodo = new BigDecimal("2.67").multiply(diasPeriodoDecimal).multiply(new BigDecimal("0.05"));
                cuotaAtepPeriodo = new BigDecimal("0.33").multiply(diasPeriodoDecimal);

            } else if (naturaleza == NaturalezaContrato.BECARIO_REMUNERADO || naturaleza == NaturalezaContrato.FORMACION) {
                // Cuota fija mensual, 95% reducción
                BigDecimal mesesPeriodo = diasPeriodoDecimal.divide(new BigDecimal("30.42"), SCALE, RoundingMode.HALF_UP);
                cuotaCcPeriodo = new BigDecimal("60.76").multiply(mesesPeriodo).multiply(new BigDecimal("0.05"));
                cuotaAtepPeriodo = new BigDecimal("7.38").multiply(mesesPeriodo);

            } else {
                // INDEFINIDO o TEMPORAL: cálculo estándar con bases RNT
                BigDecimal baseCcAnual = periodo.getBaseCcMensual().multiply(DOCE);
                BigDecimal baseCpAnual = periodo.getBaseCpMensual().multiply(DOCE);

                cuotaCcPeriodo = baseCcAnual.multiply(config.getCcEmpresa()).divide(CIEN, SCALE, RoundingMode.HALF_UP).multiply(proporcion);
                cuotaAtepPeriodo = baseCpAnual.multiply(tipoAtep).divide(CIEN, SCALE, RoundingMode.HALF_UP).multiply(proporcion);

                if (clave.getCotizaDesempleo()) {
                    BigDecimal tipoDesempleo = naturaleza == NaturalezaContrato.TEMPORAL
                            ? config.getDesempleoEmpresaTemporal()
                            : config.getDesempleoEmpresaIndefinido();
                    cuotaDesempleoPeriodo = baseCpAnual.multiply(tipoDesempleo).divide(CIEN, SCALE, RoundingMode.HALF_UP).multiply(proporcion);
                }

                if (clave.getCotizaFogasa()) {
                    cuotaFogasaPeriodo = baseCpAnual.multiply(config.getFogasa()).divide(CIEN, SCALE, RoundingMode.HALF_UP).multiply(proporcion);
                }
                if (clave.getCotizaFp()) {
                    cuotaFpPeriodo = baseCpAnual.multiply(config.getFpEmpresa()).divide(CIEN, SCALE, RoundingMode.HALF_UP).multiply(proporcion);
                }
                if (clave.getCotizaMei()) {
                    cuotaMeiPeriodo = baseCcAnual.multiply(config.getMeiEmpresa()).divide(CIEN, SCALE, RoundingMode.HALF_UP).multiply(proporcion);
                }
            }

            totalCuotaCc = totalCuotaCc.add(cuotaCcPeriodo);
            totalCuotaAtep = totalCuotaAtep.add(cuotaAtepPeriodo);
            totalCuotaDesempleo = totalCuotaDesempleo.add(cuotaDesempleoPeriodo);
            totalCuotaFogasa = totalCuotaFogasa.add(cuotaFogasaPeriodo);
            totalCuotaFp = totalCuotaFp.add(cuotaFpPeriodo);
            totalCuotaMei = totalCuotaMei.add(cuotaMeiPeriodo);
        }

        // PASO 6: SS empresa bruta
        BigDecimal ssEmpresaBruta = totalCuotaCc.add(totalCuotaAtep).add(totalCuotaDesempleo)
                .add(totalCuotaFogasa).add(totalCuotaFp).add(totalCuotaMei);

        // PASO 7: Bonificaciones
        BonificacionResultDTO bonificaciones = bonificacionService.calcularAhorroBonificaciones(
                personal.getIdPersona(), totalCuotaCc, anualidad);
        BigDecimal ahorroBonificaciones = bonificaciones.getAhorroTotalAnual();

        // PASO 8: SS empresa neta
        BigDecimal ssEmpresaNeta = ssEmpresaBruta.subtract(ahorroBonificaciones).max(BigDecimal.ZERO);

        // Guardar desglose
        ch.setRetribucionTotal(retribucionAnual);
        ch.setHorasMaximas(totalHorasEfectivas);
        ch.setCuotaCC(totalCuotaCc);
        ch.setCuotaATEP(totalCuotaAtep);
        ch.setCuotaDesempleo(totalCuotaDesempleo);
        ch.setCuotaFogasa(totalCuotaFogasa);
        ch.setCuotaFP(totalCuotaFp);
        ch.setCuotaMEI(totalCuotaMei);
        ch.setTipoATEPAplicado(tipoAtep);
        ch.setOrigenTipoATEP(origenTipoATEP);
        ch.setSsEmpresaBruta(ssEmpresaBruta);
        ch.setAhorroBonificaciones(ahorroBonificaciones);
        ch.setAhorroInvestigador(bonificaciones.getAhorroInvestigador());
        ch.setAhorroOtrasBonificaciones(bonificaciones.getAhorroOtrasBonificaciones());
        ch.setCosteSS(ssEmpresaNeta);

        // PASO 9: Coste/hora
        if (totalHorasEfectivas.compareTo(BigDecimal.ZERO) > 0) {
            ch.setCosteHora(retribucionAnual.add(ssEmpresaNeta).divide(totalHorasEfectivas, SCALE, RoundingMode.HALF_UP));
        } else {
            ch.setCosteHora(BigDecimal.ZERO);
        }

        logger.info("Personal ID {} (períodos): costeHora={}, retrib={}, SS_neta={}, horas={}",
                personal.getIdPersona(), ch.getCosteHora(), retribucionAnual, ssEmpresaNeta, totalHorasEfectivas);
    }

    private void calcularLegacy(Personal personal, CosteHoraPersonal ch,
                                ConfiguracionAnualSS config, BigDecimal tipoAtep,
                                String origenTipoATEP, BigDecimal retribucionAnual,
                                String cnae, int anualidad) {
        BasesCotizacion basesCotizacion = obtenerBasesCotizacion(personal);
        HorasPersonal horasPersonal = obtenerHorasPersonal(personal);

        ch.setRetribucionTotal(retribucionAnual);
        BigDecimal horasAnuales = BigDecimal.valueOf(horasPersonal.getHorasMaximasAnuales());
        ch.setHorasMaximas(horasAnuales);

        BigDecimal baseCCAnual = BigDecimal.valueOf(basesCotizacion.getBasesCotizacionContingenciasComunesAnual());
        BigDecimal baseCPAnual = baseCCAnual;

        BigDecimal pctDesempleo = personal.isEsContratoIndefinido()
                ? config.getDesempleoEmpresaIndefinido()
                : config.getDesempleoEmpresaTemporal();

        BigDecimal cuotaCC = baseCCAnual.multiply(config.getCcEmpresa()).divide(CIEN, SCALE, RoundingMode.HALF_UP);
        BigDecimal cuotaATEP = baseCPAnual.multiply(tipoAtep).divide(CIEN, SCALE, RoundingMode.HALF_UP);
        BigDecimal cuotaDesempleo = baseCPAnual.multiply(pctDesempleo).divide(CIEN, SCALE, RoundingMode.HALF_UP);
        BigDecimal cuotaFogasa = baseCPAnual.multiply(config.getFogasa()).divide(CIEN, SCALE, RoundingMode.HALF_UP);
        BigDecimal cuotaFP = baseCPAnual.multiply(config.getFpEmpresa()).divide(CIEN, SCALE, RoundingMode.HALF_UP);
        BigDecimal cuotaMEI = baseCCAnual.multiply(config.getMeiEmpresa()).divide(CIEN, SCALE, RoundingMode.HALF_UP);

        BigDecimal ssEmpresaBruta = cuotaCC.add(cuotaATEP).add(cuotaDesempleo).add(cuotaFogasa).add(cuotaFP).add(cuotaMEI);

        BonificacionResultDTO bonificaciones = bonificacionService.calcularAhorroBonificaciones(
                personal.getIdPersona(), cuotaCC, anualidad);
        BigDecimal ahorroBonificaciones = bonificaciones.getAhorroTotalAnual();
        BigDecimal ssEmpresaNeta = ssEmpresaBruta.subtract(ahorroBonificaciones).max(BigDecimal.ZERO);

        BigDecimal cuotaCCNeta = cuotaCC.subtract(ahorroBonificaciones).max(BigDecimal.ZERO);

        ch.setCuotaCC(cuotaCCNeta);
        ch.setCuotaATEP(cuotaATEP);
        ch.setCuotaDesempleo(cuotaDesempleo);
        ch.setCuotaFogasa(cuotaFogasa);
        ch.setCuotaFP(cuotaFP);
        ch.setCuotaMEI(cuotaMEI);
        ch.setTipoATEPAplicado(tipoAtep);
        ch.setOrigenTipoATEP(origenTipoATEP);
        ch.setSsEmpresaBruta(ssEmpresaBruta);
        ch.setAhorroBonificaciones(ahorroBonificaciones);
        ch.setAhorroInvestigador(bonificaciones.getAhorroInvestigador());
        ch.setAhorroOtrasBonificaciones(bonificaciones.getAhorroOtrasBonificaciones());
        ch.setCosteSS(ssEmpresaNeta);

        if (horasAnuales.compareTo(BigDecimal.ZERO) > 0) {
            ch.setCosteHora(retribucionAnual.add(ssEmpresaNeta).divide(horasAnuales, SCALE, RoundingMode.HALF_UP));
        } else {
            ch.setCosteHora(BigDecimal.ZERO);
        }

        logger.info("Personal ID {} (legacy): costeHora={}, retrib={}, SS_neta={}, horas={}",
                personal.getIdPersona(), ch.getCosteHora(), retribucionAnual, ssEmpresaNeta, horasAnuales);
    }

    private Retribucion obtenerRetribucion(Personal personal) {
        Retribucion retribucion = personal.getRetribucion();
        if (retribucion == null) {
            throw new RuntimeException("No se encontró la retribución para el personal con ID: " + personal.getIdPersona());
        }
        return retribucion;
    }

    private BasesCotizacion obtenerBasesCotizacion(Personal personal) {
        BasesCotizacion basesCotizacion = personal.getBasesCotizacion();
        if (basesCotizacion == null) {
            throw new RuntimeException("No se encontró la base de cotización para el personal con ID: " + personal.getIdPersona());
        }
        return basesCotizacion;
    }

    private HorasPersonal obtenerHorasPersonal(Personal personal) {
        HorasPersonal horasPersonal = personal.getHorasPersonal();
        if (horasPersonal == null || horasPersonal.getHorasMaximasAnuales() == null) {
            throw new RuntimeException("No se encontraron las horas para el personal con ID: " + personal.getIdPersona());
        }
        return horasPersonal;
    }
}
