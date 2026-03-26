package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaveOcupacionDTO {

    private String clave;
    private String descripcion;
    private BigDecimal tipoIt;
    private BigDecimal tipoIms;
    private BigDecimal tipoTotal;
    private Boolean activa;
}
