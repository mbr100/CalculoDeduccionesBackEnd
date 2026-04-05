package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenColaboracionesProyectoDTO {
    private Long idProyecto;
    private String acronimoProyecto;
    private Double totalFacturadoImputable;
    private List<ResumenColaboracionesFaseDTO> porFase;
}
