package com.marioborrego.api.calculodeduccionesbackend.security.domain.models.enums;

/**
 * Enum que define los tipos de permisos disponibles en el sistema
 * Organizados por módulos y acciones CRUD
 */
public enum PermissionType {
    // Permisos de Económicos
    CREATE_ECONOMICO("Crear Económico", "Permite crear nuevos económicos"),
    READ_ECONOMICO("Leer Económico", "Permite visualizar económicos"),
    UPDATE_ECONOMICO("Actualizar Económico", "Permite modificar económicos existentes"),
    DELETE_ECONOMICO("Eliminar Económico", "Permite eliminar económicos"),

    // Permisos de Proyectos
    CREATE_PROYECTO("Crear Proyecto", "Permite crear nuevos proyectos"),
    READ_PROYECTO("Leer Proyecto", "Permite visualizar proyectos"),
    UPDATE_PROYECTO("Actualizar Proyecto", "Permite modificar proyectos existentes"),
    DELETE_PROYECTO("Eliminar Proyecto", "Permite eliminar proyectos"),

    // Permisos de Personal
    CREATE_PERSONAL("Crear Personal", "Permite crear nuevos registros de personal"),
    READ_PERSONAL("Leer Personal", "Permite visualizar personal"),
    UPDATE_PERSONAL("Actualizar Personal", "Permite modificar registros de personal"),
    DELETE_PERSONAL("Eliminar Personal", "Permite eliminar registros de personal"),

    // Permisos de Usuarios
    CREATE_USER("Crear Usuario", "Permite crear nuevos usuarios"),
    READ_USER("Leer Usuario", "Permite visualizar usuarios"),
    UPDATE_USER("Actualizar Usuario", "Permite modificar usuarios existentes"),
    DELETE_USER("Eliminar Usuario", "Permite eliminar usuarios"),

    // Permisos de Roles
    CREATE_ROLE("Crear Rol", "Permite crear nuevos roles"),
    READ_ROLE("Leer Rol", "Permite visualizar roles"),
    UPDATE_ROLE("Actualizar Rol", "Permite modificar roles existentes"),
    DELETE_ROLE("Eliminar Rol", "Permite eliminar roles"),
    ASSIGN_ROLE("Asignar Rol", "Permite asignar roles a usuarios"),

    // Permisos de Permisos (meta-permisos)
    READ_PERMISSION("Leer Permiso", "Permite visualizar permisos"),
    ASSIGN_PERMISSION("Asignar Permiso", "Permite asignar permisos a roles");

    private final String displayName;
    private final String description;

    PermissionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
