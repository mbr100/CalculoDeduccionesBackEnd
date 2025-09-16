package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Calificacion;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Estrategia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CrearProyectoDTO implements Serializable {
    @NotNull(message = "El ID del proyecto no puede ser nulo")
    Long idProyecto;

    @NotBlank(message = "El acrónimo es obligatorio")
    @Size(min = 10, max = 10, message = "El acrónimo debe tener exactamente 10 caracteres")
    private String acronimo;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    private String titulo;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @PastOrPresent(message = "La fecha de inicio no puede estar en el futuro")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "La estrategia es obligatoria")
    private Estrategia estrategia;

    @NotNull(message = "La calificación es obligatoria")
    private Calificacion calificacion;

    @NotNull(message = "El ID económico es obligatorio")
    private Long idEconomico;
}
