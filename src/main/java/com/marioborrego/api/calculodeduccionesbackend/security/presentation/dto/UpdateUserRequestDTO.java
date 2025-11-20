package com.marioborrego.api.calculodeduccionesbackend.security.presentation.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {

    @Email(message = "El email debe ser válido")
    private String email;

    private String firstName;
    private String lastName;
    private Boolean active;
    private List<String> roles;
}
