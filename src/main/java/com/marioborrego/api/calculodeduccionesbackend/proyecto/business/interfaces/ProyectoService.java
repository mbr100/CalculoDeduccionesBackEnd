package com.marioborrego.api.calculodeduccionesbackend.proyecto.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.ListadoDeProyectosResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProyectoService {
    Page<ListadoDeProyectosResponseDTO> listarProyectosPorEconomico(Pageable pageable, Long idEconomico);
}
