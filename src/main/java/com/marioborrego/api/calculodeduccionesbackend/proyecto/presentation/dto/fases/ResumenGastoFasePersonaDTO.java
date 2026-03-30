package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.fases;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenGastoFasePersonaDTO {
    private Long idFase;
    private String nombreFase;
    private Long idPersonal;
    private String nombreCompleto;
    private Double horasAsignadas;
    private Double porcentajeDedicacion;
    private Double horasDedicadas;
    private Double costeHora;
    private Double gastoPersonal;
}
