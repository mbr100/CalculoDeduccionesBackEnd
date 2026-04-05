package com.marioborrego.api.calculodeduccionesbackend.amortizacion.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.FaseProyecto;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "imputacion_activo_fase",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_activo", "id_fase"}))
public class ImputacionActivoFase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_activo", nullable = false)
    private ActivoAmortizable activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fase", nullable = false)
    private FaseProyecto fase;

    @Column(nullable = false)
    private Double importe;
}
