package com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenActivoFaseDTO {
    private Long idFase;
    private String nombreFase;
    private Double totalImputado;
}
