package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.fases;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FilaAsignacionFaseDTO {
    private Long idPersonal;
    private String nombreCompleto;
    private Long idProyectoPersonal;
    private Double horasAsignadas;
    private Double costeHora;
    private List<Double> porcentajes; // un valor por fase, mismo orden que fases[]
}
