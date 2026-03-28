package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveContrato;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveContratoRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.PeriodoContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TipoJornada;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PeriodoContratoRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PersonalRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.periodosContrato.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PeriodoContratoService {

    private final PeriodoContratoRepository periodoContratoRepository;
    private final PersonalRepository personalRepository;
    private final ClaveContratoRepository claveContratoRepository;

    public PeriodoContratoService(PeriodoContratoRepository periodoContratoRepository,
                                  PersonalRepository personalRepository,
                                  ClaveContratoRepository claveContratoRepository) {
        this.periodoContratoRepository = periodoContratoRepository;
        this.personalRepository = personalRepository;
        this.claveContratoRepository = claveContratoRepository;
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
                .baseCcMensual(dto.getBaseCcMensual() != null ? dto.getBaseCcMensual() : BigDecimal.ZERO)
                .baseCpMensual(dto.getBaseCpMensual() != null ? dto.getBaseCpMensual() : BigDecimal.ZERO)
                .build();

        validarPeriodo(periodo, clave);

        List<PeriodoContrato> existentes = periodoContratoRepository
                .findByPersonalIdPersonaAndAnioFiscalOrderByFechaAltaAsc(dto.getIdPersona(), dto.getAnioFiscal());
        validarNoSolapamiento(periodo, existentes);

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

        if (dto.getFechaAlta() != null) periodo.setFechaAlta(dto.getFechaAlta());
        if (dto.getFechaBaja() != null) periodo.setFechaBaja(dto.getFechaBaja());
        if (dto.getPorcentajeJornada() != null) periodo.setPorcentajeJornada(dto.getPorcentajeJornada());
        if (dto.getBaseCcMensual() != null) periodo.setBaseCcMensual(dto.getBaseCcMensual());
        if (dto.getBaseCpMensual() != null) periodo.setBaseCpMensual(dto.getBaseCpMensual());

        validarPeriodo(periodo, periodo.getClaveContrato());

        List<PeriodoContrato> existentes = periodoContratoRepository
                .findByPersonalIdPersonaAndAnioFiscalOrderByFechaAltaAsc(
                        periodo.getPersonal().getIdPersona(), periodo.getAnioFiscal())
                .stream().filter(p -> !p.getId().equals(periodo.getId())).toList();
        validarNoSolapamiento(periodo, existentes);

        return toDTO(periodoContratoRepository.save(periodo));
    }

    @Transactional
    public void eliminarPeriodo(Long idPeriodo) {
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

        if (clave.getCotizaCcEstandar()) {
            if (periodo.getBaseCcMensual().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("La base CC mensual debe ser mayor que 0 para este tipo de contrato");
            }
            if (periodo.getBaseCpMensual().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("La base CP mensual debe ser mayor que 0 para este tipo de contrato");
            }
            if (periodo.getBaseCpMensual().compareTo(periodo.getBaseCcMensual()) < 0) {
                throw new IllegalArgumentException("La base CP mensual no puede ser inferior a la base CC mensual");
            }
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
                .anioFiscal(periodo.getAnioFiscal())
                .porcentajeJornada(periodo.getPorcentajeJornada())
                .baseCcMensual(periodo.getBaseCcMensual())
                .baseCpMensual(periodo.getBaseCpMensual())
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
