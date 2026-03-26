package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones;

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
public class CrearBonificacionDTO {
    private Long idPersona;
    private String tipoBonificacion;
    private BigDecimal porcentajeBonificacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer anioFiscal;
    private String descripcion;
}
