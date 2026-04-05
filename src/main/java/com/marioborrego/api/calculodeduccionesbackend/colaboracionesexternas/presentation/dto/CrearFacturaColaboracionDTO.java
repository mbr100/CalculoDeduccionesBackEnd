package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearFacturaColaboracionDTO {
    @NotNull(message = "El ID de la colaboradora es obligatorio")
    private Long idColaboradora;

    @NotBlank(message = "El número de factura es obligatorio")
    private String numeroFactura;

    private String conceptos;

    @NotNull(message = "El importe es obligatorio")
    private Double importe;

    @NotNull(message = "La base imponible es obligatoria")
    private Double baseImponible;

    @NotNull(message = "El IVA es obligatorio")
    private Double iva;

    private Double porcentajeProrrata;

    @NotNull(message = "La validez es obligatoria")
    private ValidezIDI validez;

    private Double porcentajeValidez;

    private Long idContrato;
    private Long idProyecto;
}
