package com.marioborrego.api.calculodeduccionesbackend.security.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Permission;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.enums.PermissionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones de base de datos de los permisos
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * Busca un permiso por su nombre (tipo)
     */
    Optional<Permission> findByName(PermissionType name);

    /**
     * Obtiene todos los permisos activos
     */
    List<Permission> findByActiveTrue();

    /**
     * Verifica si existe un permiso con un nombre específico
     */
    boolean existsByName(PermissionType name);

    /**
     * Busca permisos por múltiples nombres
     */
    List<Permission> findByNameIn(List<PermissionType> names);
}
