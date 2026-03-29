package com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {

    private Long id;
    private String name;
    private String displayName;
    private String description;
    private Boolean active;
    private Boolean isSystemRole;
    private List<PermissionDTO> permissions;
}
