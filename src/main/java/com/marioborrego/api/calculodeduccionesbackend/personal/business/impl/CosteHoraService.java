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
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.BasesCotizacionRepository;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class CosteHoraService {
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final int SCALE = 6;
    private static final BigDecimal FACTOR_REDUCCION_CUOTA_FIJA = new BigDecimal("0.05");
    private static final BigDecimal CUOTA_CC_BECARIO_NO_REMUNERADO_DIARIA = new BigDecimal("2.67");
    private static final BigDecimal CUOTA_ATEP_BECARIO_NO_REMUNERADO_DIARIA = new BigDecimal("0.33");
    private static final BigDecimal CUOTA_CC_FORMACION_MENSUAL = new BigDecimal("60.76");
    private static final BigDecimal CUOTA_ATEP_FORMACION_MENSUAL = new BigDecimal("7.38");
    private static final BigDecimal DIAS_EQUIVALENTE_MES = new BigDecimal("30.42");

    private final PersonalRepository personalRepository;
    private final ConfiguracionAnualSSRepository configuracionAnualSSRepository;
    private final TarifaPrimasCnaeRepository tarifaPrimasCnaeRepository;
    private final ClaveOcupacionRepository claveOcupacionRepository;
    private final BonificacionService bonificacionService;
    private final PeriodoContratoRepository periodoContratoRepository;
    private final BasesCotizacionRepository basesCotizacionRepository;

    private final Logger logger = LoggerFactory.getLogger(CosteHoraService.class);

    public CosteHoraService(PersonalRepository personalRepository,
                            ConfiguracionAnualSSRepository configuracionAnualSSRepository,
                            TarifaPrimasCnaeRepository tarifaPrimasCnaeRepository,
                            ClaveOcupacionRepository claveOcupacionRepository,
                            BonificacionService bonificacionService,
                            PeriodoContratoRepository periodoContratoRepository,
                            BasesCotizacionRepository basesCotizacionRepository) {
        this.personalRepository = personalRepository;
        this.configuracionAnualSSRepository = configuracionAnualSSRepository;
        this.tarifaPrimasCnaeRepository = tarifaPrimasCnaeRepository;
        this.claveOcupacionRepository = claveOcupacionRepository;
        this.bonificacionService = bonificacionService;
        this.periodoContratoRepository = periodoContratoRepository;
        this.basesCotizacionRepository = basesCotizacionRepository;
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
            // Sin períodos de contrato: no se puede calcular coste/hora
            ch.setRetribucionTotal(retribucionAnual);
            ch.setHorasMaximas(BigDecimal.ZERO);
            ch.setCosteHora(BigDecimal.ZERO);
            ch.setCuotaCC(BigDecimal.ZERO);
            ch.setCuotaATEP(BigDecimal.ZERO);
            ch.setCuotaDesempleo(BigDecimal.ZERO);
            ch.setCuotaFogasa(BigDecimal.ZERO);
            ch.setCuotaFP(BigDecimal.ZERO);
            ch.setCuotaMEI(BigDecimal.ZERO);
            ch.setSsEmpresaBruta(BigDecimal.ZERO);
            ch.setCosteSS(BigDecimal.ZERO);
            ch.setAhorroBonificaciones(BigDecimal.ZERO);
            ch.setAhorroInvestigador(BigDecimal.ZERO);
            ch.setAhorroOtrasBonificaciones(BigDecimal.ZERO);
            ch.setTipoATEPAplicado(tipoAtep);
            ch.setOrigenTipoATEP(origenTipoATEP);
            logger.info("Personal ID {} sin períodos de contrato: costeHora=0", personal.getIdPersona());
        } else {
            calcularConPeriodos(personal, ch, config, tipoAtep, origenTipoATEP, retribucionAnual,
                    periodos, anualidad);
        }

        personal.setCosteHoraPersonal(ch);
        personalRepository.save(personal);
        logger.info("Cálculo del coste por hora completado para el personal con ID: {}", personal.getIdPersona());
    }

    private void calcularConPeriodos(Personal personal, CosteHoraPersonal ch,
                                     ConfiguracionAnualSS config, BigDecimal tipoAtep,
                                     String origenTipoATEP, BigDecimal retribucionAnual,
                                     List<PeriodoContrato> periodos,
                                     int anualidad) {
        int diasDelAnio = Year.of(anualidad).length();
        BigDecimal diasDelAnioDecimal = BigDecimal.valueOf(diasDelAnio);

        BigDecimal totalCuotaCc = BigDecimal.ZERO;
        BigDecimal totalCuotaAtep = BigDecimal.ZERO;
        BigDecimal totalCuotaDesempleo = BigDecimal.ZERO;
        BigDecimal totalCuotaFogasa = BigDecimal.ZERO;
        BigDecimal totalCuotaFp = BigDecimal.ZERO;
        BigDecimal totalCuotaMei = BigDecimal.ZERO;
        BigDecimal totalHorasEfectivas = BigDecimal.ZERO;
        List<TramoCotizacion> tramosCotizacion = new ArrayList<>();

        LocalDate inicioAnio = LocalDate.of(anualidad, 1, 1);
        LocalDate finAnio = LocalDate.of(anualidad, 12, 31);
        List<BonificacionesTrabajador> bonificacionesTrabajador = bonificacionService.obtenerBonificaciones(
                personal.getIdPersona(), anualidad);

        for (PeriodoContrato periodo : periodos) {
            ClaveContrato clave = periodo.getClaveContrato();

            // Fechas efectivas dentro del año fiscal
            LocalDate fechaInicio = periodo.getFechaAlta().isBefore(inicioAnio) ? inicioAnio : periodo.getFechaAlta();
            LocalDate fechaFin = periodo.getFechaBaja() != null
                    ? (periodo.getFechaBaja().isAfter(finAnio) ? finAnio : periodo.getFechaBaja())
                    : finAnio;

            if (fechaFin.isBefore(fechaInicio)) {
                continue;
            }

            long diasPeriodo = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
            BigDecimal diasPeriodoDecimal = BigDecimal.valueOf(diasPeriodo);

            // Horas efectivas de este período (cada período tiene sus propias horas convenio)
            BigDecimal horasConvenioPeriodo = BigDecimal.valueOf(periodo.getHorasConvenio());
            BigDecimal horasPeriodo = horasConvenioPeriodo
                    .multiply(diasPeriodoDecimal)
                    .divide(diasDelAnioDecimal, SCALE, RoundingMode.HALF_UP)
                    .multiply(periodo.getPorcentajeJornada())
                    .divide(CIEN, SCALE, RoundingMode.HALF_UP);
            totalHorasEfectivas = totalHorasEfectivas.add(horasPeriodo);

            for (TramoCotizacion tramo : construirTramosCotizacion(periodo, fechaInicio, fechaFin, config, tipoAtep, personal)) {
                tramosCotizacion.add(tramo);
                totalCuotaCc = totalCuotaCc.add(tramo.cuotaCc());
                totalCuotaAtep = totalCuotaAtep.add(tramo.cuotaAtep());
                totalCuotaDesempleo = totalCuotaDesempleo.add(tramo.cuotaDesempleo());
                totalCuotaFogasa = totalCuotaFogasa.add(tramo.cuotaFogasa());
                totalCuotaFp = totalCuotaFp.add(tramo.cuotaFp());
                totalCuotaMei = totalCuotaMei.add(tramo.cuotaMei());
            }
        }

        // PASO: Restar horas de baja
        BigDecimal totalHorasBaja = BigDecimal.ZERO;
        for (BajaLaboral baja : personal.getBajasLaborales()) {
            LocalDate fechaInicioBaja = baja.getFechaInicio().isBefore(inicioAnio) ? inicioAnio : baja.getFechaInicio();
            LocalDate fechaFinBaja = baja.getFechaFin() == null || baja.getFechaFin().isAfter(finAnio) ? finAnio : baja.getFechaFin();

            if (!fechaInicioBaja.isAfter(fechaFinBaja)) {
                for (PeriodoContrato pc : periodos) {
                    LocalDate pcInicio = pc.getFechaAlta().isBefore(inicioAnio) ? inicioAnio : pc.getFechaAlta();
                    LocalDate pcFin = pc.getFechaBaja() == null || pc.getFechaBaja().isAfter(finAnio) ? finAnio : pc.getFechaBaja();

                    LocalDate overlapInicio = pcInicio.isAfter(fechaInicioBaja) ? pcInicio : fechaInicioBaja;
                    LocalDate overlapFin = pcFin.isBefore(fechaFinBaja) ? pcFin : fechaFinBaja;

                    if (!overlapInicio.isAfter(overlapFin)) {
                        long diasSolapados = ChronoUnit.DAYS.between(overlapInicio, overlapFin) + 1;
                        BigDecimal horasDia = BigDecimal.valueOf(pc.getHorasConvenio()).divide(diasDelAnioDecimal, 6, RoundingMode.HALF_UP);
                        BigDecimal horasBajaPc = horasDia.multiply(BigDecimal.valueOf(diasSolapados))
                                .multiply(pc.getPorcentajeJornada())
                                .divide(CIEN, SCALE, RoundingMode.HALF_UP);
                        totalHorasBaja = totalHorasBaja.add(horasBajaPc);
                    }
                }
            }
        }
        BigDecimal totalHorasTrabajadas = totalHorasEfectivas;
        totalHorasEfectivas = totalHorasTrabajadas.subtract(totalHorasBaja);

        // PASO 6: SS empresa bruta
        BigDecimal ssEmpresaBruta = totalCuotaCc.add(totalCuotaAtep).add(totalCuotaDesempleo)
                .add(totalCuotaFogasa).add(totalCuotaFp).add(totalCuotaMei);

        // PASO 7: Bonificaciones
        BonificacionResultDTO bonificaciones = calcularBonificacionesSobreTramos(
                bonificacionesTrabajador,
                tramosCotizacion
        );
        BigDecimal ahorroInvestigador = bonificaciones.getAhorroInvestigador();
        BigDecimal ahorroOtrasBonificaciones = bonificaciones.getAhorroOtrasBonificaciones();
        BigDecimal ahorroBonificaciones = ahorroInvestigador.add(ahorroOtrasBonificaciones);

        // PASO 8: SS empresa neta
        BigDecimal ssEmpresaNeta = ssEmpresaBruta.subtract(ahorroBonificaciones).max(BigDecimal.ZERO);

        // Guardar desglose
        ch.setRetribucionTotal(retribucionAnual);
        ch.setHorasMaximas(totalHorasTrabajadas); // Horas antes de restar bajas
        ch.setHorasEfectivas(totalHorasEfectivas); // Denominador real (trabajadas - bajas)
        ch.setHorasBaja(totalHorasBaja);
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
        ch.setAhorroInvestigador(ahorroInvestigador);
        ch.setAhorroOtrasBonificaciones(ahorroOtrasBonificaciones);
        ch.setCosteSS(ssEmpresaNeta);

        // PASO 9: Coste/hora (denominador = horas de periodos de alta)
        if (totalHorasTrabajadas.compareTo(BigDecimal.ZERO) > 0) {
            ch.setCosteHora(retribucionAnual.add(ssEmpresaNeta).divide(totalHorasTrabajadas, 2, RoundingMode.HALF_UP));
        } else {
            ch.setCosteHora(BigDecimal.ZERO);
        }

        logger.info("Personal ID {} (períodos): costeHora={}, retrib={}, SS_neta={}, horasAlta={}, horasEfectivas={}",
                personal.getIdPersona(), ch.getCosteHora(), retribucionAnual, ssEmpresaNeta, totalHorasTrabajadas, totalHorasEfectivas);
    }

    private BonificacionResultDTO calcularBonificacionesSobreTramos(List<BonificacionesTrabajador> bonificaciones,
                                                                    List<TramoCotizacion> tramosCotizacion) {
        if (bonificaciones == null || bonificaciones.isEmpty() || tramosCotizacion.isEmpty()) {
            return BonificacionResultDTO.builder()
                    .ahorroTotalAnual(BigDecimal.ZERO)
                    .ahorroInvestigador(BigDecimal.ZERO)
                    .ahorroOtrasBonificaciones(BigDecimal.ZERO)
                    .detalles(List.of())
                    .build();
        }

        BigDecimal ahorroInvestigador = BigDecimal.ZERO;
        BigDecimal ahorroOtrasBonificaciones = BigDecimal.ZERO;

        for (BonificacionesTrabajador bonificacion : bonificaciones) {
            BigDecimal baseBonificable = BigDecimal.ZERO;

            for (TramoCotizacion tramo : tramosCotizacion) {
                LocalDate inicioSolape = bonificacion.getFechaInicio().isAfter(tramo.fechaInicio())
                        ? bonificacion.getFechaInicio()
                        : tramo.fechaInicio();
                LocalDate finSolape = bonificacion.getFechaFin().isBefore(tramo.fechaFin())
                        ? bonificacion.getFechaFin()
                        : tramo.fechaFin();

                if (finSolape.isBefore(inicioSolape)) {
                    continue;
                }

                BigDecimal diasSolape = BigDecimal.valueOf(ChronoUnit.DAYS.between(inicioSolape, finSolape) + 1);
                BigDecimal diasTramo = BigDecimal.valueOf(ChronoUnit.DAYS.between(tramo.fechaInicio(), tramo.fechaFin()) + 1);
                BigDecimal proporcionSolape = diasSolape.divide(diasTramo, SCALE, RoundingMode.HALF_UP);
                BigDecimal baseTramo = bonificacion.getTipoBonificacion() == TiposBonificacion.BONIFICACION_PERSONAL_INVESTIGADOR
                        ? tramo.cuotaCc()
                        : tramo.totalSs();
                baseBonificable = baseBonificable.add(baseTramo.multiply(proporcionSolape));
            }

            BigDecimal ahorroBonificacion = baseBonificable
                    .multiply(bonificacion.getPorcentajeBonificacion())
                    .divide(CIEN, 2, RoundingMode.HALF_UP);

            if (bonificacion.getTipoBonificacion() == TiposBonificacion.BONIFICACION_PERSONAL_INVESTIGADOR) {
                ahorroInvestigador = ahorroInvestigador.add(ahorroBonificacion);
            } else {
                ahorroOtrasBonificaciones = ahorroOtrasBonificaciones.add(ahorroBonificacion);
            }
        }

        return BonificacionResultDTO.builder()
                .ahorroTotalAnual(ahorroInvestigador.add(ahorroOtrasBonificaciones))
                .ahorroInvestigador(ahorroInvestigador)
                .ahorroOtrasBonificaciones(ahorroOtrasBonificaciones)
                .detalles(List.of())
                .build();
    }

    private List<TramoCotizacion> construirTramosCotizacion(PeriodoContrato periodo,
                                                            LocalDate fechaInicio,
                                                            LocalDate fechaFin,
                                                            ConfiguracionAnualSS config,
                                                            BigDecimal tipoAtep,
                                                            Personal personal) {
        NaturalezaContrato naturaleza = periodo.getClaveContrato().getNaturaleza();
        if (naturaleza == NaturalezaContrato.BECARIO_NO_REMUNERADO) {
            return construirTramosCuotaFijaDiaria(fechaInicio, fechaFin);
        }
        if (naturaleza == NaturalezaContrato.BECARIO_REMUNERADO || naturaleza == NaturalezaContrato.FORMACION) {
            return construirTramosCuotaFijaMensual(fechaInicio, fechaFin);
        }
        return construirTramosEstandar(periodo, fechaInicio, fechaFin, config, tipoAtep, personal);
    }

    private List<TramoCotizacion> construirTramosCuotaFijaDiaria(LocalDate fechaInicio, LocalDate fechaFin) {
        List<TramoCotizacion> tramos = new ArrayList<>();
        LocalDate cursor = fechaInicio.withDayOfMonth(1);
        while (!cursor.isAfter(fechaFin)) {
            LocalDate inicioTramo = fechaInicio.isAfter(cursor) ? fechaInicio : cursor;
            LocalDate finMes = cursor.withDayOfMonth(cursor.lengthOfMonth());
            LocalDate finTramo = fechaFin.isBefore(finMes) ? fechaFin : finMes;
            BigDecimal diasTramo = BigDecimal.valueOf(ChronoUnit.DAYS.between(inicioTramo, finTramo) + 1);
            tramos.add(new TramoCotizacion(
                    inicioTramo,
                    finTramo,
                    CUOTA_CC_BECARIO_NO_REMUNERADO_DIARIA.multiply(diasTramo).multiply(FACTOR_REDUCCION_CUOTA_FIJA),
                    CUOTA_ATEP_BECARIO_NO_REMUNERADO_DIARIA.multiply(diasTramo),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            ));
            cursor = cursor.plusMonths(1);
        }
        return tramos;
    }

    private List<TramoCotizacion> construirTramosCuotaFijaMensual(LocalDate fechaInicio, LocalDate fechaFin) {
        List<TramoCotizacion> tramos = new ArrayList<>();
        LocalDate cursor = fechaInicio.withDayOfMonth(1);
        while (!cursor.isAfter(fechaFin)) {
            LocalDate inicioTramo = fechaInicio.isAfter(cursor) ? fechaInicio : cursor;
            LocalDate finMes = cursor.withDayOfMonth(cursor.lengthOfMonth());
            LocalDate finTramo = fechaFin.isBefore(finMes) ? fechaFin : finMes;
            BigDecimal diasTramo = BigDecimal.valueOf(ChronoUnit.DAYS.between(inicioTramo, finTramo) + 1);
            BigDecimal mesesEquivalentes = diasTramo.divide(DIAS_EQUIVALENTE_MES, SCALE, RoundingMode.HALF_UP);
            tramos.add(new TramoCotizacion(
                    inicioTramo,
                    finTramo,
                    CUOTA_CC_FORMACION_MENSUAL.multiply(mesesEquivalentes).multiply(FACTOR_REDUCCION_CUOTA_FIJA),
                    CUOTA_ATEP_FORMACION_MENSUAL.multiply(mesesEquivalentes),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            ));
            cursor = cursor.plusMonths(1);
        }
        return tramos;
    }

    private List<TramoCotizacion> construirTramosEstandar(PeriodoContrato periodo,
                                                          LocalDate fechaInicio,
                                                          LocalDate fechaFin,
                                                          ConfiguracionAnualSS config,
                                                          BigDecimal tipoAtep,
                                                          Personal personal) {
        BasesCotizacion basesPeriodo = basesCotizacionRepository.findByPeriodoContratoId(periodo.getId())
                .orElse(null);
        if (basesPeriodo == null) {
            logger.warn("No se encontraron bases de cotización para el periodo ID {} del personal ID {}",
                    periodo.getId(), personal.getIdPersona());
            return List.of(new TramoCotizacion(
                    fechaInicio,
                    fechaFin,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            ));
        }

        List<TramoCotizacion> tramos = new ArrayList<>();
        ClaveContrato clave = periodo.getClaveContrato();
        NaturalezaContrato naturaleza = clave.getNaturaleza();
        LocalDate cursor = fechaInicio.withDayOfMonth(1);

        while (!cursor.isAfter(fechaFin)) {
            LocalDate inicioTramo = fechaInicio.isAfter(cursor) ? fechaInicio : cursor;
            LocalDate finMes = cursor.withDayOfMonth(cursor.lengthOfMonth());
            LocalDate finTramo = fechaFin.isBefore(finMes) ? fechaFin : finMes;
            BigDecimal baseCcMes = BigDecimal.valueOf(basesPeriodo.getBaseCotizacionContingenciasComunesMes(cursor.getMonthValue()));
            BigDecimal baseCpMes = baseCcMes;
            BigDecimal cuotaCcMes = baseCcMes.multiply(config.getCcEmpresa()).divide(CIEN, SCALE, RoundingMode.HALF_UP);
            BigDecimal cuotaAtepMes = baseCpMes.multiply(tipoAtep).divide(CIEN, SCALE, RoundingMode.HALF_UP);
            BigDecimal cuotaDesempleoMes = BigDecimal.ZERO;
            BigDecimal cuotaFogasaMes = BigDecimal.ZERO;
            BigDecimal cuotaFpMes = BigDecimal.ZERO;
            BigDecimal cuotaMeiMes = BigDecimal.ZERO;

            if (clave.getCotizaDesempleo()) {
                BigDecimal tipoDesempleo = naturaleza == NaturalezaContrato.TEMPORAL
                        ? config.getDesempleoEmpresaTemporal()
                        : config.getDesempleoEmpresaIndefinido();
                cuotaDesempleoMes = baseCpMes.multiply(tipoDesempleo).divide(CIEN, SCALE, RoundingMode.HALF_UP);
            }
            if (clave.getCotizaFogasa()) {
                cuotaFogasaMes = baseCpMes.multiply(config.getFogasa()).divide(CIEN, SCALE, RoundingMode.HALF_UP);
            }
            if (clave.getCotizaFp()) {
                cuotaFpMes = baseCpMes.multiply(config.getFpEmpresa()).divide(CIEN, SCALE, RoundingMode.HALF_UP);
            }
            if (clave.getCotizaMei()) {
                cuotaMeiMes = baseCcMes.multiply(config.getMeiEmpresa()).divide(CIEN, SCALE, RoundingMode.HALF_UP);
            }

            tramos.add(new TramoCotizacion(
                    inicioTramo,
                    finTramo,
                    cuotaCcMes,
                    cuotaAtepMes,
                    cuotaDesempleoMes,
                    cuotaFogasaMes,
                    cuotaFpMes,
                    cuotaMeiMes
            ));
            cursor = cursor.plusMonths(1);
        }

        return tramos;
    }

    private Retribucion obtenerRetribucion(Personal personal) {
        Retribucion retribucion = personal.getRetribucion();
        if (retribucion == null) {
            throw new RuntimeException("No se encontró la retribución para el personal con ID: " + personal.getIdPersona());
        }
        return retribucion;
    }

    private record TramoCotizacion(LocalDate fechaInicio,
                                   LocalDate fechaFin,
                                   BigDecimal cuotaCc,
                                   BigDecimal cuotaAtep,
                                   BigDecimal cuotaDesempleo,
                                   BigDecimal cuotaFogasa,
                                   BigDecimal cuotaFp,
                                   BigDecimal cuotaMei) {
        private BigDecimal totalSs() {
            return cuotaCc
                    .add(cuotaAtep)
                    .add(cuotaDesempleo)
                    .add(cuotaFogasa)
                    .add(cuotaFp)
                    .add(cuotaMei);
        }
    }
}
