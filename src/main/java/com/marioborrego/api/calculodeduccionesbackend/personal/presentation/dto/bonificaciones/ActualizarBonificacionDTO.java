package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarBonificacionDTO {
    private Long idBonificacionTrabajador;
    private String campoActualizado;
    private Object valor;
}
