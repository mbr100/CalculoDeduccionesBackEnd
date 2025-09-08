package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.business.interfaces.ProyectoService;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.ListadoDeProyectosResponseDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/proyecto")
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }


    @GetMapping("/economico/{idEconomico}")
    public ResponseEntity<Page<ListadoDeProyectosResponseDTO>> listarProyectosEconomico(@PageableDefault(size = 20) Pageable pageable, @PathVariable Long idEconomico) {
        if (idEconomico == null || idEconomico <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Page<ListadoDeProyectosResponseDTO> proyectos = proyectoService.listarProyectosPorEconomico(pageable, idEconomico);

        return ResponseEntity.ok(proyectos);
    }


}
