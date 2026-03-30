package com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "asignacion_fase",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_proyecto_personal", "id_fase"}))
public class AsignacionFase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto_personal", nullable = false)
    private ProyectoPersonal proyectoPersonal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fase", nullable = false)
    private FaseProyecto faseProyecto;

    @Column(name = "porcentaje_dedicacion", nullable = false)
    private Double porcentajeDedicacion;
}
