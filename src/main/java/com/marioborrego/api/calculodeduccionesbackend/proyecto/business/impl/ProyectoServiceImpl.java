package com.marioborrego.api.calculodeduccionesbackend.proyecto.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.business.interfaces.ProyectoService;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.Proyecto;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.ProyectoRepository;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.ListadoDeProyectosResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProyectoServiceImpl implements ProyectoService {
    private final ProyectoRepository proyectoRepository;


    public ProyectoServiceImpl(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    @Override
    public Page<ListadoDeProyectosResponseDTO> listarProyectosPorEconomico(Pageable pageable, Long idEconomico) {
        if (idEconomico == null || idEconomico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        Page<Proyecto> proyectos = proyectoRepository.findProyectosByEconomico(pageable, idEconomico);
        if (proyectos.isEmpty()) {
            return Page.empty();
        } else {
            return proyectos.map(proyecto -> ListadoDeProyectosResponseDTO.builder()
                    .idProyecto(proyecto.getIdProyecto())
                    .acronimo(proyecto.getAcronimo())
                    .titulo(proyecto.getTitulo())
                    .fechaInicio(proyecto.getFechaInicio())
                    .fechaFin(proyecto.getFechaFin())
                    .estrategia(proyecto.getEstrategia())
                    .calificacion(proyecto.getCalificacion())
                    .build());
        }
    }
}
