package com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearEconomicoDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Nombre inválido")
    private String nombre;

    @NotBlank(message = "El CIF es obligatorio")
    @Pattern(
            regexp = "^[A-Z]\\d{7}[A-Z0-9]$",
            message = "El CIF debe tener el formato correcto (ej: B12345678)"
    )
    private String cif;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede superar los 255 caracteres")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\d{9}$", message = "El teléfono debe tener exactamente 9 dígitos")
    private String telefono;

    @NotBlank(message = "El nombre del contacto es obligatorio")
    @Size(max = 100, message = "El nombre del contacto no puede superar los 100 caracteres")
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$",
            message = "El nombre del contacto solo puede contener letras y espacios"
    )
    private String nombreContacto;

    @NotBlank(message = "El email del contacto es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String emailContacto;

    @PositiveOrZero(message = "Las horas de convenio deben ser positivas o cero")
    private Integer horasConvenio; // Puede ser null

    @NotBlank(message = "La URL del logo es obligatoria")
    @Size(max = 255, message = "La URL del logo no puede superar los 255 caracteres")
    @Pattern(
            regexp = "^(https?://).+",
            message = "La URL del logo debe comenzar por http:// o https://"
    )
    private String urllogo;

    @NotBlank(message = "La URL de la web es obligatoria")
    @Size(max = 255, message = "La URL de la web no puede superar los 255 caracteres")
    @Pattern(
            regexp = "^(https?://).+",
            message = "La URL de la web debe comenzar por http:// o https://"
    )
    private String urlWeb;

    @NotNull(message = "El CNAE es obligatorio")
    @Digits(integer = 5, fraction = 0, message = "El CNAE debe ser un número de hasta 5 dígitos")
    private Integer cnae;

    @NotNull(message = "La anualidad es obligatoria")
    @Min(value = 2000, message = "La anualidad no puede ser anterior al año 2000")
    @Max(value = 2100, message = "La anualidad no puede ser posterior al año 2100")
    private int anualidad;

    @NotNull(message = "Debe indicarse si es Pyme o no")
    private Boolean esPyme;
}
