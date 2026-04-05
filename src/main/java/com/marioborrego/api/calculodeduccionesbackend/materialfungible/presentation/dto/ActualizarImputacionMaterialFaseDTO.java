package com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarImputacionMaterialFaseDTO {
    @NotNull
    private Long idFactura;
    @NotNull
    private Long idFase;
    @NotNull
    private Double importe;
}
