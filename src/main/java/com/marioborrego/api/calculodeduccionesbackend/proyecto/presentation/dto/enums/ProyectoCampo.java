package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProyectoCampo {
    @JsonProperty("acronimo")
    ACRONIMO,

    @JsonProperty("titulo")
    TITULO,

    @JsonProperty("fechaInicio")
    FECHA_INICIO,

    @JsonProperty("fechaFin")
    FECHA_FIN,

    @JsonProperty("estrategia")
    ESTRATEGIA,

    @JsonProperty("calificacion")
    CALIFICACION;
}
