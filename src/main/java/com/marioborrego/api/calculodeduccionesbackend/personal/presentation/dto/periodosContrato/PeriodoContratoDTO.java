package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.periodosContrato;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.NaturalezaContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TipoJornada;
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
public class PeriodoContratoDTO {
    private Long id;
    private Long idPersona;
    private String nombre;
    private String dni;
    private String claveContrato;
    private String descripcionContrato;
    private NaturalezaContrato naturaleza;
    private TipoJornada jornada;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private Integer anioFiscal;
    private BigDecimal porcentajeJornada;
    private BigDecimal baseCcMensual;
    private BigDecimal baseCpMensual;
}
