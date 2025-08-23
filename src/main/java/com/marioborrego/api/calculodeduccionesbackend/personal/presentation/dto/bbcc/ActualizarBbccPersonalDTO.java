package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bbcc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarBbccPersonalDTO {
    private int idBbccPersonal;
    private String campoActualizado;
    private Double valor;
}

