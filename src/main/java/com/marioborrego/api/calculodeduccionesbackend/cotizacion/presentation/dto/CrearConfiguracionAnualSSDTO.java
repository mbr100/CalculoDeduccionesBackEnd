package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearConfiguracionAnualSSDTO {

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "El año no puede ser anterior al 2000")
    @Max(value = 2100, message = "El año no puede ser posterior al 2100")
    private Integer anio;

    @NotNull(message = "CC empresa es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal ccEmpresa;

    @NotNull(message = "CC trabajador es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal ccTrabajador;

    @NotNull(message = "Desempleo empresa indefinido es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal desempleoEmpresaIndefinido;

    @NotNull(message = "Desempleo empresa temporal es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal desempleoEmpresaTemporal;

    @NotNull(message = "FOGASA es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal fogasa;

    @NotNull(message = "FP empresa es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal fpEmpresa;

    @NotNull(message = "MEI empresa es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal meiEmpresa;

    @NotNull(message = "MEI trabajador es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal meiTrabajador;
}
