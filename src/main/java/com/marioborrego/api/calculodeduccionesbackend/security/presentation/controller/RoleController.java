package com.marioborrego.api.calculodeduccionesbackend.security.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces.RoleService;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.AssignPermissionRequestDTO;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.RoleDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestionar roles
 */
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Endpoints para gestión de roles")
public class RoleController {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @Operation(summary = "Obtener todos los roles", description = "Retorna una lista de todos los roles del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Roles obtenidos correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para ver roles")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('READ_ROLE')")
    public ResponseEntity<List<RoleDTO>> getAllRoles() {
        log.info("Petición para obtener todos los roles");
        List<RoleDTO> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = "Obtener rol por ID", description = "Retorna los detalles de un rol específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Rol no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para ver roles")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_ROLE')")
    public ResponseEntity<RoleDTO> getRoleById(@PathVariable Long id) {
        log.info("Petición para obtener rol con ID: {}", id);
        try {
            RoleDTO role = roleService.getRoleById(id);
            return ResponseEntity.ok(role);
        } catch (RuntimeException e) {
            log.error("Rol no encontrado con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Asignar permiso a rol", description = "Asigna un permiso a un rol específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permiso asignado correctamente"),
            @ApiResponse(responseCode = "404", description = "Rol o permiso no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para asignar permisos")
    })
    @PostMapping("/assign-permission")
    @PreAuthorize("hasAuthority('ASSIGN_PERMISSION')")
    public ResponseEntity<Void> assignPermissionToRole(@Valid @RequestBody AssignPermissionRequestDTO request) {
        log.info("Petición para asignar permiso {} al rol con ID: {}", request.getPermissionName(), request.getRoleId());
        try {
            roleService.assignPermissionToRole(request.getRoleId(), request.getPermissionName());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error al asignar permiso: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Remover permiso de rol", description = "Remueve un permiso de un rol específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permiso removido correctamente"),
            @ApiResponse(responseCode = "404", description = "Rol o permiso no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para remover permisos")
    })
    @DeleteMapping("/{roleId}/permissions/{permissionName}")
    @PreAuthorize("hasAuthority('ASSIGN_PERMISSION')")
    public ResponseEntity<Void> removePermissionFromRole(
            @PathVariable Long roleId,
            @PathVariable String permissionName
    ) {
        log.info("Petición para remover permiso {} del rol con ID: {}", permissionName, roleId);
        try {
            roleService.removePermissionFromRole(roleId, permissionName);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error al remover permiso: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
