package com.marioborrego.api.calculodeduccionesbackend.empresa.presentation.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class EmpresaDto implements Serializable {
    String nombre;
    String cif;
    String direccion;
    String telefono;
    String nombreContacto;
    String emailContacto;
    Long horasConvenio;
    String urllogo;
    String urlWeb;
    String CNAE;
    int anualidad;
    boolean esPyme;
}