package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BajasLaboralesDTO {
    private Long idPersona;
    private String nombre;
    private String dni;
    private Long idBajaLaboral;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Long horasDeBaja;
}
