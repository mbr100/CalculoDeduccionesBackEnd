package com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenMaterialFaseDTO {
    private Long idFase;
    private String nombreFase;
    private Double totalImputado;
}
