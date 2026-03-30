package com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.business.impl.FaseProyectoServiceImpl;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.fases.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@Slf4j
public class FaseProyectoController {

    private final FaseProyectoServiceImpl faseProyectoService;

    public FaseProyectoController(FaseProyectoServiceImpl faseProyectoService) {
        this.faseProyectoService = faseProyectoService;
    }

    @Operation(summary = "Listar fases de un proyecto")
    @GetMapping("/{idProyecto}/fases")
    public ResponseEntity<List<FaseProyectoDTO>> listarFases(@PathVariable Long idProyecto) {
        log.info("Listando fases del proyecto {}", idProyecto);
        return ResponseEntity.ok(faseProyectoService.listarFases(idProyecto));
    }

    @Operation(summary = "Crear una fase en un proyecto")
    @PostMapping("/{idProyecto}/fases")
    public ResponseEntity<FaseProyectoDTO> crearFase(@PathVariable Long idProyecto, @RequestBody CrearFaseProyectoDTO dto) {
        log.info("Creando fase en proyecto {}", idProyecto);
        dto.setIdProyecto(idProyecto);
        try {
            FaseProyectoDTO fase = faseProyectoService.crearFase(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(fase);
        } catch (Exception e) {
            log.error("Error al crear fase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar nombre de una fase")
    @PutMapping("/fases/{idFase}")
    public ResponseEntity<FaseProyectoDTO> actualizarFase(@PathVariable Long idFase, @RequestBody ActualizarFaseProyectoDTO dto) {
        log.info("Actualizando fase {}", idFase);
        dto.setIdFase(idFase);
        try {
            FaseProyectoDTO fase = faseProyectoService.actualizarFase(dto);
            return ResponseEntity.ok(fase);
        } catch (Exception e) {
            log.error("Error al actualizar fase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar una fase")
    @DeleteMapping("/fases/{idFase}")
    public ResponseEntity<Void> eliminarFase(@PathVariable Long idFase) {
        log.info("Eliminando fase {}", idFase);
        try {
            faseProyectoService.eliminarFase(idFase);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar fase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener matriz de asignacion de fases por proyecto")
    @GetMapping("/{idProyecto}/fases/asignaciones")
    public ResponseEntity<MatrizAsignacionFasesDTO> obtenerMatrizAsignacionFases(@PathVariable Long idProyecto) {
        log.info("Obteniendo matriz de asignacion de fases del proyecto {}", idProyecto);
        try {
            MatrizAsignacionFasesDTO matriz = faseProyectoService.obtenerMatrizAsignacionFases(idProyecto);
            return ResponseEntity.ok(matriz);
        } catch (Exception e) {
            log.error("Error al obtener matriz de asignacion de fases: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar asignacion persona-fase")
    @PutMapping("/fases/asignaciones")
    public ResponseEntity<Void> actualizarAsignacionFase(@RequestBody ActualizarAsignacionFaseDTO dto) {
        log.info("Actualizando asignacion fase: proyectoPersonal={}, fase={}", dto.getIdProyectoPersonal(), dto.getIdFase());
        try {
            faseProyectoService.actualizarAsignacionFase(dto);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("Validacion fallida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al actualizar asignacion de fase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Resumen de gasto por fase de un proyecto")
    @GetMapping("/{idProyecto}/fases/resumen")
    public ResponseEntity<List<ResumenGastoFaseDTO>> resumenGastoPorFase(@PathVariable Long idProyecto) {
        log.info("Calculando resumen de gasto por fase del proyecto {}", idProyecto);
        try {
            return ResponseEntity.ok(faseProyectoService.calcularResumenGastoPorFase(idProyecto));
        } catch (Exception e) {
            log.error("Error al calcular resumen de gasto por fase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Desglose de gasto persona-fase de un proyecto")
    @GetMapping("/{idProyecto}/fases/desglose")
    public ResponseEntity<List<ResumenGastoFasePersonaDTO>> desgloseFasePersona(@PathVariable Long idProyecto) {
        log.info("Calculando desglose persona-fase del proyecto {}", idProyecto);
        try {
            return ResponseEntity.ok(faseProyectoService.calcularDesgloseFasePersona(idProyecto));
        } catch (Exception e) {
            log.error("Error al calcular desglose persona-fase: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
