package com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Calificacion;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Estrategia;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "proyecto")
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto", nullable = false, unique = true)
    private Long idProyecto;
    @Column(unique = true)
    private String acronimo;
    private String titulo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Estrategia estrategia;
    private boolean activo;
    private Calificacion calificacion;

    @OneToMany(mappedBy = "proyecto", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<ProyectoPersonal> proyectoPersonales = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_economico", nullable = false)
    private Economico economico;
}
