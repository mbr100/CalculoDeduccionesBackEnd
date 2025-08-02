package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "BajaLaboral")
public class BajaLaboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private String motivo; // Opcional: enfermedad común, accidente laboral, maternidad, etc.

    @ManyToOne
    @JoinColumn(name = "horas_empleado_id", nullable = false)
    private HorasEmpleado horasEmpleado;

    // Opcional: relación directa con el empleado (si se necesita acceso rápido desde empleado)
    @ManyToOne
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;
}
