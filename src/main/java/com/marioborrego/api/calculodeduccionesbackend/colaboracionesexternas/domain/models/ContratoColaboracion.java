package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.TipoContratoColaboradorasExternas;
import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
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
@Table(name = "contrato_colaboracion")
public class ContratoColaboracion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contrato", nullable = false, unique = true)
    private Long idContrato;

    @Column(nullable = false, length = 200)
    private String nombreContrato;

    @Column(columnDefinition = "TEXT")
    private String objeto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContratoColaboradorasExternas tipoContrato;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ValidezIDI validez;

    @Column(nullable = false)
    private Double importeCubierto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_colaboradora", nullable = false)
    private Colaboradora colaboradora;

    @OneToMany(mappedBy = "contrato", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<FacturaColaboracion> facturas = new HashSet<>();
}
