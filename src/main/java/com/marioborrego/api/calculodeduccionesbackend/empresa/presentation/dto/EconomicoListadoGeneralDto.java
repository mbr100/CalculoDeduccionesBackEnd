package com.marioborrego.api.calculodeduccionesbackend.empresa.presentation.dto;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
public class EconomicoListadoGeneralDto implements Serializable {
    int id;
    String nombre;
    String cif;
    String CNAE;
    int anualidad;
    boolean esPyme;
}