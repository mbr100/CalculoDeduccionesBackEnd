package com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PartidaGastoDTO {
    private String tipoGasto;
    private Double importe;
}
