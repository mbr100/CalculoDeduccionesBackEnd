package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
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
@Table(name = "colaboradora",
       uniqueConstraints = @UniqueConstraint(columnNames = {"cif", "id_economico"}))
public class Colaboradora {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_colaboradora", nullable = false, unique = true)
    private Long idColaboradora;

    @Column(nullable = false, length = 15)
    private String cif;

    @Column(nullable = false, length = 200)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_economico", nullable = false)
    private Economico economico;

    @OneToMany(mappedBy = "colaboradora", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<ContratoColaboracion> contratos = new HashSet<>();

    @OneToMany(mappedBy = "colaboradora", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<FacturaColaboracion> facturas = new HashSet<>();
}
