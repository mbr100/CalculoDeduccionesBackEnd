package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.business.interfaces.ColaboracionesService;
import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colaboraciones")
@Slf4j
public class ColaboracionesController {

    private final ColaboracionesService colaboracionesService;

    public ColaboracionesController(ColaboracionesService colaboracionesService) {
        this.colaboracionesService = colaboracionesService;
    }

    // ==================== COLABORADORAS ====================

    @Operation(summary = "Listar colaboradoras de un económico")
    @GetMapping("/economico/{idEconomico}")
    public ResponseEntity<List<ColaboradoraDTO>> listarColaboradoras(@PathVariable Long idEconomico) {
        log.info("Listando colaboradoras del económico {}", idEconomico);
        return ResponseEntity.ok(colaboracionesService.listarColaboradoras(idEconomico));
    }

    @Operation(summary = "Crear colaboradora")
    @PostMapping
    public ResponseEntity<ColaboradoraDTO> crearColaboradora(@Valid @RequestBody CrearColaboradoraDTO dto) {
        log.info("Creando colaboradora: {}", dto.getCif());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(colaboracionesService.crearColaboradora(dto));
        } catch (Exception e) {
            log.error("Error al crear colaboradora: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar colaboradora")
    @PutMapping
    public ResponseEntity<ColaboradoraDTO> actualizarColaboradora(@Valid @RequestBody ActualizarColaboradoraDTO dto) {
        log.info("Actualizando colaboradora: {}", dto.getIdColaboradora());
        try {
            return ResponseEntity.ok(colaboracionesService.actualizarColaboradora(dto));
        } catch (Exception e) {
            log.error("Error al actualizar colaboradora: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar colaboradora (cascade contratos y facturas)")
    @DeleteMapping("/{idColaboradora}")
    public ResponseEntity<Void> eliminarColaboradora(@PathVariable Long idColaboradora) {
        log.info("Eliminando colaboradora: {}", idColaboradora);
        try {
            colaboracionesService.eliminarColaboradora(idColaboradora);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar colaboradora: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== CONTRATOS ====================

    @Operation(summary = "Listar contratos de un económico")
    @GetMapping("/contratos/economico/{idEconomico}")
    public ResponseEntity<List<ContratoColaboracionDTO>> listarContratos(@PathVariable Long idEconomico) {
        log.info("Listando contratos del económico {}", idEconomico);
        return ResponseEntity.ok(colaboracionesService.listarContratos(idEconomico));
    }

    @Operation(summary = "Listar contratos de una colaboradora")
    @GetMapping("/contratos/colaboradora/{idColaboradora}")
    public ResponseEntity<List<ContratoColaboracionDTO>> listarContratosPorColaboradora(@PathVariable Long idColaboradora) {
        log.info("Listando contratos de colaboradora {}", idColaboradora);
        return ResponseEntity.ok(colaboracionesService.listarContratosPorColaboradora(idColaboradora));
    }

    @Operation(summary = "Crear contrato")
    @PostMapping("/contratos")
    public ResponseEntity<ContratoColaboracionDTO> crearContrato(@Valid @RequestBody CrearContratoColaboracionDTO dto) {
        log.info("Creando contrato para colaboradora {}", dto.getIdColaboradora());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(colaboracionesService.crearContrato(dto));
        } catch (Exception e) {
            log.error("Error al crear contrato: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar contrato")
    @PutMapping("/contratos")
    public ResponseEntity<ContratoColaboracionDTO> actualizarContrato(@Valid @RequestBody ActualizarContratoColaboracionDTO dto) {
        log.info("Actualizando contrato: {}", dto.getIdContrato());
        try {
            return ResponseEntity.ok(colaboracionesService.actualizarContrato(dto));
        } catch (Exception e) {
            log.error("Error al actualizar contrato: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar contrato (desvincula facturas)")
    @DeleteMapping("/contratos/{idContrato}")
    public ResponseEntity<Void> eliminarContrato(@PathVariable Long idContrato) {
        log.info("Eliminando contrato: {}", idContrato);
        try {
            colaboracionesService.eliminarContrato(idContrato);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar contrato: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== FACTURAS ====================

    @Operation(summary = "Listar facturas de un económico")
    @GetMapping("/facturas/economico/{idEconomico}")
    public ResponseEntity<List<FacturaColaboracionDTO>> listarFacturas(@PathVariable Long idEconomico) {
        log.info("Listando facturas del económico {}", idEconomico);
        return ResponseEntity.ok(colaboracionesService.listarFacturas(idEconomico));
    }

    @Operation(summary = "Listar facturas de una colaboradora")
    @GetMapping("/facturas/colaboradora/{idColaboradora}")
    public ResponseEntity<List<FacturaColaboracionDTO>> listarFacturasPorColaboradora(@PathVariable Long idColaboradora) {
        log.info("Listando facturas de colaboradora {}", idColaboradora);
        return ResponseEntity.ok(colaboracionesService.listarFacturasPorColaboradora(idColaboradora));
    }

    @Operation(summary = "Listar facturas de un proyecto")
    @GetMapping("/facturas/proyecto/{idProyecto}")
    public ResponseEntity<List<FacturaColaboracionDTO>> listarFacturasPorProyecto(@PathVariable Long idProyecto) {
        log.info("Listando facturas del proyecto {}", idProyecto);
        return ResponseEntity.ok(colaboracionesService.listarFacturasPorProyecto(idProyecto));
    }

    @Operation(summary = "Listar facturas de un contrato")
    @GetMapping("/facturas/contrato/{idContrato}")
    public ResponseEntity<List<FacturaColaboracionDTO>> listarFacturasPorContrato(@PathVariable Long idContrato) {
        log.info("Listando facturas del contrato {}", idContrato);
        return ResponseEntity.ok(colaboracionesService.listarFacturasPorContrato(idContrato));
    }

    @Operation(summary = "Crear factura")
    @PostMapping("/facturas")
    public ResponseEntity<FacturaColaboracionDTO> crearFactura(@Valid @RequestBody CrearFacturaColaboracionDTO dto) {
        log.info("Creando factura para colaboradora {}", dto.getIdColaboradora());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(colaboracionesService.crearFactura(dto));
        } catch (Exception e) {
            log.error("Error al crear factura: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar factura")
    @PutMapping("/facturas")
    public ResponseEntity<FacturaColaboracionDTO> actualizarFactura(@Valid @RequestBody ActualizarFacturaColaboracionDTO dto) {
        log.info("Actualizando factura: {}", dto.getIdFactura());
        try {
            return ResponseEntity.ok(colaboracionesService.actualizarFactura(dto));
        } catch (Exception e) {
            log.error("Error al actualizar factura: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar factura")
    @DeleteMapping("/facturas/{idFactura}")
    public ResponseEntity<Void> eliminarFactura(@PathVariable Long idFactura) {
        log.info("Eliminando factura: {}", idFactura);
        try {
            colaboracionesService.eliminarFactura(idFactura);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al eliminar factura: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== IMPUTACIONES A FASE ====================

    @Operation(summary = "Obtener imputaciones de una factura a fases")
    @GetMapping("/facturas/{idFactura}/imputaciones")
    public ResponseEntity<List<ImputacionFacturaFaseDTO>> obtenerImputaciones(@PathVariable Long idFactura) {
        log.info("Obteniendo imputaciones de factura {}", idFactura);
        return ResponseEntity.ok(colaboracionesService.obtenerImputacionesPorFactura(idFactura));
    }

    @Operation(summary = "Crear/actualizar imputación factura-fase")
    @PutMapping("/facturas/imputaciones")
    public ResponseEntity<Void> actualizarImputacion(@Valid @RequestBody ActualizarImputacionFacturaFaseDTO dto) {
        log.info("Actualizando imputación factura {} → fase {}", dto.getIdFactura(), dto.getIdFase());
        try {
            colaboracionesService.actualizarImputacionFase(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error al actualizar imputación: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== RESUMEN ====================

    @Operation(summary = "Resumen de colaboraciones por proyecto")
    @GetMapping("/resumen/economico/{idEconomico}")
    public ResponseEntity<List<ResumenColaboracionesProyectoDTO>> resumenPorEconomico(@PathVariable Long idEconomico) {
        log.info("Calculando resumen de colaboraciones del económico {}", idEconomico);
        return ResponseEntity.ok(colaboracionesService.calcularResumenPorEconomico(idEconomico));
    }

    @Operation(summary = "Resumen de colaboraciones por fase de un proyecto")
    @GetMapping("/resumen/proyecto/{idProyecto}/fases")
    public ResponseEntity<List<ResumenColaboracionesFaseDTO>> resumenPorFases(@PathVariable Long idProyecto) {
        log.info("Calculando resumen de colaboraciones por fases del proyecto {}", idProyecto);
        return ResponseEntity.ok(colaboracionesService.calcularResumenPorFases(idProyecto));
    }
}
