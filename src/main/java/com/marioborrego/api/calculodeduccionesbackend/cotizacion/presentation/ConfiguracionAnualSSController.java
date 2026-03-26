package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces.ConfiguracionAnualSSService;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.ConfiguracionAnualSSDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearConfiguracionAnualSSDTO;
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
@RequestMapping("/api/configuracion-anual-ss")
public class ConfiguracionAnualSSController {

    private final Logger log = LoggerFactory.getLogger(ConfiguracionAnualSSController.class);
    private final ConfiguracionAnualSSService service;

    public ConfiguracionAnualSSController(ConfiguracionAnualSSService service) {
        this.service = service;
    }

    @Operation(summary = "Listar configuraciones anuales SS")
    @GetMapping
    public ResponseEntity<List<ConfiguracionAnualSSDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @Operation(summary = "Obtener configuración SS por año")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuración encontrada"),
            @ApiResponse(responseCode = "404", description = "No encontrada para ese año")
    })
    @GetMapping("/{anio}")
    public ResponseEntity<ConfiguracionAnualSSDTO> obtenerPorAnio(@PathVariable Integer anio) {
        try {
            return ResponseEntity.ok(service.obtenerPorAnio(anio));
        } catch (RuntimeException e) {
            log.warn("No encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Crear configuración anual SS")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creada correctamente"),
            @ApiResponse(responseCode = "409", description = "Ya existe para ese año")
    })
    @PostMapping
    public ResponseEntity<ConfiguracionAnualSSDTO> crear(@Valid @RequestBody CrearConfiguracionAnualSSDTO dto) {
        log.info("Crear configuración SS para año {}", dto.getAnio());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
        } catch (RuntimeException e) {
            log.warn("Conflicto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Actualizar configuración anual SS")
    @PutMapping("/{id}")
    public ResponseEntity<ConfiguracionAnualSSDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CrearConfiguracionAnualSSDTO dto) {
        try {
            return ResponseEntity.ok(service.actualizar(id, dto));
        } catch (RuntimeException e) {
            log.warn("No encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Eliminar configuración anual SS")
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
