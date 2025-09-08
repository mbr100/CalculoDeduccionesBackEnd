package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.retribuciones;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RetribucionesPersonalDTO {
    private Long idPersonal;
    private String nombre;
    private String dni;
    private Long idRetribucion;
    private Long importeRetribucionNoIT;
    private Long importeRetribucionExpecie;
    private Long aportacionesPrevencionSocial;
    private Long dietasViajeExentas;
    private Long rentasExentas190;
}
