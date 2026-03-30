package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.fases;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearFaseProyectoDTO {
    @NotNull(message = "El ID del proyecto es obligatorio")
    private Long idProyecto;

    @NotBlank(message = "El nombre de la fase es obligatorio")
    @Size(min = 1, max = 100, message = "El nombre debe tener entre 1 y 100 caracteres")
    private String nombre;
}
