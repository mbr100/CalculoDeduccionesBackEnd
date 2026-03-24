package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces.TipoCotizacionService;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearTipoCotizacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.TipoCotizacionDTO;
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
@RequestMapping("/api/tipos-cotizacion")
public class TipoCotizacionController {

    private final Logger log = LoggerFactory.getLogger(TipoCotizacionController.class);
    private final TipoCotizacionService tipoCotizacionService;

    public TipoCotizacionController(TipoCotizacionService tipoCotizacionService) {
        this.tipoCotizacionService = tipoCotizacionService;
    }

    @Operation(summary = "Listar tipos de cotización", description = "Obtiene todos los tipos de cotización, opcionalmente filtrados por anualidad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    })
    @GetMapping
    public ResponseEntity<List<TipoCotizacionDTO>> listarTodos(@RequestParam(required = false) Integer anualidad) {
        log.info("Petición para listar tipos de cotización. Anualidad: {}", anualidad);
        List<TipoCotizacionDTO> lista = tipoCotizacionService.listarTodos(anualidad);
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Obtener tipo de cotización por CNAE y anualidad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo de cotización encontrado"),
            @ApiResponse(responseCode = "404", description = "No encontrado para ese CNAE y anualidad")
    })
    @GetMapping("/{cnae}/{anualidad}")
    public ResponseEntity<TipoCotizacionDTO> obtenerPorCnaeYAnualidad(@PathVariable String cnae, @PathVariable Integer anualidad) {
        log.info("Petición para obtener tipo de cotización CNAE {} anualidad {}", cnae, anualidad);
        try {
            TipoCotizacionDTO dto = tipoCotizacionService.obtenerPorCnaeYAnualidad(cnae, anualidad);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            log.warn("No encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Comprobar existencia de tipo de cotización")
    @GetMapping("/{cnae}/{anualidad}/existe")
    public ResponseEntity<Boolean> existe(@PathVariable String cnae, @PathVariable Integer anualidad) {
        return ResponseEntity.ok(tipoCotizacionService.existePorCnaeYAnualidad(cnae, anualidad));
    }

    @Operation(summary = "Crear tipo de cotización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creado correctamente"),
            @ApiResponse(responseCode = "409", description = "Ya existe para ese CNAE y anualidad")
    })
    @PostMapping
    public ResponseEntity<TipoCotizacionDTO> crear(@Valid @RequestBody CrearTipoCotizacionDTO dto) {
        log.info("Petición para crear tipo de cotización CNAE {} anualidad {}", dto.getCnae(), dto.getAnualidad());
        try {
            TipoCotizacionDTO creado = tipoCotizacionService.crear(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (RuntimeException e) {
            log.warn("Conflicto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Actualizar tipo de cotización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TipoCotizacionDTO> actualizar(@PathVariable Long id, @Valid @RequestBody CrearTipoCotizacionDTO dto) {
        log.info("Petición para actualizar tipo de cotización ID {}", id);
        try {
            TipoCotizacionDTO actualizado = tipoCotizacionService.actualizar(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            log.warn("No encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Eliminar tipo de cotización")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "No encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        log.info("Petición para eliminar tipo de cotización ID {}", id);
        try {
            tipoCotizacionService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("No encontrado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
