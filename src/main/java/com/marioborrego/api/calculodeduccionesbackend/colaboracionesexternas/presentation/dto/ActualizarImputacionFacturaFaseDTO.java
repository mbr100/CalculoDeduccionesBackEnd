package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarImputacionFacturaFaseDTO {
    @NotNull(message = "El ID de la factura es obligatorio")
    private Long idFactura;

    @NotNull(message = "El ID de la fase es obligatorio")
    private Long idFase;

    @NotNull(message = "El importe es obligatorio")
    private Double importe;
}
