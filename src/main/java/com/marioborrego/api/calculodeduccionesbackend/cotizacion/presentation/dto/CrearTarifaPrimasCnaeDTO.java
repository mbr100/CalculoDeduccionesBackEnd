package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearTarifaPrimasCnaeDTO {

    @NotBlank(message = "El CNAE es obligatorio")
    @Size(max = 10, message = "El CNAE no puede superar 10 caracteres")
    private String cnae;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "El año no puede ser anterior al 2000")
    @Max(value = 2100, message = "El año no puede ser posterior al 2100")
    private Integer anio;

    private String descripcion;

    @NotNull(message = "El tipo IT es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal tipoIt;

    @NotNull(message = "El tipo IMS es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal tipoIms;

    @Size(max = 10, message = "La versión CNAE no puede superar 10 caracteres")
    private String versionCnae;
}
