package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TarifaPrimasCnaeDTO {

    private Long id;
    private String cnae;
    private Integer anio;
    private String descripcion;
    private BigDecimal tipoIt;
    private BigDecimal tipoIms;
    private BigDecimal tipoTotal;
    private String versionCnae;
}
