package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ColaboradoraDTO {
    private Long idColaboradora;
    private String cif;
    private String nombre;
}
