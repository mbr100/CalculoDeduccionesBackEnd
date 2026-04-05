package com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacturaMaterialDTO {
    private Long idFactura;
    private String numeroFactura;
    private String proveedor;
    private String descripcion;
    private Double baseImponible;
    private Double iva;
    private Double porcentajeProrrata;
    private ValidezIDI validez;
    private Double porcentajeValidez;
    private Double importeFinal;
    private Double importeImputable;
    private Long idEconomico;
    private Long idProyecto;
    private String acronimoProyecto;
    private List<ImputacionMaterialFaseDTO> imputaciones;
}
