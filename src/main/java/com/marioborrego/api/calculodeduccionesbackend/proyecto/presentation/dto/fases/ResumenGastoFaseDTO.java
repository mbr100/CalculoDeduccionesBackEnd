package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.fases;

import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.PartidaGastoDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenGastoFaseDTO {
    private Long idFase;
    private String nombreFase;
    private List<PartidaGastoDTO> partidas;
    private Double total;
    private Long porcentajeDeduccion;
    private Double deduccion;
}
