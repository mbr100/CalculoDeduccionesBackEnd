package com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearFacturaMaterialDTO {
    @NotNull
    private Long idEconomico;
    @NotBlank
    private String numeroFactura;
    @NotBlank
    private String proveedor;
    private String descripcion;
    @NotNull
    private Double baseImponible;
    @NotNull
    private Double iva;
    private Double porcentajeProrrata;
    @NotNull
    private ValidezIDI validez;
    private Double porcentajeValidez;
    private Long idProyecto;
}
