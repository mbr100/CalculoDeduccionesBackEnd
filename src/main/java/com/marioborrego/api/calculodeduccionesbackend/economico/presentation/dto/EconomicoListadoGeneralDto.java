package com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
public class EconomicoListadoGeneralDto implements Serializable {
    int id;
    String nombre;
    String cif;
    int CNAE;
    int anualidad;
    boolean esPyme;
}