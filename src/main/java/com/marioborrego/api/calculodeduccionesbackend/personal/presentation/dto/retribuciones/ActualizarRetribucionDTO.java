package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.retribuciones;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarRetribucionDTO {
    private int idRetribucion;
    private String campoActualizado;
    private Double valor;
}
