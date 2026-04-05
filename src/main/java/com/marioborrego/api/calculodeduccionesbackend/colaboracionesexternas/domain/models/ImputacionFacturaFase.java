package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.FaseProyecto;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "imputacion_factura_fase",
       uniqueConstraints = @UniqueConstraint(columnNames = {"id_factura", "id_fase"}))
public class ImputacionFacturaFase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_factura", nullable = false)
    private FacturaColaboracion factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fase", nullable = false)
    private FaseProyecto fase;

    @Column(nullable = false)
    private Double importe;
}
