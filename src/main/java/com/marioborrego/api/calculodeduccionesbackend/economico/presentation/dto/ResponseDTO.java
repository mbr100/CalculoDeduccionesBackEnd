package com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDTO {
    private String status;
    private String message;
}
