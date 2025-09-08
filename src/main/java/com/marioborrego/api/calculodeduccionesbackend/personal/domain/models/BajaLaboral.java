package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.ChronoUnit;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity()
@Table(name = "baja_laboral")
public class BajaLaboral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    private Long horasDeBaja;

    @ManyToOne
    private HorasPersonal horasPersonal;

    @ManyToOne
    @JoinColumn(name = "id_personal", referencedColumnName = "id_personal", nullable = false)
    private Personal personal;

    @PrePersist
    @PreUpdate
    @PreRemove
    private void procesarCambios() {
        System.out.println("=== DEBUG: Ejecutando procesarCambios() ===");
        System.out.println("ID: " + id);
        System.out.println("FechaInicio: " + fechaInicio);
        System.out.println("FechaFin: " + fechaFin);

        // Calcular horas de baja ANTES de actualizar horas máximas
        calcularHorasDeBaja();

        // Actualizar horas máximas
        if (horasPersonal != null) {
            horasPersonal.setHorasMaximasAnuales((long) horasPersonal.getHorasEfectivas());
            System.out.println("Horas máximas actualizadas: " + horasPersonal.getHorasMaximasAnuales());
        }
    }

    private void calcularHorasDeBaja() {
        System.out.println("--- Iniciando cálculo de horas de baja ---");

        // Validaciones básicas
        if (personal == null) {
            System.out.println("ERROR: Personal es null");
            this.horasDeBaja = 0L;
            return;
        }

        if (personal.getEconomico() == null) {
            System.out.println("ERROR: Económico es null");
            this.horasDeBaja = 0L;
            return;
        }

        if (horasPersonal == null) {
            System.out.println("ERROR: HorasPersonal es null");
            this.horasDeBaja = 0L;
            return;
        }

        long diasAnualidad = Year.of(Math.toIntExact(personal.getEconomico().getAnualidad())).isLeap() ? 366 : 365;
        System.out.println("Días anualidad: " + diasAnualidad);

        if (fechaInicio == null || fechaFin == null) {
            System.out.println("ERROR: Fechas son null - Inicio: " + fechaInicio + ", Fin: " + fechaFin);
            this.horasDeBaja = 0L;
            return;
        }

        if (fechaInicio.isAfter(fechaFin)) {
            System.out.println("ERROR: Fecha inicio posterior a fecha fin");
            this.horasDeBaja = 0L;
            return;
        }

        long diasBaja = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;

        System.out.println("Días de baja calculados: " + diasBaja);

        Long horasConvenio = horasPersonal.getHorasConvenioAnual();
        System.out.println("Horas convenio anual: " + horasConvenio);

        if (horasConvenio != null && horasConvenio > 0) {
            long horasPorDia = horasConvenio / diasAnualidad;
            double horasCalculadas = diasBaja * horasPorDia;

            // Redondear al entero más cercano
            this.horasDeBaja = Math.round(horasCalculadas);

            System.out.println("Horas por día: " + horasPorDia);
            System.out.println("Horas calculadas (decimal): " + horasCalculadas);
            System.out.println("Horas de baja final: " + this.horasDeBaja);
        } else {
            System.out.println("ERROR: Horas convenio inválidas: " + horasConvenio);
            this.horasDeBaja = 0L;
        }

        System.out.println("--- Fin cálculo de horas de baja ---");
    }

    public void recalcularHorasDeBaja() {
        calcularHorasDeBaja();
    }


    @Override
    public String toString() {
        return "BajaLaboral{" +
                "id=" + id +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", horasDeBaja=" + horasDeBaja +
                '}';
    }
}