package com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EconomicoDTO{
    Long id;
    String nombre;
    String cif;
    String direccion;
    String telefono;
    String nombreContacto;
    String emailContacto;
    Long horasConvenio;
    String urllogo;
    String urlWeb;
    Long cnae;
    Long anualidad;
    boolean esPyme;
    boolean selloPymeInnovadora;
    String presentacionEmpresa;
    String descripcionIDI;
}