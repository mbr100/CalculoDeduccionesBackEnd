package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bbcc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BbccPersonalDTO {
    private Long idPersonal;
    private String nombre;
    private String dni;
    private Long id_baseCotizacion;
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

    // Información del periodo de contrato (null si es la fila por defecto)
    private Long idPeriodoContrato;
    private String claveContrato;
    private String descripcionContrato;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private Integer anioFiscal;
}
