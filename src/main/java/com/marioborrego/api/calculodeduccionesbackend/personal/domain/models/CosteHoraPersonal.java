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

    // Desglose de cuotas SS empresa (anuales)
    @Column(precision = 12, scale = 2)
    private BigDecimal cuotaCC;
    @Column(precision = 12, scale = 2)
    private BigDecimal cuotaATEP;
    @Column(precision = 12, scale = 2)
    private BigDecimal cuotaDesempleo;
    @Column(precision = 12, scale = 2)
    private BigDecimal cuotaFogasa;
    @Column(precision = 12, scale = 2)
    private BigDecimal cuotaFP;
    @Column(precision = 12, scale = 2)
    private BigDecimal cuotaMEI;

    // Trazabilidad del tipo AT/EP aplicado
    @Column(precision = 5, scale = 2)
    private BigDecimal tipoATEPAplicado;
    @Column(length = 50)
    private String origenTipoATEP;

    // Bonificaciones SS
    @Column(precision = 12, scale = 2)
    private BigDecimal ssEmpresaBruta;
    @Column(precision = 12, scale = 2)
    private BigDecimal ahorroBonificaciones;
    @Column(precision = 12, scale = 2)
    private BigDecimal ahorroInvestigador;
    @Column(precision = 12, scale = 2)
    private BigDecimal ahorroOtrasBonificaciones;

    @OneToOne(mappedBy = "costeHoraPersonal")
    private Personal personal;
}
