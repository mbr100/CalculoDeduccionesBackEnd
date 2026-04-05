package com.marioborrego.api.calculodeduccionesbackend.amortizacion.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.dto.*;

import java.util.List;

public interface AmortizacionService {

    // Activos
    List<ActivoAmortizableDTO> listarActivos(Long idEconomico);
    List<ActivoAmortizableDTO> listarActivosPorProyecto(Long idProyecto);
    ActivoAmortizableDTO crearActivo(CrearActivoAmortizableDTO dto);
    ActivoAmortizableDTO actualizarActivo(ActualizarActivoAmortizableDTO dto);
    void eliminarActivo(Long idActivo);

    // Imputaciones
    List<ImputacionActivoFaseDTO> obtenerImputaciones(Long idActivo);
    void actualizarImputacion(ActualizarImputacionActivoFaseDTO dto);

    // Resumen
    List<ResumenActivoProyectoDTO> calcularResumenPorEconomico(Long idEconomico);
    List<ResumenActivoFaseDTO> calcularResumenPorFases(Long idProyecto);
}
