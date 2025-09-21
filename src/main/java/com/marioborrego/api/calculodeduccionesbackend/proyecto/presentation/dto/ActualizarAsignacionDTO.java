package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarAsignacionDTO {
    private Long idPersonal;
    private Long idProyecto;
    private Long horas;
}
