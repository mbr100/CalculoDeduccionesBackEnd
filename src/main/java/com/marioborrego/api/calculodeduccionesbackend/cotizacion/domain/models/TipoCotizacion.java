package com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "tipos_cotizacion",
    uniqueConstraints = @UniqueConstraint(columnNames = {"cnae", "anualidad"})
)
public class TipoCotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String cnae;

    @Column(nullable = false)
    private Integer anualidad;

    private String descripcion;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal contingenciasComunes;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal accidentesTrabajoIT;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal accidentesTrabajoIMS;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal desempleoIndefinido;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal desempleoTemporal;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal fogasa;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal formacionProfesional;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal mei;

    /**
     * Devuelve el tipo total de AT/EP (IT + IMS).
     */
    public BigDecimal getAccidentesTrabajoTotal() {
        return accidentesTrabajoIT.add(accidentesTrabajoIMS);
    }
}
