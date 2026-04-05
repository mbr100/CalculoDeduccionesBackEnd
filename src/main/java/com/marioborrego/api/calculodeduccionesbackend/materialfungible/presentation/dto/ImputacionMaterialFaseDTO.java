package com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImputacionMaterialFaseDTO {
    private Long id;
    private Long idFactura;
    private Long idFase;
    private String nombreFase;
    private Double importe;
}
