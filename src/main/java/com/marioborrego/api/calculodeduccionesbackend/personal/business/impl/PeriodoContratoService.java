package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveContrato;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveContratoRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BasesCotizacion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.PeriodoContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.NaturalezaContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TipoJornada;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.BasesCotizacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PeriodoContratoRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PersonalRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.periodosContrato.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PeriodoContratoService {

    private final PeriodoContratoRepository periodoContratoRepository;
    private final PersonalRepository personalRepository;
    private final ClaveContratoRepository claveContratoRepository;
    private final BasesCotizacionRepository basesCotizacionRepository;

    public PeriodoContratoService(PeriodoContratoRepository periodoContratoRepository,
                                  PersonalRepository personalRepository,
                                  ClaveContratoRepository claveContratoRepository,
                                  BasesCotizacionRepository basesCotizacionRepository) {
        this.periodoContratoRepository = periodoContratoRepository;
        this.personalRepository = personalRepository;
        this.claveContratoRepository = claveContratoRepository;
        this.basesCotizacionRepository = basesCotizacionRepository;
    }

    public Page<PeriodoContratoDTO> obtenerPeriodosPorEconomico(Long idEconomico, Pageable pageable) {
        return periodoContratoRepository.findByEconomico(idEconomico, pageable)
                .map(this::toDTO);
    }

    public List<PeriodoContratoDTO> obtenerPeriodosPorPersonal(Long idPersonal, Integer anioFiscal) {
        return periodoContratoRepository.findByPersonalIdPersonaAndAnioFiscalOrderByFechaAltaAsc(idPersonal, anioFiscal)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public PeriodoContratoDTO crearPeriodo(CrearPeriodoContratoDTO dto) {
        org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PeriodoContratoService.class);
        logger.info("Creando periodo de contrato para personal ID {}: clave={}, alta={}, baja={}",
                dto.getIdPersona(), dto.getClaveContrato(), dto.getFechaAlta(), dto.getFechaBaja());
        Personal personal = personalRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> new IllegalArgumentException("Trabajador no encontrado: " + dto.getIdPersona()));

        ClaveContrato clave = claveContratoRepository.findById(dto.getClaveContrato())
                .orElseThrow(() -> new IllegalArgumentException("Clave de contrato no encontrada: " + dto.getClaveContrato()));

        PeriodoContrato periodo = PeriodoContrato.builder()
                .personal(personal)
                .claveContrato(clave)
                .fechaAlta(dto.getFechaAlta())
                .fechaBaja(dto.getFechaBaja())
                .anioFiscal(dto.getAnioFiscal())
                .porcentajeJornada(dto.getPorcentajeJornada())
                .horasConvenio(dto.getHorasConvenio() != null ? dto.getHorasConvenio() : 1720L)
                .build();

        validarPeriodo(periodo, clave);

        List<PeriodoContrato> existentes = periodoContratoRepository
                .findByPersonalIdPersonaAndAnioFiscalOrderByFechaAltaAsc(dto.getIdPersona(), dto.getAnioFiscal());
        validarNoSolapamiento(periodo, existentes);

        // Auto-crear BasesCotizacion vinculada al periodo (solo para contratos estándar)
        NaturalezaContrato nat = clave.getNaturaleza();
        if (nat != NaturalezaContrato.FORMACION
                && nat != NaturalezaContrato.BECARIO_REMUNERADO
                && nat != NaturalezaContrato.BECARIO_NO_REMUNERADO) {
            BasesCotizacion bases = BasesCotizacion.builder()
                    .persona(personal)
                    .periodoContrato(periodo)
                    .build();
            periodo.setBasesCotizacionPeriodo(bases);
        }

        return toDTO(periodoContratoRepository.save(periodo));
    }

    @Transactional
    public PeriodoContratoDTO actualizarPeriodo(ActualizarPeriodoContratoDTO dto) {
        PeriodoContrato periodo = periodoContratoRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Período no encontrado: " + dto.getId()));

        if (dto.getClaveContrato() != null) {
            ClaveContrato clave = claveContratoRepository.findById(dto.getClaveContrato())
                    .orElseThrow(() -> new IllegalArgumentException("Clave de contrato no encontrada: " + dto.getClaveContrato()));
            periodo.setClaveContrato(clave);
        }

        boolean fechasCambiaron = false;
        if (dto.getFechaAlta() != null) {
            fechasCambiaron = !dto.getFechaAlta().equals(periodo.getFechaAlta());
            periodo.setFechaAlta(dto.getFechaAlta());
        }
        if (dto.getFechaBaja() != null) {
            fechasCambiaron = fechasCambiaron || !dto.getFechaBaja().equals(periodo.getFechaBaja());
            periodo.setFechaBaja(dto.getFechaBaja());
        }
        if (dto.getPorcentajeJornada() != null) periodo.setPorcentajeJornada(dto.getPorcentajeJornada());
        if (dto.getHorasConvenio() != null) periodo.setHorasConvenio(dto.getHorasConvenio());

        validarPeriodo(periodo, periodo.getClaveContrato());

        List<PeriodoContrato> existentes = periodoContratoRepository
                .findByPersonalIdPersonaAndAnioFiscalOrderByFechaAltaAsc(
                        periodo.getPersonal().getIdPersona(), periodo.getAnioFiscal())
                .stream().filter(p -> !p.getId().equals(periodo.getId())).toList();
        validarNoSolapamiento(periodo, existentes);

        // Anular BBCC de meses fuera del nuevo rango de fechas
        if (fechasCambiaron && periodo.getBasesCotizacionPeriodo() != null) {
            int anio = periodo.getAnioFiscal();
            LocalDate inicioAnio = LocalDate.of(anio, 1, 1);
            LocalDate finAnio = LocalDate.of(anio, 12, 31);
            LocalDate desde = periodo.getFechaAlta().isBefore(inicioAnio) ? inicioAnio : periodo.getFechaAlta();
            LocalDate hasta = periodo.getFechaBaja() != null
                    ? (periodo.getFechaBaja().isAfter(finAnio) ? finAnio : periodo.getFechaBaja())
                    : finAnio;
            periodo.getBasesCotizacionPeriodo().anularBasesForaDeRango(desde, hasta, anio);
        }

        return toDTO(periodoContratoRepository.save(periodo));
    }

    @Transactional
    public void eliminarPeriodo(Long idPeriodo) {
        // La BasesCotizacion vinculada se elimina por cascade (orphanRemoval)
        periodoContratoRepository.deleteById(idPeriodo);
    }

    public List<ClaveContratoDTO> obtenerClavesContratoVigentes() {
        return claveContratoRepository.findByVigenteTrueOrderByClaveAsc()
                .stream().map(this::toClaveDTO).toList();
    }

    private void validarPeriodo(PeriodoContrato periodo, ClaveContrato clave) {
        if (periodo.getFechaBaja() != null && periodo.getFechaBaja().isBefore(periodo.getFechaAlta())) {
            throw new IllegalArgumentException("La fecha de baja debe ser igual o posterior a la fecha de alta");
        }

        BigDecimal pj = periodo.getPorcentajeJornada();
        if (pj.compareTo(new BigDecimal("0.01")) < 0 || pj.compareTo(new BigDecimal("100.00")) > 0) {
            throw new IllegalArgumentException("El porcentaje de jornada debe estar entre 0,01 y 100,00");
        }

        if (clave.getJornada() == TipoJornada.TIEMPO_COMPLETO && pj.compareTo(new BigDecimal("100.00")) != 0) {
            throw new IllegalArgumentException("Un contrato a tiempo completo requiere porcentaje de jornada 100%");
        }

        if (clave.getJornada() == TipoJornada.TIEMPO_PARCIAL && pj.compareTo(new BigDecimal("100.00")) >= 0) {
            throw new IllegalArgumentException("Un contrato a tiempo parcial requiere porcentaje de jornada inferior a 100%");
        }
    }

    private void validarNoSolapamiento(PeriodoContrato nuevo, List<PeriodoContrato> existentes) {
        LocalDate nuevoInicio = nuevo.getFechaAlta();
        LocalDate nuevoFin = nuevo.getFechaBaja() != null ? nuevo.getFechaBaja()
                : LocalDate.of(nuevo.getAnioFiscal(), 12, 31);

        for (PeriodoContrato existente : existentes) {
            LocalDate exInicio = existente.getFechaAlta();
            LocalDate exFin = existente.getFechaBaja() != null ? existente.getFechaBaja()
                    : LocalDate.of(existente.getAnioFiscal(), 12, 31);

            if (!nuevoInicio.isAfter(exFin) && !nuevoFin.isBefore(exInicio)) {
                throw new IllegalArgumentException(
                        "El período se solapa con otro existente (" + exInicio + " - " + exFin + ")");
            }
        }
    }

    private PeriodoContratoDTO toDTO(PeriodoContrato periodo) {
        ClaveContrato clave = periodo.getClaveContrato();
        Personal personal = periodo.getPersonal();
        Integer anioFiscal = periodo.getAnioFiscal();

        // Cálculo de horas hábiles (pro-rata año fiscal)
        long diasAnualidad = Year.of(anioFiscal).isLeap() ? 366 : 365;
        BigDecimal diasAnualidadDecimal = BigDecimal.valueOf(diasAnualidad);
        BigDecimal CIEN = new BigDecimal("100");

        LocalDate inicioAnio = LocalDate.of(anioFiscal, 1, 1);
        LocalDate finAnio = LocalDate.of(anioFiscal, 12, 31);

        LocalDate fechaInicioEfectiva = periodo.getFechaAlta().isBefore(inicioAnio) ? inicioAnio : periodo.getFechaAlta();
        LocalDate fechaFinEfectiva = periodo.getFechaBaja() != null
                ? (periodo.getFechaBaja().isAfter(finAnio) ? finAnio : periodo.getFechaBaja())
                : finAnio;

        long horasHabiles = 0L;
        if (!fechaInicioEfectiva.isAfter(fechaFinEfectiva)) {
            long diasPeriodo = ChronoUnit.DAYS.between(fechaInicioEfectiva, fechaFinEfectiva) + 1;
            BigDecimal horasDia = BigDecimal.valueOf(periodo.getHorasConvenio()).divide(diasAnualidadDecimal, 6, RoundingMode.HALF_UP);
            BigDecimal horasPeriodo = horasDia.multiply(BigDecimal.valueOf(diasPeriodo))
                    .multiply(periodo.getPorcentajeJornada())
                    .divide(CIEN, 2, RoundingMode.HALF_UP);
            horasHabiles = horasPeriodo.setScale(0, RoundingMode.HALF_UP).longValue();
        }

        return PeriodoContratoDTO.builder()
                .id(periodo.getId())
                .idPersona(personal.getIdPersona())
                .nombre(personal.getNombre() + " " + personal.getApellidos())
                .dni(personal.getDni())
                .claveContrato(clave.getClave())
                .descripcionContrato(clave.getDescripcion())
                .naturaleza(clave.getNaturaleza())
                .jornada(clave.getJornada())
                .fechaAlta(periodo.getFechaAlta())
                .fechaBaja(periodo.getFechaBaja())
                .anioFiscal(anioFiscal)
                .porcentajeJornada(periodo.getPorcentajeJornada())
                .horasConvenio(periodo.getHorasConvenio())
                .horasHabiles(horasHabiles)
                .build();
    }

    private ClaveContratoDTO toClaveDTO(ClaveContrato clave) {
        return ClaveContratoDTO.builder()
                .clave(clave.getClave())
                .descripcion(clave.getDescripcion())
                .naturaleza(clave.getNaturaleza())
                .jornada(clave.getJornada())
                .cotizaDesempleo(clave.getCotizaDesempleo())
                .cotizaFogasa(clave.getCotizaFogasa())
                .cotizaFp(clave.getCotizaFp())
                .cotizaMei(clave.getCotizaMei())
                .cotizaCcEstandar(clave.getCotizaCcEstandar())
                .vigente(clave.getVigente())
                .build();
    }
}
