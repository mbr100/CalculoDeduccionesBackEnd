package com.marioborrego.api.calculodeduccionesbackend.security.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces.PermissionService;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Permission;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.enums.PermissionType;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.repository.PermissionRepository;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.PermissionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permiso no encontrado con ID: " + id));
        return convertToDTO(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionByName(String name) {
        try {
            PermissionType permissionType = PermissionType.valueOf(name);
            Permission permission = permissionRepository.findByName(permissionType)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado: " + name));
            return convertToDTO(permission);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Nombre de permiso inválido: " + name);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Permission getPermissionEntityByName(String name) {
        try {
            PermissionType permissionType = PermissionType.valueOf(name);
            return permissionRepository.findByName(permissionType)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado: " + name));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Nombre de permiso inválido: " + name);
        }
    }

    private PermissionDTO convertToDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName().name())
                .displayName(permission.getDisplayName())
                .description(permission.getDescription())
                .active(permission.getActive())
                .build();
    }
}
