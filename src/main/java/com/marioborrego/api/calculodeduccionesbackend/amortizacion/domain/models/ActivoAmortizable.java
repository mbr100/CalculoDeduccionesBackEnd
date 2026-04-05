package com.marioborrego.api.calculodeduccionesbackend.amortizacion.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.Proyecto;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activo_amortizable")
public class ActivoAmortizable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_activo", nullable = false, unique = true)
    private Long idActivo;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(nullable = false, length = 200)
    private String proveedor;

    @Column(length = 50)
    private String numeroFactura;

    @Column(nullable = false)
    private Double valorAdquisicion;

    @Column(nullable = false)
    private Double porcentajeAmortizacion;

    @Column(nullable = false)
    private Double porcentajeUsoProyecto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_economico", nullable = false)
    private Economico economico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

    @OneToMany(mappedBy = "activo", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<ImputacionActivoFase> imputaciones = new HashSet<>();
}
