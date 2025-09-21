package com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GastoProyectoDetalladoDTO {
    private Long idProyecto;
    private String acronimo;
    private String titulo;
    private List<PartidaGastoDTO> partidas;
    private Double total;

}
