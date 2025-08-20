package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
//    @JoinColumn(name = "horas_personal_id", nullable = false)
    private HorasPersonal horasPersonal;

    @ManyToOne
    @JoinColumn(name = "id_personal", referencedColumnName = "id_personal", nullable = false)
    private Personal personal;


    @PrePersist
    @PreUpdate
    @PreRemove
    private void notificarCambio() {
        if (horasPersonal != null) {
            horasPersonal.setHorasMaximasAnuales((long) horasPersonal.getHorasEfectivas());
        }
    }
}
