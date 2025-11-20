package com.marioborrego.api.calculodeduccionesbackend.security.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces.RoleService;
import com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces.UserService;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Role;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.User;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.repository.UserRepository;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserDTO createUser(CreateUserRequestDTO request) {
        log.info("Creando nuevo usuario: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya existe: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .active(true)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .build();

        // Asignar roles
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            for (String roleName : request.getRoles()) {
                Role role = roleService.getRoleEntityByName(roleName);
                user.addRole(role);
            }
        }

        User savedUser = userRepository.save(user);
        log.info("Usuario creado exitosamente: {}", savedUser.getUsername());

        return convertToDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequestDTO request) {
        log.info("Actualizando usuario con ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("El email ya está registrado: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }

        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }

        if (request.getActive() != null) {
            user.setActive(request.getActive());
        }

        // Actualizar roles si se proporcionan
        if (request.getRoles() != null) {
            user.getRoles().clear();
            for (String roleName : request.getRoles()) {
                Role role = roleService.getRoleEntityByName(roleName);
                user.addRole(role);
            }
        }

        User updatedUser = userRepository.save(user);
        log.info("Usuario actualizado exitosamente: {}", updatedUser.getUsername());

        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Eliminando usuario con ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        userRepository.delete(user);
        log.info("Usuario eliminado exitosamente");
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDTO> searchUsers(String search, Pageable pageable) {
        return userRepository.searchUsers(search, pageable).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllActiveUsers() {
        return userRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignRoleToUser(Long userId, String roleName) {
        log.info("Asignando rol {} al usuario con ID: {}", roleName, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        Role role = roleService.getRoleEntityByName(roleName);
        user.addRole(role);
        userRepository.save(user);
        log.info("Rol asignado exitosamente");
    }

    @Override
    @Transactional
    public void removeRoleFromUser(Long userId, String roleName) {
        log.info("Removiendo rol {} del usuario con ID: {}", roleName, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        Role role = roleService.getRoleEntityByName(roleName);
        user.removeRole(role);
        userRepository.save(user);
        log.info("Rol removido exitosamente");
    }

    @Override
    @Transactional
    public void activateUser(Long id) {
        log.info("Activando usuario con ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setActive(true);
        userRepository.save(user);
        log.info("Usuario activado exitosamente");
    }

    @Override
    @Transactional
    public void deactivateUser(Long id) {
        log.info("Desactivando usuario con ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        user.setActive(false);
        userRepository.save(user);
        log.info("Usuario desactivado exitosamente");
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));
    }

    private UserDTO convertToDTO(User user) {
        List<RoleDTO> roleDTOs = user.getRoles().stream()
                .filter(Role::getActive)
                .map(this::convertRoleToDTO)
                .collect(Collectors.toList());

        List<String> permissions = user.getAllPermissions().stream()
                .map(p -> p.getName().name())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .active(user.getActive())
                .lastLoginDate(user.getLastLoginDate())
                .createdAt(user.getCreatedAt())
                .roles(roleDTOs)
                .permissions(permissions)
                .build();
    }

    private RoleDTO convertRoleToDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName().name())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .active(role.getActive())
                .isSystemRole(role.getIsSystemRole())
                .build();
    }
}
