package com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private List<String> roles;
    private List<String> permissions;
}
