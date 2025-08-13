package com.marioborrego.api.calculodeduccionesbackend.empresa.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.empresa.business.interfaces.EconomicoService;
import com.marioborrego.api.calculodeduccionesbackend.empresa.presentation.dto.EmpresaDto;
import com.marioborrego.api.calculodeduccionesbackend.empresa.presentation.dto.EconomicoListadoGeneralDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController()
@RequestMapping("/api/economicos")
public class EconomicosController {
    private final Logger log = LoggerFactory.getLogger(EconomicosController.class);

    public final EconomicoService economicoService;

    public EconomicosController(EconomicoService economicoService) {
        this.economicoService = economicoService;
    }

//    @Operation(summary = "Obtener listado de economicos", description = "Permite obtener un listado de económios registrados en el sistema.")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Listado de económicos obtenido correctamente"),
//            @ApiResponse(responseCode = "400", description = "Error al obtener el listado de económicos")
//    })
//    @GetMapping
//    public ResponseEntity<List<EconomicoListadoGeneralDto>> listadoDeEconomicos() {
//        log.info("Petición para obtener el listado de económicos");
//        try {
//            return ResponseEntity.status(HttpStatus.OK).body(economicoService.obtenerTodosLosEconomicos());
//        } catch (Exception e) {
//            log.error("Error al obtener el listado de económicos: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
//        }
//    }

    @Operation(summary = "Obtener listado de economicos", description = "Permite obtener un listado de económios registrados en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de económicos obtenido correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al obtener el listado de económicos")
    })
    @GetMapping("")
    public ResponseEntity<Page<EconomicoListadoGeneralDto>> listadoDeEconomicosPaginado(@PageableDefault(page = 0, size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        log.info("Petición para obtener el listado de económicos con paginación: página {}, tamaño {}", pageable.getPageNumber(), pageable.getPageSize());
        try {
            Page<EconomicoListadoGeneralDto> economicos = economicoService.obtenerEconomicosPaginados(pageable);
            return ResponseEntity.status(HttpStatus.OK).body(economicos);
        } catch (Exception e) {
            log.error("Error al obtener el listado de económicos paginados: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping
    public void eliminarEconomico(@RequestBody EconomicoListadoGeneralDto economico) {
        log.info("Petición para eliminar el economico con ID: {}", economico);
        boolean result = economicoService.eliminarEconomico(economico);
        if (result) {
            log.info("Económico eliminado correctamente: {}", economico);
        } else {
            log.warn("No se pudo eliminar el económico: {}", economico);
        }
    }

    @Operation(summary = "Crear una nueva empresa", description = "Permite crear una nueva empresa en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empresa creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al crear la empresa")
    })
    @PostMapping()
    public EmpresaDto crearEmpresa(EmpresaDto empresaDto) {
        log.info("Petición para crear una nueva empresa: {}", empresaDto);
        // Aquí se debería implementar la lógica para guardar la empresa en la base de datos
        // Por ahora, simplemente retornamos el DTO recibido
        return empresaDto;
    }


}
