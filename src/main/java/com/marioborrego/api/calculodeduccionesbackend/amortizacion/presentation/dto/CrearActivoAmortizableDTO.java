package com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearActivoAmortizableDTO {
    @NotNull
    private Long idEconomico;
    @NotBlank
    private String descripcion;
    @NotBlank
    private String proveedor;
    private String numeroFactura;
    @NotNull
    private Double valorAdquisicion;
    @NotNull
    private Double porcentajeAmortizacion;
    @NotNull
    private Double porcentajeUsoProyecto;
    private Long idProyecto;
}
