package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
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
@Table(name = "factura_colaboracion",
       uniqueConstraints = @UniqueConstraint(columnNames = {"numero_factura", "id_colaboradora"}))
public class FacturaColaboracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura", nullable = false, unique = true)
    private Long idFactura;

    @Column(name = "numero_factura", nullable = false, length = 50)
    private String numeroFactura;

    @Column(columnDefinition = "TEXT")
    private String conceptos;

    @Column(nullable = false)
    private Double importe;

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
    @JoinColumn(name = "id_colaboradora", nullable = false)
    private Colaboradora colaboradora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_contrato")
    private ContratoColaboracion contrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<ImputacionFacturaFase> imputacionesFase = new HashSet<>();
}
