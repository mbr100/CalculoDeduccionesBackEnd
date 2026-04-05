package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.TipoContratoColaboradorasExternas;
import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarContratoColaboracionDTO {
    @NotNull(message = "El ID del contrato es obligatorio")
    private Long idContrato;
    private String nombreContrato;
    private String objeto;
    private TipoContratoColaboradorasExternas tipoContrato;
    private ValidezIDI validez;
    private Double importeCubierto;
}
