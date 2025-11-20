package com.marioborrego.api.calculodeduccionesbackend.security.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.LoginRequestDTO;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.LoginResponseDTO;
import com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto.RegisterRequestDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);

    LoginResponseDTO register(RegisterRequestDTO request);

    void changePassword(String username, String oldPassword, String newPassword);
}
