package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleBonificacionDTO {
    private Long idBonificacion;
    private String tipoBonificacion;
    private BigDecimal porcentaje;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Integer diasBonificados;
    private BigDecimal ahorroPeriodo;
    private String descripcion;
}
