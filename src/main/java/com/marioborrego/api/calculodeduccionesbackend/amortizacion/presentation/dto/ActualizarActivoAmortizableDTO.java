package com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarActivoAmortizableDTO {
    @NotNull
    private Long idActivo;
    private String descripcion;
    private String proveedor;
    private String numeroFactura;
    private Double valorAdquisicion;
    private Double porcentajeAmortizacion;
    private Double porcentajeUsoProyecto;
    private Long idProyecto;
    private Boolean clearProyecto;
}
