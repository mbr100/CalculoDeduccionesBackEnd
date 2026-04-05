package com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.materialfungible.business.interfaces.MaterialFungibleService;
import com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materiales")
@Slf4j
public class MaterialFungibleController {

    private final MaterialFungibleService materialFungibleService;

    public MaterialFungibleController(MaterialFungibleService materialFungibleService) {
        this.materialFungibleService = materialFungibleService;
    }

    // ==================== FACTURAS ====================

    @Operation(summary = "Listar facturas de material fungible de un económico")
    @GetMapping("/economico/{idEconomico}")
    public ResponseEntity<List<FacturaMaterialDTO>> listarFacturas(@PathVariable Long idEconomico) {
        log.info("Listando facturas de material del económico {}", idEconomico);
        return ResponseEntity.ok(materialFungibleService.listarFacturas(idEconomico));
    }

    @Operation(summary = "Listar facturas de material fungible de un proyecto")
    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<List<FacturaMaterialDTO>> listarFacturasPorProyecto(@PathVariable Long idProyecto) {
        log.info("Listando facturas de material del proyecto {}", idProyecto);
        return ResponseEntity.ok(materialFungibleService.listarFacturasPorProyecto(idProyecto));
    }

    @Operation(summary = "Crear factura de material fungible")
    @PostMapping
    public ResponseEntity<FacturaMaterialDTO> crearFactura(@Valid @RequestBody CrearFacturaMaterialDTO dto) {
        log.info("Creando factura de material: {} (económico {})", dto.getNumeroFactura(), dto.getIdEconomico());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(materialFungibleService.crearFactura(dto));
        } catch (Exception e) {
            log.error("Error al crear factura de material: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar factura de material fungible")
    @PutMapping
    public ResponseEntity<FacturaMaterialDTO> actualizarFactura(@Valid @RequestBody ActualizarFacturaMaterialDTO dto) {
        log.info("Actualizando factura de material: {}", dto.getIdFactura());
        try {
            return ResponseEntity.ok(materialFungibleService.actualizarFactura(dto));
        } catch (Exception e) {
            log.error("Error al actualizar factura de material: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar factura de material fungible")
    @DeleteMapping("/{idFactura}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long idFactura) {
        log.info("Eliminando factura de material: {}", idFactura);
        try {
            materialFungibleService.eliminarFactura(idFactura);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar factura de material: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== IMPUTACIONES A FASE ====================

    @Operation(summary = "Obtener imputaciones de una factura de material a fases")
    @GetMapping("/{idFactura}/imputaciones")
    public ResponseEntity<List<ImputacionMaterialFaseDTO>> obtenerImputaciones(@PathVariable Long idFactura) {
        log.info("Obteniendo imputaciones de factura de material {}", idFactura);
        return ResponseEntity.ok(materialFungibleService.obtenerImputaciones(idFactura));
    }

    @Operation(summary = "Crear/actualizar imputación factura material - fase")
    @PutMapping("/imputaciones")
    public ResponseEntity<Void> actualizarImputacion(@Valid @RequestBody ActualizarImputacionMaterialFaseDTO dto) {
        log.info("Actualizando imputación factura material {} → fase {}", dto.getIdFactura(), dto.getIdFase());
        try {
            materialFungibleService.actualizarImputacion(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar imputación de material: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== RESUMEN ====================

    @Operation(summary = "Resumen de materiales fungibles por proyecto")
    @GetMapping("/resumen/economico/{idEconomico}")
    public ResponseEntity<List<ResumenMaterialProyectoDTO>> resumenPorEconomico(@PathVariable Long idEconomico) {
        log.info("Calculando resumen de materiales del económico {}", idEconomico);
        return ResponseEntity.ok(materialFungibleService.calcularResumenPorEconomico(idEconomico));
    }

    @Operation(summary = "Resumen de materiales fungibles por fase de un proyecto")
    @GetMapping("/resumen/proyecto/{idProyecto}/fases")
    public ResponseEntity<List<ResumenMaterialFaseDTO>> resumenPorFases(@PathVariable Long idProyecto) {
        log.info("Calculando resumen de materiales por fases del proyecto {}", idProyecto);
        return ResponseEntity.ok(materialFungibleService.calcularResumenPorFases(idProyecto));
    }
}
