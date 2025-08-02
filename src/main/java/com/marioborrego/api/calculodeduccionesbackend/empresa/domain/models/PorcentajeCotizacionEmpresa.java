package com.marioborrego.api.calculodeduccionesbackend.empresa.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "Porcentajes_cotizacion_empresa")
public class PorcentajeCotizacionEmpresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private Long idCotizacionEmpresa;

    @Column(length = 10, nullable = false, unique = true)
    private String CNAE;

    private String descripcion;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal contingenciasComunes;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal accidentesTrabajo;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal desempleo;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal fogasa;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal formacionProfesional;
}
