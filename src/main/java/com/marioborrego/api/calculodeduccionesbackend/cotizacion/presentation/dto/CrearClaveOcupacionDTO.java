package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearClaveOcupacionDTO {

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 2, message = "La clave no puede superar 2 caracteres")
    private String clave;

    private String descripcion;

    @NotNull(message = "El tipo IT es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal tipoIt;

    @NotNull(message = "El tipo IMS es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal tipoIms;

    private Boolean activa;
}
