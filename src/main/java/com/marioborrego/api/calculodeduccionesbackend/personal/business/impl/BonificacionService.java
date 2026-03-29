package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BonificacionesTrabajador;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.BonificacionesTrabajadorRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.BonificacionResultDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.DetalleBonificacionDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class BonificacionService {
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final int SCALE = 6;

    private final BonificacionesTrabajadorRepository bonificacionRepository;

    public BonificacionService(BonificacionesTrabajadorRepository bonificacionRepository) {
        this.bonificacionRepository = bonificacionRepository;
    }

    /**
     * Calcula el ahorro total en SS por bonificaciones para un trabajador en un año.
     *
     * Las bonificaciones de personal investigador se aplican solo sobre la cuota de CC.
     * El resto de bonificaciones se aplica sobre el total de SS empresa.
     */
    public BonificacionResultDTO calcularAhorroBonificaciones(
            Long idPersonal,
            BigDecimal cuotaCcEmpresa,
            BigDecimal ssEmpresa,
            Integer anioFiscal
    ) {
        return calcularAhorroBonificacionesEnRango(
                obtenerBonificaciones(idPersonal, anioFiscal),
                cuotaCcEmpresa,
                ssEmpresa,
                LocalDate.of(anioFiscal, 1, 1),
                LocalDate.of(anioFiscal, 12, 31)
        );
    }

    public List<BonificacionesTrabajador> obtenerBonificaciones(Long idPersonal, Integer anioFiscal) {
        return bonificacionRepository.findByPersonalIdPersonaAndAnioFiscal(idPersonal, anioFiscal);
    }

    public BonificacionResultDTO calcularAhorroBonificacionesEnRango(
            List<BonificacionesTrabajador> bonificaciones,
            BigDecimal cuotaCcEmpresa,
            BigDecimal ssEmpresa,
            LocalDate fechaInicio,
            LocalDate fechaFin
    ) {
        if (bonificaciones == null || bonificaciones.isEmpty() || fechaFin.isBefore(fechaInicio)) {
            return BonificacionResultDTO.builder()
                    .ahorroTotalAnual(BigDecimal.ZERO)
                    .ahorroInvestigador(BigDecimal.ZERO)
                    .ahorroOtrasBonificaciones(BigDecimal.ZERO)
                    .detalles(List.of())
                    .build();
        }

        BigDecimal ahorroTotalInvestigador = BigDecimal.ZERO;
        BigDecimal ahorroTotalOtras = BigDecimal.ZERO;
        List<DetalleBonificacionDTO> detalles = new ArrayList<>();
        BigDecimal diasRango = BigDecimal.valueOf(ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1);

        for (BonificacionesTrabajador bonif : bonificaciones) {
            LocalDate inicioEfectivo = bonif.getFechaInicio().isAfter(fechaInicio)
                    ? bonif.getFechaInicio()
                    : fechaInicio;
            LocalDate finEfectivo = bonif.getFechaFin().isBefore(fechaFin)
                    ? bonif.getFechaFin()
                    : fechaFin;

            if (finEfectivo.isBefore(inicioEfectivo)) {
                continue;
            }

            BigDecimal diasBonificados = BigDecimal.valueOf(
                    ChronoUnit.DAYS.between(inicioEfectivo, finEfectivo) + 1);
            BigDecimal proporcionRango = diasBonificados.divide(diasRango, SCALE, RoundingMode.HALF_UP);
            BigDecimal baseAhorro = bonif.getTipoBonificacion() == TiposBonificacion.BONIFICACION_PERSONAL_INVESTIGADOR
                    ? cuotaCcEmpresa
                    : ssEmpresa;

            BigDecimal ahorroPeriodo = baseAhorro
                    .multiply(proporcionRango)
                    .multiply(bonif.getPorcentajeBonificacion())
                    .divide(CIEN, 2, RoundingMode.HALF_UP);

            if (bonif.getTipoBonificacion() == TiposBonificacion.BONIFICACION_PERSONAL_INVESTIGADOR) {
                ahorroTotalInvestigador = ahorroTotalInvestigador.add(ahorroPeriodo);
            } else {
                ahorroTotalOtras = ahorroTotalOtras.add(ahorroPeriodo);
            }

            detalles.add(DetalleBonificacionDTO.builder()
                    .idBonificacion(bonif.getIdBonificacionTrabajador())
                    .tipoBonificacion(bonif.getTipoBonificacion().name())
                    .porcentaje(bonif.getPorcentajeBonificacion())
                    .fechaInicio(inicioEfectivo)
                    .fechaFin(finEfectivo)
                    .diasBonificados(diasBonificados.intValue())
                    .ahorroPeriodo(ahorroPeriodo)
                    .descripcion(bonif.getDescripcion())
                    .build());
        }

        return BonificacionResultDTO.builder()
                .ahorroTotalAnual(ahorroTotalInvestigador.add(ahorroTotalOtras))
                .ahorroInvestigador(ahorroTotalInvestigador)
                .ahorroOtrasBonificaciones(ahorroTotalOtras)
                .detalles(detalles)
                .build();
    }

    /**
     * Valida los datos de una bonificación antes de guardarla.
     */
    public void validarBonificacion(BonificacionesTrabajador bonif) {
        // 1. Si es PERSONAL_INVESTIGADOR, solo permitir 40, 45 o 50
        if (bonif.getTipoBonificacion() == TiposBonificacion.BONIFICACION_PERSONAL_INVESTIGADOR) {
            BigDecimal pct = bonif.getPorcentajeBonificacion();
            if (pct.compareTo(new BigDecimal("40.00")) != 0
                    && pct.compareTo(new BigDecimal("45.00")) != 0
                    && pct.compareTo(new BigDecimal("50.00")) != 0) {
                throw new IllegalArgumentException(
                        "Personal investigador solo admite 40%, 45% o 50%");
            }
        }

        // 2. Si es OTRA_BONIFICACION, la descripción es obligatoria
        if (bonif.getTipoBonificacion() == TiposBonificacion.OTRA_BONIFICACION) {
            if (bonif.getDescripcion() == null || bonif.getDescripcion().isBlank()) {
                throw new IllegalArgumentException(
                        "La descripción es obligatoria para otras bonificaciones");
            }
            if (bonif.getPorcentajeBonificacion().compareTo(BigDecimal.ZERO) <= 0
                    || bonif.getPorcentajeBonificacion().compareTo(new BigDecimal("100")) > 0) {
                throw new IllegalArgumentException(
                        "El porcentaje debe estar entre 0.01% y 100%");
            }
        }

        // 3. Fecha fin >= fecha inicio
        if (bonif.getFechaFin().isBefore(bonif.getFechaInicio())) {
            throw new IllegalArgumentException(
                    "La fecha fin no puede ser anterior a la fecha inicio");
        }

        // 4. Las fechas deben solaparse con el año fiscal
        int anio = bonif.getAnioFiscal();
        LocalDate inicioAnio = LocalDate.of(anio, 1, 1);
        LocalDate finAnio = LocalDate.of(anio, 12, 31);
        if (bonif.getFechaInicio().isAfter(finAnio) || bonif.getFechaFin().isBefore(inicioAnio)) {
            throw new IllegalArgumentException(
                    "El período debe solaparse con el año fiscal " + anio);
        }
    }
}
