package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilaAsignacionDTO {
    private Long idPersonal;
    private String nombreCompleto;
    private List<Double> horas;   // una celda por proyecto, en el mismo orden que "proyectos"
}
