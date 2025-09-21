package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.ActualizacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.business.interfaces.ProyectoService;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.ActualizarAsignacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.CrearProyectoDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.ListadoDeProyectosResponseDTO;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.MatrizAsignacionesDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.enums.ProyectoCampo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proyectos")
@Slf4j
public class ProyectoController {

    private final ProyectoService proyectoService;

    public ProyectoController(ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }


    @Operation(summary = "Listar proyectos por economico", description = "Obtiene una lista paginada de proyectos asociados a un economico específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de proyectos obtenida exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, ID del economico no proporcionado o inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/economico/{idEconomico}")
    public ResponseEntity<Page<ListadoDeProyectosResponseDTO>> listarProyectosEconomico(@PageableDefault Pageable pageable, @PathVariable Long idEconomico) {
        log.info("Recibiendo solicitud para listar proyectos del economico con ID: {}", idEconomico);
        if (idEconomico == null || idEconomico <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Page<ListadoDeProyectosResponseDTO> proyectos = proyectoService.listarProyectosPorEconomico(pageable, idEconomico);
        return ResponseEntity.ok(proyectos);
    }

    @Operation(summary = "Crear un nuevo proyecto", description = "Crea un nuevo proyecto asociado a un economico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Proyecto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos del proyecto incorrectos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping()
    public ResponseEntity<Void> creaeProyecto(@RequestBody CrearProyectoDTO crearProyectoDTO) {
        if (crearProyectoDTO.getIdProyecto() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            proyectoService.crearProyecto(crearProyectoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error("Error al crear el proyecto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar un campo de proyecto", description = "Actualiza un valor de un campo de un poryecto ya existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proyecto actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, datos de actualización incorrectos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping()
    public ResponseEntity<Void> actualizarProyecto(@RequestBody ActualizacionDTO<Object, ProyectoCampo> crearProyectoDTO) {
        log.info("Recibiendo solicitud para actualizar el proyecto con ID: {}", crearProyectoDTO.getId());
        if (crearProyectoDTO.getId() == null || crearProyectoDTO.getId() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            proyectoService.actualizarProyecto(crearProyectoDTO);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            log.error("Error al actualizar el proyecto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar un proyecto", description = "Elimina un proyecto existente por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Proyecto eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida, ID del proyecto no proporcionado o inválido"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{idProyecto}")
    public ResponseEntity<Void> eliminarProyecto(@PathVariable Long idProyecto) {
        log.info("Recibiendo solicitud para eliminar el proyecto con ID: {}", idProyecto);
        if (idProyecto == null || idProyecto <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            proyectoService.eliminarProyecto(idProyecto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            log.error("Error al eliminar el proyecto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/asignaciones/{idEconomico}")
    public ResponseEntity<MatrizAsignacionesDTO> listarPersonalPorProyectoAsignacion(@PathVariable Long idEconomico) {
        log.info("Recibiendo solicitud para listar matriz de asignaciones del economico con ID: {}", idEconomico);
        if (idEconomico == null || idEconomico <= 0) {
            return ResponseEntity.badRequest().build();
        }
        try {
            MatrizAsignacionesDTO m = proyectoService.listarPersonalPorProyectoAsignacion(idEconomico);
            if (m == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.status(HttpStatus.OK).body(m);
        } catch (Exception e){
            log.error("Error al obtener la matriz de asignaciones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/asignaciones")
    public ResponseEntity<Void> actualizarAsignaciones(@RequestBody ActualizarAsignacionDTO asignacionDTO) {
        log.info("Recibiendo solicitud para actualizar la matriz de asignaciones");
        if (asignacionDTO == null || asignacionDTO.getIdProyecto() == null || asignacionDTO.getIdPersonal() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try {
            proyectoService.actualizarAsignaciones(asignacionDTO);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            log.error("Error al actualizar la matriz de asignaciones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
