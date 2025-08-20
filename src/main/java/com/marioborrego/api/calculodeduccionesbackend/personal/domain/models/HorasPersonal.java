package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

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

    @OneToMany(mappedBy = "horasPersonal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BajaLaboral> bajas = new ArrayList<>();

    /**
     * Calcula los días de alta en el ejercicio (entre alta y baja en el año fiscal).
     */
    private long getDiasAltaEjercicio() {
        return fechaAltaEjercicio != null && fechaBajaEjercicio != null
                ? fechaAltaEjercicio.until(fechaBajaEjercicio).getDays() + 1
                : 0;
    }

    /**
     * Calcula las horas teóricas para el ejercicio.
     */
    private double getHorasTeoricas() {
        if (horasConvenioAnual == null || horasConvenioAnual <= 0) return 0;
        return getDiasAltaEjercicio() * (horasConvenioAnual / 365.0);
    }

    /**
     * Calcula las horas de baja dentro del ejercicio.
     */
    private double getHorasDeBaja() {
        double horasDia = (horasConvenioAnual != null ? horasConvenioAnual : 1800) / 365.0;
        return bajas.stream()
                .mapToDouble(baja -> {
                    LocalDate inicio = baja.getFechaInicio().isBefore(fechaAltaEjercicio) ? fechaAltaEjercicio : baja.getFechaInicio();
                    LocalDate fin = baja.getFechaFin().isAfter(fechaBajaEjercicio) ? fechaBajaEjercicio : baja.getFechaFin();
                    if (fin.isBefore(inicio)) return 0.0;
                    long dias = inicio.until(fin).getDays() + 1;
                    return dias * horasDia;
                })
                .sum();
    }

    public double getHorasEfectivas() {
        return Math.max(getHorasTeoricas() - getHorasDeBaja(), 0.0);
    }

    @PrePersist
    @PreUpdate
    private void actualizarHorasMaximas() {
        this.horasMaximasAnuales = (long) getHorasEfectivas();
    }
}