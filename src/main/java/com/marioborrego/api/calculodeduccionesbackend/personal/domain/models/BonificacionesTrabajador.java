package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "BonificacionesTrabajador")
public class BonificacionesTrabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBonificacionTrabajador;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TiposBonificacion tipoBonificacion;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeBonificacion;

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Integer anioFiscal;

    @Column(length = 300)
    private String descripcion;

    @Column(nullable = false, updatable = false)
    private LocalDate fechaCreacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_personal", nullable = false)
    private Personal personal;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDate.now();
    }
}
