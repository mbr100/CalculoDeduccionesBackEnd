package com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "proyecto_personal")
public class ProyectoPersonal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;  // ID propio autogenerado

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personal", nullable = false)
    private Personal personal;

    @Column(name = "horas_asignadas")
    private Double horasAsignadas;
}
