package com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EconomicoDTO{
    int id;
    String nombre;
    String cif;
    String direccion;
    String telefono;
    String nombreContacto;
    String emailContacto;
    Long horasConvenio;
    String urllogo;
    String urlWeb;
    int cnae;
    int anualidad;
    boolean esPyme;
    String presentacionEmpresa;
    String descripcionIDI;
}