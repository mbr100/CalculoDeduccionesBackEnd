package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.periodosContrato;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarPeriodoContratoDTO {
    private Long id;
    private String claveContrato;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private BigDecimal porcentajeJornada;
    private BigDecimal baseCcMensual;
    private BigDecimal baseCpMensual;
}
