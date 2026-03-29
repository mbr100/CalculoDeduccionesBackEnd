package com.marioborrego.api.calculodeduccionesbackend.configuration;

import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Permission;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Role;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.User;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.enums.PermissionType;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.enums.RoleType;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.repository.PermissionRepository;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.repository.RoleRepository;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Inicializa la base de datos con roles, permisos y un usuario administrador por defecto
 */
@Component
public class SecurityDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SecurityDataLoader.class);

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityDataLoader(PermissionRepository permissionRepository,
                             RoleRepository roleRepository,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Iniciando carga de datos de seguridad...");

        // Crear permisos
        Map<PermissionType, Permission> permissions = createPermissions();

        // Crear roles y asignar permisos
        Map<RoleType, Role> roles = createRoles(permissions);

        // Crear usuario administrador por defecto
        createDefaultAdminUser(roles);

        log.info("Carga de datos de seguridad completada");
    }

    private Map<PermissionType, Permission> createPermissions() {
        log.info("Creando permisos...");
        Map<PermissionType, Permission> permissionsMap = new HashMap<>();

        for (PermissionType permissionType : PermissionType.values()) {
            if (!permissionRepository.existsByName(permissionType)) {
                Permission permission = Permission.builder()
                        .name(permissionType)
                        .displayName(permissionType.getDisplayName())
                        .description(permissionType.getDescription())
                        .active(true)
                        .build();
                permission = permissionRepository.save(permission);
                permissionsMap.put(permissionType, permission);
                log.info("Permiso creado: {}", permissionType.name());
            } else {
                Permission permission = permissionRepository.findByName(permissionType).orElseThrow();
                permissionsMap.put(permissionType, permission);
            }
        }

        return permissionsMap;
    }

    private Map<RoleType, Role> createRoles(Map<PermissionType, Permission> permissions) {
        log.info("Creando roles...");
        Map<RoleType, Role> rolesMap = new HashMap<>();

        // SUPER_ADMIN - Todos los permisos
        rolesMap.put(RoleType.SUPER_ADMIN, createOrUpdateRole(
                RoleType.SUPER_ADMIN,
                new HashSet<>(permissions.values()),
                true
        ));

        // ADMIN - Todos excepto gestión avanzada de roles y permisos
        Set<Permission> adminPermissions = new HashSet<>();
        for (PermissionType pt : PermissionType.values()) {
            if (pt != PermissionType.CREATE_ROLE &&
                pt != PermissionType.UPDATE_ROLE &&
                pt != PermissionType.DELETE_ROLE) {
                adminPermissions.add(permissions.get(pt));
            }
        }
        rolesMap.put(RoleType.ADMIN, createOrUpdateRole(RoleType.ADMIN, adminPermissions, true));

        // GESTOR - CRUD en económicos, proyectos y personal
        Set<Permission> gestorPermissions = new HashSet<>(Arrays.asList(
                permissions.get(PermissionType.CREATE_ECONOMICO),
                permissions.get(PermissionType.READ_ECONOMICO),
                permissions.get(PermissionType.UPDATE_ECONOMICO),
                permissions.get(PermissionType.DELETE_ECONOMICO),
                permissions.get(PermissionType.CREATE_PROYECTO),
                permissions.get(PermissionType.READ_PROYECTO),
                permissions.get(PermissionType.UPDATE_PROYECTO),
                permissions.get(PermissionType.DELETE_PROYECTO),
                permissions.get(PermissionType.CREATE_PERSONAL),
                permissions.get(PermissionType.READ_PERSONAL),
                permissions.get(PermissionType.UPDATE_PERSONAL),
                permissions.get(PermissionType.DELETE_PERSONAL),
                permissions.get(PermissionType.READ_USER)
        ));
        rolesMap.put(RoleType.GESTOR, createOrUpdateRole(RoleType.GESTOR, gestorPermissions, true));

        // CONSULTOR - Solo lectura
        Set<Permission> consultorPermissions = new HashSet<>(Arrays.asList(
                permissions.get(PermissionType.READ_ECONOMICO),
                permissions.get(PermissionType.READ_PROYECTO),
                permissions.get(PermissionType.READ_PERSONAL),
                permissions.get(PermissionType.READ_USER),
                permissions.get(PermissionType.READ_ROLE),
                permissions.get(PermissionType.READ_PERMISSION)
        ));
        rolesMap.put(RoleType.CONSULTOR, createOrUpdateRole(RoleType.CONSULTOR, consultorPermissions, true));

        // USUARIO - Permisos básicos de lectura
        Set<Permission> usuarioPermissions = new HashSet<>(Arrays.asList(
                permissions.get(PermissionType.READ_ECONOMICO),
                permissions.get(PermissionType.READ_PROYECTO),
                permissions.get(PermissionType.READ_PERSONAL)
        ));
        rolesMap.put(RoleType.USUARIO, createOrUpdateRole(RoleType.USUARIO, usuarioPermissions, true));

        return rolesMap;
    }

    private Role createOrUpdateRole(RoleType roleType, Set<Permission> permissions, boolean isSystemRole) {
        Optional<Role> existingRole = roleRepository.findByName(roleType);

        if (existingRole.isPresent()) {
            Role role = existingRole.get();
            role.getPermissions().clear();
            role.getPermissions().addAll(permissions);
            role = roleRepository.save(role);
            log.info("Rol actualizado: {}", roleType.name());
            return role;
        } else {
            Role role = Role.builder()
                    .name(roleType)
                    .displayName(roleType.getDisplayName())
                    .description(roleType.getDescription())
                    .active(true)
                    .isSystemRole(isSystemRole)
                    .permissions(permissions)
                    .build();
            role = roleRepository.save(role);
            log.info("Rol creado: {}", roleType.name());
            return role;
        }
    }

    private void createDefaultAdminUser(Map<RoleType, Role> roles) {
        String defaultUsername = "admin";
        String defaultPassword = "admin123";

        if (!userRepository.existsByUsername(defaultUsername)) {
            log.info("Creando usuario administrador por defecto...");

            User admin = User.builder()
                    .username(defaultUsername)
                    .email("admin@calculodeducciones.com")
                    .password(passwordEncoder.encode(defaultPassword))
                    .firstName("Administrador")
                    .lastName("Sistema")
                    .active(true)
                    .accountNonLocked(true)
                    .accountNonExpired(true)
                    .credentialsNonExpired(true)
                    .build();

            admin.addRole(roles.get(RoleType.SUPER_ADMIN));
            userRepository.save(admin);

            log.info("Usuario administrador creado - Username: {}, Password: {}", defaultUsername, defaultPassword);
            log.warn("IMPORTANTE: Cambie la contraseña del usuario administrador por defecto en producción");
        } else {
            log.info("Usuario administrador ya existe");
        }
    }
}
