package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonificacionesEmpleadoEconomicoDTO {
    private Long idPersona;
    private String nombre;
    private String dni;
    private Long idBonificacionTrabajador;
    private TiposBonificacion tipoBonificacion;
    private BigDecimal porcentajeBonificacion;

}
