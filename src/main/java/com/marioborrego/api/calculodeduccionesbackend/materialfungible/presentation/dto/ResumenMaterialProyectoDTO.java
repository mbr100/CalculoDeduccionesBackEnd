package com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenMaterialProyectoDTO {
    private Long idProyecto;
    private String acronimoProyecto;
    private Double totalImputable;
    private List<ResumenMaterialFaseDTO> porFase;
}
