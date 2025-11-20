package com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Permission;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.PermissionDTO;

import java.util.List;

public interface PermissionService {

    List<PermissionDTO> getAllPermissions();

    PermissionDTO getPermissionById(Long id);

    PermissionDTO getPermissionByName(String name);

    Permission getPermissionEntityByName(String name);
}
