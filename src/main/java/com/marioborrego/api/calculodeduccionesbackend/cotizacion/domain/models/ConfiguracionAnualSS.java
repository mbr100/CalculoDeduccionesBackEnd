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
@Table(name = "configuracion_anual_ss",
       uniqueConstraints = @UniqueConstraint(columnNames = "anio"))
public class ConfiguracionAnualSS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer anio;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal ccEmpresa;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal ccTrabajador;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal desempleoEmpresaIndefinido;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal desempleoEmpresaTemporal;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal fogasa;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal fpEmpresa;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal meiEmpresa;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal meiTrabajador;
}
