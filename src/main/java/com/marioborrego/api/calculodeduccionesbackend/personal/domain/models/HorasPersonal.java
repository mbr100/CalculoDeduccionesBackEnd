package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "HorasPersonal")
public class HorasPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer ejercicio; // Año fiscal

    private LocalDate fechaAltaEjercicio;
    private LocalDate fechaBajaEjercicio;

    private Long horasConvenioAnual;

    private Long horasMaximasAnuales;

    @OneToOne(mappedBy = "horasPersonal")
    private Personal personal;

    @OneToMany(mappedBy = "horasPersonal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BajaLaboral> bajas = new ArrayList<>();

    /**
     * Calcula los días de alta en el ejercicio (entre alta y baja en el año fiscal).
     */
    private long getDiasAltaEjercicio() {
        return ChronoUnit.DAYS.between(fechaAltaEjercicio, fechaBajaEjercicio) + 1;
    }

    /**
     * Calcula las horas teóricas para el ejercicio.
     */
    private double getHorasTeoricas() {
        if (horasConvenioAnual == null || horasConvenioAnual <= 0) return 0;
        System.out.printf("horasConvenioAnual: %d, getDiasAltaEjercicio: %d%n", horasConvenioAnual, getDiasAltaEjercicio());
        System.out.printf("horasTeoricas: %f%n", getDiasAltaEjercicio() * (horasConvenioAnual / 365.0));
        return getDiasAltaEjercicio() * (horasConvenioAnual / 365.0);
    }

    /**
     * Calcula las horas de baja dentro del ejercicio.
     */
    private double getHorasDeBaja() {
        return bajas.stream()
                .mapToDouble(BajaLaboral::getHorasDeBaja)
                .sum();
    }

    public double getHorasEfectivas() {
        return Math.max(getHorasTeoricas() - getHorasDeBaja(), 0.0);
    }

    @PrePersist
    @PreUpdate
    private void actualizarHorasMaximas() {
        long horasEfectivas = (long) getHorasEfectivas();
        if (horasEfectivas > horasConvenioAnual) {
            this.horasMaximasAnuales = horasConvenioAnual;
        } else {
            this.horasMaximasAnuales = (long) getHorasEfectivas();
        }
    }

    public void actualizarHorasMaximasAnuales() {
        long horasEfectivas = (long) getHorasEfectivas();
        if (horasEfectivas > horasConvenioAnual) {
            this.horasMaximasAnuales = horasConvenioAnual;
        } else {
            this.horasMaximasAnuales = (long) getHorasEfectivas();
        }
    }
}