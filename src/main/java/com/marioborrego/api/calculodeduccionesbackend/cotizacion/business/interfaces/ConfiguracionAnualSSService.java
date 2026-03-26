package com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.ConfiguracionAnualSSDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearConfiguracionAnualSSDTO;

import java.util.List;

public interface ConfiguracionAnualSSService {

    ConfiguracionAnualSSDTO crear(CrearConfiguracionAnualSSDTO dto);

    ConfiguracionAnualSSDTO obtenerPorAnio(Integer anio);

    List<ConfiguracionAnualSSDTO> listarTodos();

    ConfiguracionAnualSSDTO actualizar(Long id, CrearConfiguracionAnualSSDTO dto);

    void eliminar(Long id);
}
