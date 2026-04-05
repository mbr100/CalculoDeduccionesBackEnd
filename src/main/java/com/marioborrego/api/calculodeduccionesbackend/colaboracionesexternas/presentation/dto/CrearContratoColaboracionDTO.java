package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.TipoContratoColaboradorasExternas;
import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearContratoColaboracionDTO {
    @NotNull(message = "El ID de la colaboradora es obligatorio")
    private Long idColaboradora;

    @NotBlank(message = "El nombre del contrato es obligatorio")
    private String nombreContrato;

    private String objeto;

    @NotNull(message = "El tipo de contrato es obligatorio")
    private TipoContratoColaboradorasExternas tipoContrato;

    @NotNull(message = "La validez es obligatoria")
    private ValidezIDI validez;

    @NotNull(message = "El importe cubierto es obligatorio")
    private Double importeCubierto;
}
