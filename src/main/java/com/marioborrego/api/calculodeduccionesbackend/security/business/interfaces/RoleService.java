package com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Role;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.RoleDTO;

import java.util.List;

public interface RoleService {

    List<RoleDTO> getAllRoles();

    RoleDTO getRoleById(Long id);

    RoleDTO getRoleByName(String name);

    void assignPermissionToRole(Long roleId, String permissionName);

    void removePermissionFromRole(Long roleId, String permissionName);

    Role getRoleEntityByName(String name);
}
