package com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces.ClaveOcupacionService;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.ClaveOcupacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearClaveOcupacionDTO;
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
@RequestMapping("/api/claves-ocupacion")
public class ClaveOcupacionController {

    private final Logger log = LoggerFactory.getLogger(ClaveOcupacionController.class);
    private final ClaveOcupacionService service;

    public ClaveOcupacionController(ClaveOcupacionService service) {
        this.service = service;
    }

    @Operation(summary = "Listar claves de ocupación")
    @GetMapping
    public ResponseEntity<List<ClaveOcupacionDTO>> listarTodos(@RequestParam(required = false) Boolean soloActivas) {
        List<ClaveOcupacionDTO> lista = Boolean.TRUE.equals(soloActivas)
                ? service.listarActivas()
                : service.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @Operation(summary = "Crear clave de ocupación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creada correctamente"),
            @ApiResponse(responseCode = "409", description = "Ya existe esa clave")
    })
    @PostMapping
    public ResponseEntity<ClaveOcupacionDTO> crear(@Valid @RequestBody CrearClaveOcupacionDTO dto) {
        log.info("Crear clave de ocupación '{}'", dto.getClave());
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
        } catch (RuntimeException e) {
            log.warn("Conflicto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Actualizar clave de ocupación")
    @PutMapping("/{clave}")
    public ResponseEntity<ClaveOcupacionDTO> actualizar(@PathVariable String clave, @Valid @RequestBody CrearClaveOcupacionDTO dto) {
        try {
            return ResponseEntity.ok(service.actualizar(clave, dto));
        } catch (RuntimeException e) {
            log.warn("No encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Operation(summary = "Eliminar clave de ocupación")
    @DeleteMapping("/{clave}")
    public ResponseEntity<Void> eliminar(@PathVariable String clave) {
        try {
            service.eliminar(clave);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.warn("No encontrada: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
