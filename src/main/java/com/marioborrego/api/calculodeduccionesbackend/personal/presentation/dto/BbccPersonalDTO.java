package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BbccPersonalDTO {
    private int idPersonal;
    private String nombre;
    private String dni;
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
}
