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
        calcularHorasDeBaja();

        if (horasPersonal != null) {
            horasPersonal.setHorasMaximasAnuales((long) horasPersonal.getHorasEfectivas());
        }
    }

    private void calcularHorasDeBaja() {
        if (personal == null) {
            this.horasDeBaja = 0L;
            return;
        }

        if (personal.getEconomico() == null) {
            this.horasDeBaja = 0L;
            return;
        }

        if (horasPersonal == null) {
            this.horasDeBaja = 0L;
            return;
        }

        long diasAnualidad = Year.of(Math.toIntExact(personal.getEconomico().getAnualidad())).isLeap() ? 366 : 365;

        if (fechaInicio == null || fechaFin == null) {
            this.horasDeBaja = 0L;
            return;
        }

        if (fechaInicio.isAfter(fechaFin)) {
            this.horasDeBaja = 0L;
            return;
        }

        long diasBaja = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;

        Long horasConvenio = horasPersonal.getHorasConvenioAnual();

        if (horasConvenio != null && horasConvenio > 0) {
            long horasPorDia = horasConvenio / diasAnualidad;
            double horasCalculadas = diasBaja * horasPorDia;

            this.horasDeBaja = Math.round(horasCalculadas);
        } else {
            this.horasDeBaja = 0L;
        }
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
