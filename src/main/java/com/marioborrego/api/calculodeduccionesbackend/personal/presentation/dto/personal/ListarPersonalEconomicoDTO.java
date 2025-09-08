package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.personal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListarPersonalEconomicoDTO {
    private Long idPersona;
    private String nombre;
    private String apellidos;
    private String dni;
    private String puesto;
    private String departamento;
    private String titulacion1;
    private String titulacion2;
    private String titulacion3;
    private String titulacion4;
    private boolean esPersonalInvestigador;
}
