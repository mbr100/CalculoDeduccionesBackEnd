package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImputacionFacturaFaseDTO {
    private Long id;
    private Long idFactura;
    private Long idFase;
    private String nombreFase;
    private Double importe;
}
