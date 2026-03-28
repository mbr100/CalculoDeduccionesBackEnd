package com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.NaturalezaContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TipoJornada;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "clave_contrato")
public class ClaveContrato {

    @Id
    @Column(length = 3)
    private String clave;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private NaturalezaContrato naturaleza;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoJornada jornada;

    @Column(nullable = false)
    private Boolean cotizaDesempleo;

    @Column(nullable = false)
    private Boolean cotizaFogasa;

    @Column(nullable = false)
    private Boolean cotizaFp;

    @Column(nullable = false)
    private Boolean cotizaMei;

    @Column(nullable = false)
    private Boolean cotizaCcEstandar;

    @Column(nullable = false)
    private Boolean vigente;
}
