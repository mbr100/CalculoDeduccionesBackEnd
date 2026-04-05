package com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenActivoProyectoDTO {
    private Long idProyecto;
    private String acronimoProyecto;
    private Double totalImputable;
    private List<ResumenActivoFaseDTO> porFase;
}
