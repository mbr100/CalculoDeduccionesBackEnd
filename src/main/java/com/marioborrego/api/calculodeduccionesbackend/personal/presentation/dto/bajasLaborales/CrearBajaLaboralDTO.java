package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearBajaLaboralDTO {
    private int idPersona;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}