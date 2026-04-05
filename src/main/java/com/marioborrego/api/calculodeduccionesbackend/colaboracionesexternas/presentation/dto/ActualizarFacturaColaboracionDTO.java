package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarFacturaColaboracionDTO {
    @NotNull(message = "El ID de la factura es obligatorio")
    private Long idFactura;
    private String numeroFactura;
    private String conceptos;
    private Double importe;
    private Double baseImponible;
    private Double iva;
    private Double porcentajeProrrata;
    private ValidezIDI validez;
    private Double porcentajeValidez;
    private Long idContrato;
    private Long idProyecto;
}
