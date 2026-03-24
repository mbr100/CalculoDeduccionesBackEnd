package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearTipoCotizacionDTO {

    @NotBlank(message = "El CNAE es obligatorio")
    @Size(max = 10, message = "El CNAE no puede superar 10 caracteres")
    private String cnae;

    @NotNull(message = "La anualidad es obligatoria")
    @Min(value = 2000, message = "La anualidad no puede ser anterior al año 2000")
    @Max(value = 2100, message = "La anualidad no puede ser posterior al año 2100")
    private Integer anualidad;

    private String descripcion;

    @NotNull(message = "Las contingencias comunes son obligatorias")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal contingenciasComunes;

    @NotNull(message = "El IT de accidentes de trabajo es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal accidentesTrabajoIT;

    @NotNull(message = "El IMS de accidentes de trabajo es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal accidentesTrabajoIMS;

    @NotNull(message = "El desempleo indefinido es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal desempleoIndefinido;

    @NotNull(message = "El desempleo temporal es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal desempleoTemporal;

    @NotNull(message = "El FOGASA es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal fogasa;

    @NotNull(message = "La formación profesional es obligatoria")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal formacionProfesional;

    @NotNull(message = "El MEI es obligatorio")
    @DecimalMin(value = "0.00", message = "El porcentaje debe ser positivo")
    private BigDecimal mei;
}
