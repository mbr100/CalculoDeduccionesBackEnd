package com.marioborrego.api.calculodeduccionesbackend.proyecto.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.ActualizacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.CrearProyectoDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.ListadoDeProyectosResponseDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.MatrizAsignacionesDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.enums.ProyectoCampo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProyectoService {
    Page<ListadoDeProyectosResponseDTO> listarProyectosPorEconomico(Pageable pageable, Long idEconomico);
    void crearProyecto(CrearProyectoDTO crearProyectoDTO);
    void actualizarProyecto(ActualizacionDTO<Object, ProyectoCampo> crearProyectoDTO);
    void eliminarProyecto(Long idProyecto);

    MatrizAsignacionesDTO listarPersonalPorProyectoAsignacion(Long idEconomico);
}
