package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveContrato;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "PeriodoContrato")
@Table(name = "periodo_contrato", indexes = {
        @Index(name = "idx_periodo_personal_anio", columnList = "id_personal, anio_fiscal")
})
public class PeriodoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personal", nullable = false)
    private Personal personal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clave_contrato", nullable = false)
    private ClaveContrato claveContrato;

    @Column(nullable = false)
    private LocalDate fechaAlta;

    private LocalDate fechaBaja;

    @Column(nullable = false)
    private Integer anioFiscal;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeJornada;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal baseCcMensual;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal baseCpMensual;
}
