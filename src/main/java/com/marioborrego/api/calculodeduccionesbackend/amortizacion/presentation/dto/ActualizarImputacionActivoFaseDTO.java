package com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarImputacionActivoFaseDTO {
    @NotNull
    private Long idActivo;
    @NotNull
    private Long idFase;
    @NotNull
    private Double importe;
}
