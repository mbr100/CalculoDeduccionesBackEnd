package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.resumenCostes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenCostePersonalDTO {
    private int idPersonal;
    private String nombre;
    private String dni;
    private Long idCosteHoraPersonal;
    private BigDecimal retribucionTotal;
    private BigDecimal costeSS;
    private BigDecimal horasMaximas;
    private BigDecimal costeHora;
}
