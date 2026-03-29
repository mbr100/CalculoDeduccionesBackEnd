package com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionDTO {

    private Long id;
    private String name;
    private String displayName;
    private String description;
    private Boolean active;
}
