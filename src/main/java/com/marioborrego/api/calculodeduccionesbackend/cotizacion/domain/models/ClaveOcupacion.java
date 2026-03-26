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
@Table(name = "clave_ocupacion")
public class ClaveOcupacion {

    @Id
    @Column(length = 2)
    private String clave;

    @Column(nullable = false, length = 150)
    private String descripcion;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tipoIt;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tipoIms;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal tipoTotal;

    @Column(nullable = false)
    private Boolean activa;
}
