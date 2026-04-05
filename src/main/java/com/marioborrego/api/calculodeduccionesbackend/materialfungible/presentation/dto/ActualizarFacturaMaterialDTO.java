package com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarFacturaMaterialDTO {
    @NotNull
    private Long idFactura;
    private String numeroFactura;
    private String proveedor;
    private String descripcion;
    private Double baseImponible;
    private Double iva;
    private Double porcentajeProrrata;
    private ValidezIDI validez;
    private Double porcentajeValidez;
    private Long idProyecto;
    private Boolean clearProyecto;
}
