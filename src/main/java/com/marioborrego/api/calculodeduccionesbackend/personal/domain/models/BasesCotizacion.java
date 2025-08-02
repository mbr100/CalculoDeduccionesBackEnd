package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "BasesCotizacion")
public class BasesCotizacion {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int id_baseCotizacion;

    private Long basesCotizacionContingenciasComunesEnero;
    private Long basesCotizacionContingenciasComunesFebrero;
    private Long basesCotizacionContingenciasComunesMarzo;
    private Long basesCotizacionContingenciasComunesAbril;
    private Long basesCotizacionContingenciasComunesMayo;
    private Long basesCotizacionContingenciasComunesJunio;
    private Long basesCotizacionContingenciasComunesJulio;
    private Long basesCotizacionContingenciasComunesAgosto;
    private Long basesCotizacionContingenciasComunesSeptiembre;
    private Long basesCotizacionContingenciasComunesOctubre;
    private Long basesCotizacionContingenciasComunesNoviembre;
    private Long basesCotizacionContingenciasComunesDiciembre;

    @Transient
    private Long getBasesCotizacionContingenciasComunesAnual() {
        return (basesCotizacionContingenciasComunesEnero != null ? basesCotizacionContingenciasComunesEnero : 0L)
                + (basesCotizacionContingenciasComunesFebrero != null ? basesCotizacionContingenciasComunesFebrero : 0L)
                + (basesCotizacionContingenciasComunesMarzo != null ? basesCotizacionContingenciasComunesMarzo : 0L)
                + (basesCotizacionContingenciasComunesAbril != null ? basesCotizacionContingenciasComunesAbril : 0L)
                + (basesCotizacionContingenciasComunesMayo != null ? basesCotizacionContingenciasComunesMayo : 0L)
                + (basesCotizacionContingenciasComunesJunio != null ? basesCotizacionContingenciasComunesJunio : 0L)
                + (basesCotizacionContingenciasComunesJulio != null ? basesCotizacionContingenciasComunesJulio : 0L)
                + (basesCotizacionContingenciasComunesAgosto != null ? basesCotizacionContingenciasComunesAgosto : 0L)
                + (basesCotizacionContingenciasComunesSeptiembre != null ? basesCotizacionContingenciasComunesSeptiembre : 0L)
                + (basesCotizacionContingenciasComunesOctubre != null ? basesCotizacionContingenciasComunesOctubre : 0L)
                + (basesCotizacionContingenciasComunesNoviembre != null ? basesCotizacionContingenciasComunesNoviembre : 0L)
                + (basesCotizacionContingenciasComunesDiciembre != null ? basesCotizacionContingenciasComunesDiciembre : 0L);
    }

    @OneToOne
    private Empleado empleado;  // Relación con la entidad Empleado
}
