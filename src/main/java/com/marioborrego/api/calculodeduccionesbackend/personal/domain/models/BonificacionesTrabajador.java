package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "BonificacionesTrabajador")
public class BonificacionesTrabajador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idBonificacionTrabajador;

    @Enumerated(value = EnumType.STRING)
    private TiposBonificacion tipoBonificacion;

    private BigDecimal porcentajeBonificacion;
}
