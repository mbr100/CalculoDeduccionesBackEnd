package com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActualizarDatosEconomicoDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String nombreContacto;
    private String emailContacto;
    private Long horasConvenio; // puede ser null
    private String urllogo;
    private String urlWeb;
    private Integer cnae;
    private Boolean esPyme;
    private String presentacionEmpresa;
    private String descripcionIDI;
}
