package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiguracionAnualSSDTO {

    private Long id;
    private Integer anio;
    private BigDecimal ccEmpresa;
    private BigDecimal ccTrabajador;
    private BigDecimal desempleoEmpresaIndefinido;
    private BigDecimal desempleoEmpresaTemporal;
    private BigDecimal fogasa;
    private BigDecimal fpEmpresa;
    private BigDecimal meiEmpresa;
    private BigDecimal meiTrabajador;
}
