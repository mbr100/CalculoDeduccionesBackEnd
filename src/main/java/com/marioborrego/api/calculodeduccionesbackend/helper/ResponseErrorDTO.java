package com.marioborrego.api.calculodeduccionesbackend.helper;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
public class ResponseErrorDTO {
    private String status;
    private String message;
}
