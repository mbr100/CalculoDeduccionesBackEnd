package com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.amortizacion.business.interfaces.AmortizacionService;
import com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/amortizacion")
@Slf4j
public class AmortizacionController {

    private final AmortizacionService amortizacionService;

    public AmortizacionController(AmortizacionService amortizacionService) {
        this.amortizacionService = amortizacionService;
    }

    // ==================== ACTIVOS ====================

    @Operation(summary = "Listar activos amortizables de un económico")
    @GetMapping("/economico/{idEconomico}")
    public ResponseEntity<List<ActivoAmortizableDTO>> listarActivos(@PathVariable Long idEconomico) {
        log.info("Listando activos del económico {}", idEconomico);
        return ResponseEntity.ok(amortizacionService.listarActivos(idEconomico));
    }

    @Operation(summary = "Listar activos amortizables de un proyecto")
    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<ActivoAmortizableDTO>> listarActivosPorProyecto(@PathVariable Long idProyecto) {
        log.info("Listando activos del proyecto {}", idProyecto);
        return ResponseEntity.ok(amortizacionService.listarActivosPorProyecto(idProyecto));
    }

    @Operation(summary = "Crear activo amortizable")
    @PostMapping
    public ResponseEntity<ActivoAmortizableDTO> crearActivo(@Valid @RequestBody CrearActivoAmortizableDTO dto) {
        log.info("Creando activo amortizable: {} (económico {})", dto.getDescripcion(), dto.getIdEconomico());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(amortizacionService.crearActivo(dto));
        } catch (Exception e) {
            log.error("Error al crear activo amortizable: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar activo amortizable")
    @PutMapping
    public ResponseEntity<ActivoAmortizableDTO> actualizarActivo(@Valid @RequestBody ActualizarActivoAmortizableDTO dto) {
        log.info("Actualizando activo amortizable: {}", dto.getIdActivo());
        try {
            return ResponseEntity.ok(amortizacionService.actualizarActivo(dto));
        } catch (Exception e) {
            log.error("Error al actualizar activo amortizable: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar activo amortizable")
    @DeleteMapping("/{idActivo}")
    public ResponseEntity<Void> eliminarActivo(@PathVariable Long idActivo) {
        log.info("Eliminando activo amortizable: {}", idActivo);
        try {
            amortizacionService.eliminarActivo(idActivo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar activo amortizable: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== IMPUTACIONES A FASE ====================

    @Operation(summary = "Obtener imputaciones de un activo a fases")
    @GetMapping("/{idActivo}/imputaciones")
    public ResponseEntity<List<ImputacionActivoFaseDTO>> obtenerImputaciones(@PathVariable Long idActivo) {
        log.info("Obteniendo imputaciones del activo {}", idActivo);
        return ResponseEntity.ok(amortizacionService.obtenerImputaciones(idActivo));
    }

    @Operation(summary = "Crear/actualizar imputación activo - fase")
    @PutMapping("/imputaciones")
    public ResponseEntity<Void> actualizarImputacion(@Valid @RequestBody ActualizarImputacionActivoFaseDTO dto) {
        log.info("Actualizando imputación activo {} → fase {}", dto.getIdActivo(), dto.getIdFase());
        try {
            amortizacionService.actualizarImputacion(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar imputación de activo: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== RESUMEN ====================

    @Operation(summary = "Resumen de activos amortizables por proyecto")
    @GetMapping("/resumen/economico/{idEconomico}")
    public ResponseEntity<List<ResumenActivoProyectoDTO>> resumenPorEconomico(@PathVariable Long idEconomico) {
        log.info("Calculando resumen de activos del económico {}", idEconomico);
        return ResponseEntity.ok(amortizacionService.calcularResumenPorEconomico(idEconomico));
    }

    @Operation(summary = "Resumen de activos amortizables por fase de un proyecto")
    @GetMapping("/resumen/proyecto/{idProyecto}/fases")
    public ResponseEntity<List<ResumenActivoFaseDTO>> resumenPorFases(@PathVariable Long idProyecto) {
        log.info("Calculando resumen de activos por fases del proyecto {}", idProyecto);
        return ResponseEntity.ok(amortizacionService.calcularResumenPorFases(idProyecto));
    }
}
