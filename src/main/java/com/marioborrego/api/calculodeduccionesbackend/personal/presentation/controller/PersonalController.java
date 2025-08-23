package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.controller;

import com.marioborrego.api.calculodeduccionesbackend.configuration.DTO.PageResponse;
import com.marioborrego.api.calculodeduccionesbackend.personal.business.interfaces.PersonalService;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.*;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.altasEjercicio.ActualizarAltaEjercicioDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.altasEjercicio.AltaEjercicioDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bbcc.ActualizarBbccPersonalDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bbcc.BbccPersonalDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.ActualizarBajaLaboralDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.BajasLaboralesDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.CrearBajaLaboralDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.ListadoPersonalSelectorEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.personal.ListarPersonalEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.personal.PersonalEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.retribuciones.ActualizarRetribucionDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.retribuciones.RetribucionesPersonalDTO;
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
@RequestMapping("/api/personal")
public class PersonalController {
    private final Logger log = LoggerFactory.getLogger(PersonalController.class);
    private final PersonalService personalService;

    public PersonalController(PersonalService personalService) {
        this.personalService = personalService;
    }

    @Operation(summary = "Obtener listado de personal económico", description = "Permite obtener un listado de personal económico registrado en el sistema para un económico específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de personal económico obtenido correctamente"),
            @ApiResponse(responseCode = "204", description = "No se encontró personal económico para el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error al obtener el listado de personal económico")
    })
    @GetMapping("/economico/{idEconomico}")
    public ResponseEntity<Page<ListarPersonalEconomicoDTO>> listadoPersonalEconomico(@PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable, @PathVariable Long idEconomico) {

        log.info("Petición para obtener el listado de personal económico: {}", idEconomico);
        try {
            Page<ListarPersonalEconomicoDTO> listado = personalService.obtenerTodoPersonalEconomico(idEconomico, pageable);

            if (listado == null || listado.isEmpty()) {
                log.warn("No se encontró personal económico para el ID: {}", idEconomico);
                return ResponseEntity.noContent().build();
            }

            log.info("Listado de personal económico obtenido correctamente: {}", listado.getTotalElements());
            return ResponseEntity.ok(listado);

        } catch (Exception e) {
            log.error("Error al obtener el listado de personal económico: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener listado de personal económico selector", description = "Permite obtener un listado de personal económico selector para un económico específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de personal económico selector obtenido correctamente"),
            @ApiResponse(responseCode = "204", description = "No se encontró personal económico selector para el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error al obtener el listado de personal económico selector")
    })
    @GetMapping("/selector/{idEconomico}")
    public ResponseEntity<List<ListadoPersonalSelectorEconomicoDTO>> listadoPersonalEconomicoSelector(@PathVariable int idEconomico) {
        log.info("Petición para obtener el listado de personal económico selector: {}", idEconomico);
        try {
            List<ListadoPersonalSelectorEconomicoDTO> listado = personalService.obtenerTodoPersonalSelectorEconomico(idEconomico);
            if (listado == null) {
                log.warn("No se encontró personal económico selector para el ID: {}", idEconomico);
                return ResponseEntity.noContent().build();
            }
            log.info("Listado de personal económico selector obtenido correctamente: {}", listado.size());
            return ResponseEntity.ok(listado);
        } catch (Exception e) {
            log.error("Error al obtener el listado de personal económico selector: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Crear nuevo personal económico", description = "Permite crear un nuevo personal económico asociado a un económico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Personal económico creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al crear el personal económico"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al crear el personal económico")
    })
    @PostMapping("/economico/crear")
    public ResponseEntity<PersonalEconomicoDTO> crearPersonalEconomico(@RequestBody PersonalEconomicoDTO personalEconomicoDTO) {
        log.info("Petición para crear un nuevo personal económico: {}", personalEconomicoDTO);
        try {
            PersonalEconomicoDTO c = this.personalService.crearPersonalEconomico(personalEconomicoDTO);
            log.info("Personal económico creado correctamente: {}", personalEconomicoDTO);
            return ResponseEntity.status(HttpStatus.OK).body(c);
        } catch (Exception e) {
            log.error("Error al crear el personal económico: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Operation(summary = "Eliminar personal económico", description = "Permite eliminar un personal económico por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Personal económico eliminado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al eliminar el personal económico"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al eliminar el personal económico")
    })
    @DeleteMapping("/{economico}/{id}")
    public ResponseEntity<Void> eliminarPersonalEconomico(@PathVariable int id, @PathVariable Long economico) {
        log.info("Petición para eliminar el personal económico con ID: {}", id);
        try {
            this.personalService.eliminarPersonalEconomico(id, economico);
            log.info("Personal económico eliminado correctamente: {}", id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("Error al eliminar el personal económico: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar personal económico", description = "Permite actualizar un personal económico por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Personal económico actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al actualizar el personal económico"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al actualizar el personal económico")
    })
    @PutMapping("/actualizar")
    public ResponseEntity<Void> actualizarPersonal(@RequestBody PersonalEconomicoDTO personalEconomicoDTO) {
        log.info("Petición para actualizar el personal económico con ID: {}", personalEconomicoDTO.getIdPersona());
        try {
            this.personalService.actualizarPersonalEconomico(personalEconomicoDTO);
            log.info("Personal económico actualizado correctamente: {}", personalEconomicoDTO.getIdPersona());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("Error al actualizar el personal económico: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener retribuciones del personal", description = "Permite obtener las retribuciones de un personal específico por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retribuciones del personal obtenidas correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron retribuciones para el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al obtener las retribuciones del personal")
    })
    @GetMapping("/{idEconomico}/retribuciones")
    public ResponseEntity<Page<RetribucionesPersonalDTO>> obtenerRetribucionesPersonal(@PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable, @PathVariable Long idEconomico) {
        log.info("Petición para obtener las retribuciones del personal del economico con ID: {}", idEconomico);
        try {
            if (idEconomico<= 0) {
                log.warn("ID económico no válido para retribuciones: {}", idEconomico);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Page<RetribucionesPersonalDTO> retribuciones = personalService.obtenerRetribucionesPersonalPorEconomico(idEconomico, pageable );
            return ResponseEntity.status(HttpStatus.OK).body(retribuciones);
        } catch (Exception e) {
            log.error("Error al obtener las retribuciones del personal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener cotizaciones del personal", description = "Permite obtener las cotizaciones del personal de un economico por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cotizaciones del personal obtenidas correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron cotizaciones para el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al obtener las cotizaciones del personal")
    })
    @GetMapping("/{idEconomico}/cotizaciones")
    public ResponseEntity<Page<BbccPersonalDTO>> obtenerCotizacionesPersonal(@PageableDefault(size = 20, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable, @PathVariable Long idEconomico) {
        log.info("Petición para obtener las cotizaciones del personal del economico con ID: {}", idEconomico);
        try {
            if (idEconomico <= 0) {
                log.warn("ID económico no válido: {}", idEconomico);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Page<BbccPersonalDTO> cotizaciones = personalService.obtenerCotizacionesPersonalPorEconomico(idEconomico, pageable);
            return ResponseEntity.status(HttpStatus.OK).body(cotizaciones);
        } catch (Exception e) {
            log.error("Error al obtener las cotizaciones del personal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar retribución del personal", description = "Permite actualizar la retribución de un personal específico dado el Id de Retribucion.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Retribución del personal actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al actualizar la retribución del personal"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al actualizar la retribución del personal")
    })
    @PutMapping("/retribucion")
    public ResponseEntity<Void> actualizarRetribucionPersonal(@RequestBody ActualizarRetribucionDTO actualizarRetribucionDTO) {
        log.info("Petición para actualizar la retribución con ID: {}", actualizarRetribucionDTO.getIdRetribucion());
        try {
            if (actualizarRetribucionDTO.getIdRetribucion() == 0 || actualizarRetribucionDTO.getCampoActualizado() == null || actualizarRetribucionDTO.getValor() == null) {
                log.warn("Datos de actualización retribucion personal incompletos: {}", actualizarRetribucionDTO);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            this.personalService.actualizarRetribucionPersonal(actualizarRetribucionDTO);
            log.info("Retribución del personal eliminada correctamente: {}", actualizarRetribucionDTO.getIdRetribucion());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("Error al eliminar la retribución del personal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar bases de cotización del personal", description = "Permite actualizar las bases de cotización de un personal específico dado el Id de Bases de Cotización.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Bases de cotización del personal actualizadas correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al actualizar las bases de cotización del personal"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al actualizar las bases de cotización del personal")
    })
    @PutMapping("/bbcc")
    public ResponseEntity<Void> actualizarBbccPersona(@RequestBody ActualizarBbccPersonalDTO actualizarBbccPersonalDTO) {
        log.info("Petición para actualizar las bases de cotización con ID: {}", actualizarBbccPersonalDTO.getIdBbccPersonal());
        try {
            if (actualizarBbccPersonalDTO.getIdBbccPersonal() <= 0 || actualizarBbccPersonalDTO.getCampoActualizado() == null || actualizarBbccPersonalDTO.getValor() == null) {
                log.warn("Datos de actualización incompletos: {}", actualizarBbccPersonalDTO);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            this.personalService.actualizarBbccPersonal(actualizarBbccPersonalDTO);
            log.info("Bases de cotización del personal actualizadas correctamente: {}", actualizarBbccPersonalDTO.getIdBbccPersonal());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("Error al actualizar las bases de cotización del personal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener listado de personal con los datos alta ejercicio", description = "Permite obtener un listado de personal con los datos alta ejercicio para un económico específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de personal con datos alta ejercicio obtenido correctamente"),
            @ApiResponse(responseCode = "204", description = "No se encontró personal con datos alta ejercicio para el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error al obtener el listado de personal con datos alta ejercicio")
    })
    @GetMapping("/{idEconomico}/alta-ejercicio")
    public ResponseEntity<Page<AltaEjercicioDTO>> listadoDePersonalAltaEjercicio(@PageableDefault(size = 20) Pageable pageable, @PathVariable Long idEconomico) {
        log.info("Petición para obtener el listado de alta de personal económico: {}", idEconomico);
        try {
            Page<AltaEjercicioDTO> listado = personalService.obtenerTodoPersonalAltaEjercicio(idEconomico, pageable);
            if (listado == null || listado.isEmpty()) {
                log.warn("No se encontró listado de alta personal económico para el ID: {}", idEconomico);
                return ResponseEntity.noContent().build();
            }
            log.info("Listado de alta  de personal económico obtenido correctamente: {}", listado.getTotalElements());
            return ResponseEntity.ok(listado);
        } catch (Exception e) {
            log.error("Error al obtener el listado de alta  de personal económico: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar alta ejercicio del personal", description = "Permite actualizar el alta ejercicio de un personal específico dado el Id de Alta Ejercicio.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Alta ejercicio del personal actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al actualizar el alta ejercicio del personal"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al actualizar el alta ejercicio del personal")
    })
    @PutMapping("/alta-ejercicio")
    public ResponseEntity<Void> actualizarAltaEjercicio(@RequestBody ActualizarAltaEjercicioDTO actualizarAltaEjercicioDTO) {
        log.info("Petición para actualizar el alta ejercicio con ID: {}", actualizarAltaEjercicioDTO.getIdAltaEjercicio());
        log.info("Datos a cambiar: campo {}, valor {}", actualizarAltaEjercicioDTO.getCampoActualizado(), actualizarAltaEjercicioDTO.getValor());
        try {
            if (actualizarAltaEjercicioDTO.getIdAltaEjercicio() <= 0 || actualizarAltaEjercicioDTO.getCampoActualizado() == null || actualizarAltaEjercicioDTO.getValor() == null) {
                log.warn("Datos de actualización alta ejercicio incompletos: {}", actualizarAltaEjercicioDTO);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            this.personalService.actualizarAltaEjercicio(actualizarAltaEjercicioDTO);
            log.info("Alta ejercicio actualizado correctamente: {}", actualizarAltaEjercicioDTO.getIdAltaEjercicio());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("Error al actualizar el alta ejercicio: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener bajas laborales del personal", description = "Permite obtener las bajas laborales de un personal específico por su ID económico.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bajas laborales del personal obtenidas correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron bajas laborales para el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al obtener las bajas laborales del personal")
    })
    @GetMapping("/{idEconomico}/bajas-laborales")
    public ResponseEntity<Page<BajasLaboralesDTO>> obtenerBajasLaborales(@PageableDefault(size = 20, sort = "fechaInicio", direction = Sort.Direction.DESC) Pageable pageable, @PathVariable Long idEconomico) {
        log.info("Petición para obtener las bajas laborales del personal del económico con ID: {}", idEconomico);
        try {
            if (idEconomico <= 0) {
                log.warn("No se puede recouperar bajas ya que el ID económico es no válido: {}", idEconomico);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Page<BajasLaboralesDTO> bajasLaborales = personalService.obtenerBajasLaboralesPorEconomico(idEconomico, pageable);
            return ResponseEntity.status(HttpStatus.OK).body(bajasLaborales);
        } catch (Exception e) {
            log.error("Error al obtener las bajas laborales del personal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Crear baja laboral", description = "Permite crear una nueva baja laboral para un personal específico.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Baja laboral creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al crear la baja laboral"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al crear la baja laboral")
    })
    @PostMapping("/baja-laboral")
    public ResponseEntity<Void> crearBajaLaboral(@RequestBody CrearBajaLaboralDTO bajaLaboralDTO) {
        log.info("Petición para crear una nueva baja laboral: {}", bajaLaboralDTO);
        try {
            if (bajaLaboralDTO.getIdPersona() <= 0) {
                log.warn("Datos de baja laboral incompletos: {}", bajaLaboralDTO);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            this.personalService.crearBajaLaboral(bajaLaboralDTO);
            log.info("Baja laboral creada correctamente: {}", bajaLaboralDTO);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            log.error("Error al crear la baja laboral: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar baja laboral", description = "Permite eliminar una baja laboral por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Baja laboral eliminada correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al eliminar la baja laboral"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al eliminar la baja laboral")
    })
    @DeleteMapping("/baja-laboral/{idBajaLaboral}")
    public ResponseEntity<Void> eliminarBajaLaboral(@PathVariable Long idBajaLaboral) {
        log.info("Petición para eliminar la baja laboral con ID: {}", idBajaLaboral);
        try {
            if (idBajaLaboral <= 0) {
                log.warn("ID de baja laboral no válido: {}", idBajaLaboral);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            this.personalService.eliminarBajaLaboral(idBajaLaboral);
            log.info("Baja laboral eliminada correctamente: {}", idBajaLaboral);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("Error al eliminar la baja laboral: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar baja laboral", description = "Permite actualizar una baja laboral por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Baja laboral actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "Error al actualizar la baja laboral"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al actualizar la baja laboral")
    })
    @PutMapping("/baja-laboral")
    public ResponseEntity<Void> actualizarBajaLaboral(@RequestBody ActualizarBajaLaboralDTO actualizarBajaLaboralDTO) {
        log.info("Petición para actualizar la baja laboral con ID: {}", actualizarBajaLaboralDTO.getIdBajaLaboral());
        try {
            if (actualizarBajaLaboralDTO.getIdBajaLaboral() <= 0 || actualizarBajaLaboralDTO.getCampoActualizado() == null || actualizarBajaLaboralDTO.getValor() == null) {
                log.warn("Datos de actualización de baja laboral incompletos: {}", actualizarBajaLaboralDTO);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            this.personalService.actualizarBajaLaboral(actualizarBajaLaboralDTO);
            log.info("Baja laboral actualizada correctamente: {}", actualizarBajaLaboralDTO.getIdBajaLaboral());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            log.error("Error al actualizar la baja laboral: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Obtener bonificaciones del personal", description = "Permite obtener las bonificaciones del personal de un economico por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Bonificaciones del personal obtenidas correctamente"),
            @ApiResponse(responseCode = "404", description = "No se encontraron bonificaciones para el ID proporcionado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor al obtener las bonificaciones del personal")
    })
    @GetMapping("/{idEconomico}/bonificaciones")
    public ResponseEntity<PageResponse<BonificacionesEmpleadoEconomicoDTO>> obtenerBonificacionesEmpleado(@PageableDefault(size = 20) Pageable pageable, @PathVariable Long idEconomico) {
        log.info("Petición para obtener las bonificaciones del personal del economico con ID: {}", idEconomico);
        try {
            if (idEconomico <= 0) {
                log.warn("ID económico no válido para obtener las bonificaciones: {}", idEconomico);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            Page<BonificacionesEmpleadoEconomicoDTO> bonificaciones = personalService.obtenerBonificacionesEmpleadoPorEconomico(idEconomico, pageable);
            PageResponse<BonificacionesEmpleadoEconomicoDTO> p = PageResponse.from(bonificaciones);
            return ResponseEntity.status(HttpStatus.OK).body(p);
        } catch (Exception e) {
            log.error("Error al obtener las bonificaciones del personal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
