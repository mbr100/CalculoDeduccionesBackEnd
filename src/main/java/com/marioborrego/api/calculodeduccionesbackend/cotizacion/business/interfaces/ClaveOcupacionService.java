package com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.ClaveOcupacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearClaveOcupacionDTO;

import java.util.List;

public interface ClaveOcupacionService {

    ClaveOcupacionDTO crear(CrearClaveOcupacionDTO dto);

    List<ClaveOcupacionDTO> listarTodos();

    List<ClaveOcupacionDTO> listarActivas();

    ClaveOcupacionDTO actualizar(String clave, CrearClaveOcupacionDTO dto);

    void eliminar(String clave);
}
