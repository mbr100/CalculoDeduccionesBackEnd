package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarColaboradoraDTO {
    @NotNull(message = "El ID de la colaboradora es obligatorio")
    private Long idColaboradora;
    private String cif;
    private String nombre;
}
