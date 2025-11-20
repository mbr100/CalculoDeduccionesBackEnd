package com.marioborrego.api.calculodeduccionesbackend.security.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces.UserService;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller para gestionar usuarios
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Obtener todos los usuarios paginados", description = "Retorna una lista paginada de usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para ver usuarios")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('READ_USER')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(page = 0, size = 20, sort = "username", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        log.info("Petición para obtener todos los usuarios paginados");
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Buscar usuarios", description = "Busca usuarios por nombre, apellido, username o email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada correctamente"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para buscar usuarios")
    })
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('READ_USER')")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @RequestParam String query,
            @PageableDefault(page = 0, size = 20) Pageable pageable
    ) {
        log.info("Petición para buscar usuarios con query: {}", query);
        Page<UserDTO> users = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Retorna los detalles de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para ver usuarios")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_USER')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        log.info("Petición para obtener usuario con ID: {}", id);
        try {
            UserDTO user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.error("Usuario no encontrado con ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "409", description = "El usuario o email ya existe"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para crear usuarios")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequestDTO request) {
        log.info("Petición para crear usuario: {}", request.getUsername());
        try {
            UserDTO user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (RuntimeException e) {
            log.error("Error al crear usuario: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para actualizar usuarios")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequestDTO request
    ) {
        log.info("Petición para actualizar usuario con ID: {}", id);
        try {
            UserDTO user = userService.updateUser(id, request);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            log.error("Error al actualizar usuario: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para eliminar usuarios")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Petición para eliminar usuario con ID: {}", id);
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Error al eliminar usuario: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Asignar rol a usuario", description = "Asigna un rol a un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol asignado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para asignar roles")
    })
    @PostMapping("/assign-role")
    @PreAuthorize("hasAuthority('ASSIGN_ROLE')")
    public ResponseEntity<Void> assignRoleToUser(@Valid @RequestBody AssignRoleRequestDTO request) {
        log.info("Petición para asignar rol {} al usuario con ID: {}", request.getRoleName(), request.getUserId());
        try {
            userService.assignRoleToUser(request.getUserId(), request.getRoleName());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error al asignar rol: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Activar usuario", description = "Activa un usuario desactivado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario activado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para activar usuarios")
    })
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        log.info("Petición para activar usuario con ID: {}", id);
        try {
            userService.activateUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error al activar usuario: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Desactivar usuario", description = "Desactiva un usuario activo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario desactivado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No tiene permisos para desactivar usuarios")
    })
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('UPDATE_USER')")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        log.info("Petición para desactivar usuario con ID: {}", id);
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            log.error("Error al desactivar usuario: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
