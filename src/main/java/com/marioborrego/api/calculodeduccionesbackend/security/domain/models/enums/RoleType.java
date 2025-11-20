package com.marioborrego.api.calculodeduccionesbackend.security.domain.models.enums;

/**
 * Enum que define los tipos de roles disponibles en el sistema
 */
public enum RoleType {
    /**
     * Super Administrador - Acceso total al sistema incluyendo gestión de usuarios, roles y permisos
     */
    SUPER_ADMIN("Super Administrador", "Acceso total al sistema"),

    /**
     * Administrador - Gestión de usuarios y datos del sistema (sin modificar roles/permisos)
     */
    ADMIN("Administrador", "Gestión de usuarios y datos del sistema"),

    /**
     * Gestor - Puede crear, editar y eliminar económicos, proyectos y personal
     */
    GESTOR("Gestor", "CRUD completo en económicos, proyectos y personal"),

    /**
     * Consultor - Solo lectura de datos
     */
    CONSULTOR("Consultor", "Solo lectura de datos"),

    /**
     * Usuario - Acceso básico de lectura
     */
    USUARIO("Usuario", "Acceso básico de lectura");

    private final String displayName;
    private final String description;

    RoleType(String displayName, String description) {
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
