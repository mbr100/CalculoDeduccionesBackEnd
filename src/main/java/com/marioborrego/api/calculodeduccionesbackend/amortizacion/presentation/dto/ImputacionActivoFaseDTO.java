package com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImputacionActivoFaseDTO {
    private Long id;
    private Long idActivo;
    private Long idFase;
    private String nombreFase;
    private Double importe;
}
