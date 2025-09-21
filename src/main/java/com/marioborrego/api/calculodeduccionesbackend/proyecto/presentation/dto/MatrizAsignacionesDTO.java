package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatrizAsignacionesDTO {
    private List<ProyectoAsignacionDTO> proyectos;                // nombres/acrónimos de columnas
    private List<FilaAsignacionDTO> filas;         // cada persona = una fila
}
