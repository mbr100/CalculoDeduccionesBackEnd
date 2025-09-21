package com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.views;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Calificacion;

public interface GastoPersonalProyectoView {
    Long getIdProyecto();
    String getAcronimo();
    String getTitulo();
    Calificacion getCalificacion();
    Double getGastoPersonal();
}
