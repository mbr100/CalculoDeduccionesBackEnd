package com.marioborrego.api.calculodeduccionesbackend.security.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar las operaciones de base de datos de los usuarios
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario
     */
    Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su email
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por username o email
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Verifica si existe un usuario con un username específico
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con un email específico
     */
    boolean existsByEmail(String email);

    /**
     * Obtiene todos los usuarios activos
     */
    List<User> findByActiveTrue();

    /**
     * Obtiene usuarios paginados
     */
    Page<User> findAll(Pageable pageable);

    /**
     * Busca usuarios por nombre, apellido, username o email
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsers(String search, Pageable pageable);

    /**
     * Obtiene todos los usuarios con sus roles
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles WHERE u.active = true")
    List<User> findAllWithRoles();
}
