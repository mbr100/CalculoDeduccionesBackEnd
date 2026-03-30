package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.fases;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarAsignacionFaseDTO {
    @NotNull(message = "El ID del proyecto personal es obligatorio")
    private Long idProyectoPersonal;

    @NotNull(message = "El ID de la fase es obligatorio")
    private Long idFase;

    @NotNull(message = "El porcentaje de dedicacion es obligatorio")
    @Min(value = 0, message = "El porcentaje no puede ser negativo")
    @Max(value = 100, message = "El porcentaje no puede superar 100")
    private Double porcentajeDedicacion;
}
