package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TipoCotizacionDTO {

    private Long id;
    private String cnae;
    private Integer anualidad;
    private String descripcion;
    private BigDecimal contingenciasComunes;
    private BigDecimal accidentesTrabajoIT;
    private BigDecimal accidentesTrabajoIMS;
    private BigDecimal accidentesTrabajoTotal;
    private BigDecimal desempleoIndefinido;
    private BigDecimal desempleoTemporal;
    private BigDecimal fogasa;
    private BigDecimal formacionProfesional;
    private BigDecimal mei;
}
