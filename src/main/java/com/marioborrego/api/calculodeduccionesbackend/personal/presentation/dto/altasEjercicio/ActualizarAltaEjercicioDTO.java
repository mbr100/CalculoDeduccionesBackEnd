package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.altasEjercicio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarAltaEjercicioDTO {
    private Long idAltaEjercicio;
    private String campoActualizado;
    private String valor;
}
