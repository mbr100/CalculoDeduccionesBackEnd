package com.marioborrego.api.calculodeduccionesbackend.economico.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.economico.business.interfaces.EconomicoService;
import com.marioborrego.api.calculodeduccionesbackend.economico.business.interfaces.GastoProyectoService;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.*;
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

import java.util.List;


@RestController()
@RequestMapping("/api/economicos")
public class EconomicosController {
    private final Logger log = LoggerFactory.getLogger(EconomicosController.class);

    private final EconomicoService economicoService;
    private final GastoProyectoService gastoProyectoService;

    public EconomicosController(EconomicoService economicoService, GastoProyectoService gastoProyectoService) {
        this.economicoService = economicoService;
        this.gastoProyectoService = gastoProyectoService;
    }

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
            Page<EconomicoListadoGeneralDto> dtoPage = economicos.map(e -> EconomicoListadoGeneralDto.builder()
                    .id(e.getId())
                    .nombre(e.getNombre())
                    .cif(e.getCif())
                    .CNAE(e.getCNAE())
                    .anualidad(e.getAnualidad())
                    .esPyme(e.isEsPyme())
                    .build());
            return ResponseEntity.status(HttpStatus.OK).body(dtoPage);
        } catch (Exception e) {
            log.error("Error al obtener el listado de económicos paginados: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("")
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
    public ResponseEntity<EconomicoCreadoDTO> crearEconomico(@RequestBody CrearEconomicoDTO crearEconomicoDTO) {
        log.info("Petición para crear un nuevo económico: CIF {} an anualidad {}", crearEconomicoDTO.getCif(), crearEconomicoDTO.getAnualidad());
        boolean resultado = economicoService.comprobarExistenciaEconomico(crearEconomicoDTO.getCif(), crearEconomicoDTO.getAnualidad());
        log.warn("Existencia de economico {}", resultado);
        if (resultado) {
            log.warn("Ya existe un económico con CIF: {} y Anualidad: {}", crearEconomicoDTO.getCif(), crearEconomicoDTO.getAnualidad());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        log.info("Petición para crear un nuevo económico: {}", crearEconomicoDTO);
        try {
            EconomicoCreadoDTO dtocreado = economicoService.crearEconomico(crearEconomicoDTO);
            log.info("Económico creado correctamente: {}", dtocreado);
            return ResponseEntity.status(HttpStatus.OK).body(dtocreado);
        } catch (Exception e) {
            log.error("Error al crear el económico: {}", e.getMessage());
            throw new RuntimeException("Error al crear el económico", e);
        }

    }

    @Operation(summary = "Obtener la Información General Economico", description = "El usuario selecciona un economico y permite visualizar la pantalla general con los datos generales de la empresa para esa anualidad")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se devuelve el objeto ya que se ha encontrado"),
            @ApiResponse(responseCode = "400", description = "Se devuelve un error ya que no se ha encontrado")
    })
    @GetMapping("/{idEconomico}")
    public ResponseEntity<EconomicoDTO> getEconomico(@PathVariable Long idEconomico) {
        EconomicoDTO economicoDTO = economicoService.obtenerEconomico(idEconomico);
        return ResponseEntity.status(HttpStatus.OK).body(economicoDTO);
    }

    @Operation(summary = "Actualizar los datos del económico", description = "Permite actualizar los datos de un económico existente en el sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Datos del económico actualizados correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al actualizar los datos del económico")
    })
    @PutMapping("/actualizar")
    public ResponseEntity<Boolean> actualizarDatosEconomico(@RequestBody ActualizarDatosEconomicoDTO economico) {
        log.info("Petición para actualizar los datos del económico: {}", economico);
        try {
            economicoService.actualizarDatosEconomico(economico);
            log.info("Datos del económico actualizados correctamente: {}", economico);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } catch (Exception e) {
            log.error("Error al actualizar los datos del económico: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(false);
        }
    }

    @Operation(summary = "Obtener el resumen económico por proyectos", description = "Permite obtener un resumen económico detallado de los proyectos asociados a un económico específico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Resumen económico obtenido correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al obtener el resumen económico")
    })
    @GetMapping("/{idEconomico}/resumen")
    public ResponseEntity<List<GastoProyectoDetalladoDTO>> getResumenEconomico(@PathVariable Long idEconomico) {
        if (idEconomico == null || idEconomico <= 0) {
            log.error("ID de económico no válido: {}", idEconomico);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        List<GastoProyectoDetalladoDTO> resumen = gastoProyectoService.calcularGastosPorEconomico(idEconomico);
        return ResponseEntity.status(HttpStatus.OK).body(resumen);
    }
}
