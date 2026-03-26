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
@Table(name = "tarifa_primas_cnae",
       uniqueConstraints = @UniqueConstraint(columnNames = {"cnae", "anio"}))
public class TarifaPrimasCnae {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 10, nullable = false)
    private String cnae;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tipoIt;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tipoIms;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tipoTotal;

    @Column(length = 10, nullable = false)
    private String versionCnae;

    public BigDecimal calcularTotal() {
        return tipoIt.add(tipoIms);
    }
}
