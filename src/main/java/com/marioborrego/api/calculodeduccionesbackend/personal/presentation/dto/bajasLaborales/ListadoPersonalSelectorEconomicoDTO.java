package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListadoPersonalSelectorEconomicoDTO {
    private int idPersona;
    private String nombre;
}
