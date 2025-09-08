package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizacionDTO <V, E extends Enum<E>> {
    @NotNull(message = "El ID es obligatorio")
    @JsonProperty("id")
    private Long id;

    @NotNull(message = "El campo a actualizar es obligatorio")
    @JsonProperty("campoActualizado")
    private E campoActualizado;

    @NotNull(message = "El valor es obligatorio")
    @JsonProperty("valor")
    private V valor;
}
