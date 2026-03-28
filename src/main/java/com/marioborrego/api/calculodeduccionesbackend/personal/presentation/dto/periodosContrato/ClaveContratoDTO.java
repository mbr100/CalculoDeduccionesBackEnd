package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.periodosContrato;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.NaturalezaContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TipoJornada;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaveContratoDTO {
    private String clave;
    private String descripcion;
    private NaturalezaContrato naturaleza;
    private TipoJornada jornada;
    private Boolean cotizaDesempleo;
    private Boolean cotizaFogasa;
    private Boolean cotizaFp;
    private Boolean cotizaMei;
    private Boolean cotizaCcEstandar;
    private Boolean vigente;
}
