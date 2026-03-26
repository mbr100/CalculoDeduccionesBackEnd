package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BonificacionResultDTO {
    private BigDecimal ahorroTotalAnual;
    private BigDecimal ahorroInvestigador;
    private BigDecimal ahorroOtrasBonificaciones;
    private List<DetalleBonificacionDTO> detalles;
}
