package com.marioborrego.api.calculodeduccionesbackend.security.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces.PermissionService;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.PermissionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestionar permisos
 */
@RestController
@RequestMapping("/api/permissions")
@Tag(name = "Permisos", description = "Endpoints para consulta de permisos")
public class PermissionController {

    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Operation(summary = "Obtener todos los permisos", description = "Retorna una lista de todos los permisos del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permisos obtenidos correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para ver permisos")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('READ_PERMISSION')")
    public ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        log.info("Petición para obtener todos los permisos");
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @Operation(summary = "Obtener permiso por ID", description = "Retorna los detalles de un permiso específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Permiso obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Permiso no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para ver permisos")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_PERMISSION')")
    public ResponseEntity<PermissionDTO> getPermissionById(@PathVariable Long id) {
        log.info("Petición para obtener permiso con ID: {}", id);
        try {
            PermissionDTO permission = permissionService.getPermissionById(id);
            return ResponseEntity.ok(permission);
        } catch (RuntimeException e) {
            log.error("Permiso no encontrado con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
}
