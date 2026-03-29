package com.marioborrego.api.calculodeduccionesbackend.security.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces.PermissionService;
import com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces.RoleService;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Permission;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Role;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.enums.RoleType;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.repository.RoleRepository;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.PermissionDTO;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.RoleDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public RoleServiceImpl(RoleRepository roleRepository, PermissionService permissionService) {
        this.roleRepository = roleRepository;
        this.permissionService = permissionService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAllWithPermissions().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
        return convertToDTO(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleDTO getRoleByName(String name) {
        try {
            RoleType roleType = RoleType.valueOf(name);
            Role role = roleRepository.findByName(roleType)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + name));
            return convertToDTO(role);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Nombre de rol inválido: " + name);
        }
    }

    @Override
    @Transactional
    public void assignPermissionToRole(Long roleId, String permissionName) {
        log.info("Asignando permiso {} al rol con ID: {}", permissionName, roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));

        Permission permission = permissionService.getPermissionEntityByName(permissionName);

        role.addPermission(permission);
        roleRepository.save(role);
        log.info("Permiso asignado exitosamente");
    }

    @Override
    @Transactional
    public void removePermissionFromRole(Long roleId, String permissionName) {
        log.info("Removiendo permiso {} del rol con ID: {}", permissionName, roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + roleId));

        Permission permission = permissionService.getPermissionEntityByName(permissionName);

        role.removePermission(permission);
        roleRepository.save(role);
        log.info("Permiso removido exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public Role getRoleEntityByName(String name) {
        try {
            RoleType roleType = RoleType.valueOf(name);
            return roleRepository.findByName(roleType)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + name));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Nombre de rol inválido: " + name);
        }
    }

    private RoleDTO convertToDTO(Role role) {
        List<PermissionDTO> permissionDTOs = role.getPermissions().stream()
                .filter(Permission::getActive)
                .map(this::convertPermissionToDTO)
                .collect(Collectors.toList());

        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName().name())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .active(role.getActive())
                .isSystemRole(role.getIsSystemRole())
                .permissions(permissionDTOs)
                .build();
    }

    private PermissionDTO convertPermissionToDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName().name())
                .displayName(permission.getDisplayName())
                .description(permission.getDescription())
                .active(permission.getActive())
                .build();
    }
}
