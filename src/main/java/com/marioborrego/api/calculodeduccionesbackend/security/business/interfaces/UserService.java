package com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.security.domain.models.User;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.CreateUserRequestDTO;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.UpdateUserRequestDTO;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserDTO createUser(CreateUserRequestDTO request);

    UserDTO updateUser(Long id, UpdateUserRequestDTO request);

    void deleteUser(Long id);

    UserDTO getUserById(Long id);

    UserDTO getUserByUsername(String username);

    Page<UserDTO> getAllUsers(Pageable pageable);

    Page<UserDTO> searchUsers(String search, Pageable pageable);

    List<UserDTO> getAllActiveUsers();

    void assignRoleToUser(Long userId, String roleName);

    void removeRoleFromUser(Long userId, String roleName);

    void activateUser(Long id);

    void deactivateUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    User getUserEntityByUsername(String username);
}
