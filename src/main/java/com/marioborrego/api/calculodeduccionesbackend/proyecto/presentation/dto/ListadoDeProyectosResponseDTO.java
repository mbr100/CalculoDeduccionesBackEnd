package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Calificacion;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Estrategia;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListadoDeProyectosResponseDTO implements Serializable {
    Long idProyecto;
    String acronimo;
    String titulo;
    LocalDate fechaInicio;
    LocalDate fechaFin;
    Estrategia estrategia;
    Calificacion calificacion;
}