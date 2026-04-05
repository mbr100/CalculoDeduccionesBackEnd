package com.marioborrego.api.calculodeduccionesbackend.materialfungible.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
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
@Table(name = "factura_material",
       uniqueConstraints = @UniqueConstraint(columnNames = {"numero_factura", "id_economico"}))
public class FacturaMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura", nullable = false, unique = true)
    private Long idFactura;

    @Column(name = "numero_factura", nullable = false, length = 50)
    private String numeroFactura;

    @Column(nullable = false, length = 200)
    private String proveedor;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Double baseImponible;

    @Column(nullable = false)
    private Double iva;

    @Column(nullable = false)
    @Builder.Default
    private Double porcentajeProrrata = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidezIDI validez;

    @Column(nullable = false)
    private Double porcentajeValidez;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_economico", nullable = false)
    private Economico economico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<ImputacionMaterialFase> imputaciones = new HashSet<>();
}
