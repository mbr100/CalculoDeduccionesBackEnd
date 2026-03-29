package com.marioborrego.api.calculodeduccionesbackend.security.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Role;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones de base de datos de los roles
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Busca un rol por su nombre (tipo)
     */
    Optional<Role> findByName(RoleType name);

    /**
     * Obtiene todos los roles activos
     */
    List<Role> findByActiveTrue();

    /**
     * Verifica si existe un rol con un nombre específico
     */
    boolean existsByName(RoleType name);

    /**
     * Busca roles por múltiples nombres
     */
    List<Role> findByNameIn(List<RoleType> names);

    /**
     * Obtiene todos los roles del sistema (no modificables)
     */
    List<Role> findByIsSystemRoleTrue();

    /**
     * Obtiene todos los roles con sus permisos
     */
    @Query("SELECT DISTINCT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.active = true")
    List<Role> findAllWithPermissions();
}
