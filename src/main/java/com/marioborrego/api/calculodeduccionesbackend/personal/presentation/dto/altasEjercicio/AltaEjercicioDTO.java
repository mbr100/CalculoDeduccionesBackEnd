package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.altasEjercicio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AltaEjercicioDTO {
    private int idPersona;
    private String nombre;
    private String dni;
    private Long idAltaEjercicio;
    private LocalDate fechaAltaEjercicio;
    private LocalDate fechaBajaEjercicio;
    private Long horasConvenioAnual;
    private Long horasMaximasAnuales;
}
