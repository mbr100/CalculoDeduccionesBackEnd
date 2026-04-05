package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaColaboracionDTO {
    private Long idFactura;
    private String numeroFactura;
    private String conceptos;
    private Double importe;
    private Double baseImponible;
    private Double iva;
    private Double porcentajeProrrata;
    private ValidezIDI validez;
    private Double porcentajeValidez;
    private Double importeFinal;
    private Double importeImputable;
    private Long idColaboradora;
    private String nombreColaboradora;
    private Long idContrato;
    private String nombreContrato;
    private Long idProyecto;
    private String acronimoProyecto;
    private List<ImputacionFacturaFaseDTO> imputacionesFase;
}
