package com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignPermissionRequestDTO {

    @NotNull(message = "El ID del rol es obligatorio")
    private Long roleId;

    @NotBlank(message = "El nombre del permiso es obligatorio")
    private String permissionName;
}
