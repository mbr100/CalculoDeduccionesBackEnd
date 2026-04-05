package com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivoAmortizableDTO {
    private Long idActivo;
    private String descripcion;
    private String proveedor;
    private String numeroFactura;
    private Double valorAdquisicion;
    private Double porcentajeAmortizacion;
    private Double porcentajeUsoProyecto;
    private Double cuotaAmortizacion;
    private Double importeImputable;
    private Long idEconomico;
    private Long idProyecto;
    private String acronimoProyecto;
    private List<ImputacionActivoFaseDTO> imputaciones;
}
