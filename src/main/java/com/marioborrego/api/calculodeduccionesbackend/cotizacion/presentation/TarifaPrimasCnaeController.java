package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces.TarifaPrimasCnaeService;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearTarifaPrimasCnaeDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.TarifaPrimasCnaeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarifa-primas-cnae")
public class TarifaPrimasCnaeController {

    private final Logger log = LoggerFactory.getLogger(TarifaPrimasCnaeController.class);
    private final TarifaPrimasCnaeService service;

    public TarifaPrimasCnaeController(TarifaPrimasCnaeService service) {
        this.service = service;
    }

    @Operation(summary = "Listar tarifas de primas por CNAE")
    @GetMapping
    public ResponseEntity<List<TarifaPrimasCnaeDTO>> listarTodos(@RequestParam(required = false) Integer anio) {
        return ResponseEntity.ok(service.listarTodos(anio));
    }

    @Operation(summary = "Obtener tarifa por CNAE y año")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarifa encontrada"),
            @ApiResponse(responseCode = "404", description = "No encontrada")
    })
    @GetMapping("/{cnae}/{anio}")
    public ResponseEntity<TarifaPrimasCnaeDTO> obtenerPorCnaeYAnio(@PathVariable String cnae, @PathVariable Integer anio) {
        try {
            return ResponseEntity.ok(service.obtenerPorCnaeYAnio(cnae, anio));
        } catch (RuntimeException e) {
            log.warn("No encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Comprobar existencia de tarifa")
    @GetMapping("/{cnae}/{anio}/existe")
    public ResponseEntity<Boolean> existe(@PathVariable String cnae, @PathVariable Integer anio) {
        return ResponseEntity.ok(service.existePorCnaeYAnio(cnae, anio));
    }

    @Operation(summary = "Crear tarifa de primas CNAE")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creada correctamente"),
            @ApiResponse(responseCode = "409", description = "Ya existe para ese CNAE y año")
    })
    @PostMapping
    public ResponseEntity<TarifaPrimasCnaeDTO> crear(@Valid @RequestBody CrearTarifaPrimasCnaeDTO dto) {
        log.info("Crear tarifa CNAE {} año {}", dto.getCnae(), dto.getAnio());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
        } catch (RuntimeException e) {
            log.warn("Conflicto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Actualizar tarifa de primas CNAE")
    @PutMapping("/{id}")
    public ResponseEntity<TarifaPrimasCnaeDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CrearTarifaPrimasCnaeDTO dto) {
        try {
            return ResponseEntity.ok(service.actualizar(id, dto));
        } catch (RuntimeException e) {
            log.warn("No encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Eliminar tarifa de primas CNAE")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            service.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("No encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
