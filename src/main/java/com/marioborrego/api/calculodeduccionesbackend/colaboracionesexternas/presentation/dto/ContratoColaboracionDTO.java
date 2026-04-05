package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.TipoContratoColaboradorasExternas;
import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoColaboracionDTO {
    private Long idContrato;
    private String nombreContrato;
    private String objeto;
    private TipoContratoColaboradorasExternas tipoContrato;
    private ValidezIDI validez;
    private Double importeCubierto;
    private Long idColaboradora;
    private String nombreColaboradora;
    private Double totalFacturado;
    private Boolean superaCobertura;
}
