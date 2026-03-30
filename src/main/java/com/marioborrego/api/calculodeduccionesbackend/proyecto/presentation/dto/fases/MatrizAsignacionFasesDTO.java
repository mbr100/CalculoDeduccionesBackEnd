package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.fases;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatrizAsignacionFasesDTO {
    private List<FaseProyectoDTO> fases;
    private List<FilaAsignacionFaseDTO> filas;
}
