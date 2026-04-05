package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenColaboracionesFaseDTO {
    private Long idFase;
    private String nombreFase;
    private Double totalImputado;
}
