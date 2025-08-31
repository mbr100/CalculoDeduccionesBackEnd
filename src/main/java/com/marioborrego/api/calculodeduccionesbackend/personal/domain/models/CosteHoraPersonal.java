package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "CosteHoraPersonal")
public class CosteHoraPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal retribucionTotal;
    private BigDecimal costeSS;
    private BigDecimal horasMaximas;
    private BigDecimal costeHora;

    @OneToOne(mappedBy = "costeHoraPersonal")
    private Personal personal;
}
