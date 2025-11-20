package com.marioborrego.api.calculodeduccionesbackend.security.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces.AuthService;
import com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces.RoleService;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.Role;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.User;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.enums.RoleType;
import com.marioborrego.api.calculodeduccionesbackend.security.domain.repository.UserRepository;
import com.marioborrego.api.calculodeduccionesbackend.security.infrastructure.CustomUserDetails;
import com.marioborrego.api.calculodeduccionesbackend.security.infrastructure.JwtService;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.LoginRequestDTO;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.LoginResponseDTO;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.RegisterRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                          RoleService roleService,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Intento de login para usuario: {}", request.getUsername());

        // Autenticar usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // Actualizar fecha de último login
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);

        // Generar token JWT
        String token = jwtService.generateToken(userDetails);

        // Obtener roles y permisos
        List<String> roles = user.getRoles().stream()
                .filter(Role::getActive)
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        List<String> permissions = user.getAllPermissions().stream()
                .map(p -> p.getName().name())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        log.info("Login exitoso para usuario: {}", user.getUsername());

        return LoginResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Override
    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO request) {
        log.info("Registrando nuevo usuario: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("El username ya existe: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado: " + request.getEmail());
        }

        // Crear usuario
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

        // Asignar rol USUARIO por defecto
        Role defaultRole = roleService.getRoleEntityByName(RoleType.USUARIO.name());
        user.addRole(defaultRole);

        User savedUser = userRepository.save(user);

        // Generar token
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);
        String token = jwtService.generateToken(userDetails);

        // Obtener roles y permisos
        List<String> roles = savedUser.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList());

        List<String> permissions = savedUser.getAllPermissions().stream()
                .map(p -> p.getName().name())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        log.info("Usuario registrado exitosamente: {}", savedUser.getUsername());

        return LoginResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .roles(roles)
                .permissions(permissions)
                .build();
    }

    @Override
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Cambiando contraseña para usuario: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("La contraseña actual es incorrecta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("Contraseña cambiada exitosamente para usuario: {}", username);
    }
}
