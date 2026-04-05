package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearColaboradoraDTO {
    @NotNull(message = "El ID del económico es obligatorio")
    private Long idEconomico;

    @NotBlank(message = "El CIF es obligatorio")
    @Size(max = 15, message = "El CIF no puede superar 15 caracteres")
    private String cif;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 200, message = "El nombre no puede superar 200 caracteres")
    private String nombre;
}
