package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.helper.ValoresDefecto;
import com.marioborrego.api.calculodeduccionesbackend.personal.business.interfaces.PersonalService;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.*;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.*;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.ActualizacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bbcc.ActualizarBbccPersonalDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bbcc.BbccPersonalDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.ActualizarBajaLaboralDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.BajasLaboralesDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.CrearBajaLaboralDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.ListadoPersonalSelectorEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.ActualizarBonificacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.BonificacionesEmpleadoEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.CrearBonificacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.personal.ListarPersonalEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.personal.PersonalEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.resumenCostes.ResumenCostePersonalDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.retribuciones.CamposRetribuciones;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.retribuciones.RetribucionesPersonalDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.exceptions.IDEconomicoException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class PersonalServiceImpl implements PersonalService {
    private final PersonalRepository personalRepository;
    private final EconomicoRepository economicoRepository;
    private final RetribucionRepository retribucionRepository;
    private final BasesCotizacionRepository basesCotizacionRepository;
    private final HorasEmpleadoRepository horasEmpleadoRepository;
    private final BajaLaboralRepository bajaLaboralRepository;
    private final BonificacionesTrabajadorRepository bonificacionesTrabajadorRepository;
    private final CosteHoraService costeHoraService;
    private final BonificacionService bonificacionService;
    private final com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.ProyectoPersonalRepository proyectoPersonalRepository;

    private final Logger logger = LoggerFactory.getLogger(PersonalServiceImpl.class);


    public PersonalServiceImpl(PersonalRepository personalRepository, EconomicoRepository economicoRepository, RetribucionRepository retribucionRepository,
                               BasesCotizacionRepository basesCotizacionRepository, HorasEmpleadoRepository horasEmpleadoRepository,
                               BajaLaboralRepository bajaLaboralRepository, BonificacionesTrabajadorRepository bonificacionesTrabajadorRepository,
                               CosteHoraService costeHoraService, BonificacionService bonificacionService,
                               com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.ProyectoPersonalRepository proyectoPersonalRepository) {
        this.bonificacionesTrabajadorRepository = bonificacionesTrabajadorRepository;
        this.basesCotizacionRepository = basesCotizacionRepository;
        this.retribucionRepository = retribucionRepository;
        this.economicoRepository = economicoRepository;
        this.personalRepository = personalRepository;
        this.horasEmpleadoRepository = horasEmpleadoRepository;
        this.bajaLaboralRepository = bajaLaboralRepository;
        this.costeHoraService = costeHoraService;
        this.bonificacionService = bonificacionService;
        this.proyectoPersonalRepository = proyectoPersonalRepository;
    }

    @Override
    public Page<ListarPersonalEconomicoDTO> obtenerTodoPersonalEconomico(Long idEconomico, Pageable pageable) {
        Page<Personal> personalList = personalRepository.findPersonalByeconomicoId(idEconomico, pageable);
        if (personalList != null && !personalList.isEmpty()) {
            return personalList.map(personal -> ListarPersonalEconomicoDTO.builder()
                    .idPersona(personal.getIdPersona())
                    .nombre(personal.getNombre())
                    .apellidos(personal.getApellidos())
                    .dni(personal.getDni())
                    .puesto(personal.getPuesto())
                    .departamento(personal.getDepartamento())
                    .titulacion1(personal.getTitulacion1())
                    .titulacion2(personal.getTitulacion2())
                    .titulacion3(personal.getTitulacion3())
                    .titulacion4(personal.getTitulacion4())
                    .esPersonalInvestigador(personal.isEsPersonalInvestigador())
                    .esContratoIndefinido(personal.isEsContratoIndefinido())
                    .claveOcupacion(personal.getClaveOcupacion())
                    .build());
        } else {
            return Page.empty();
        }
    }

    @Override
    public PersonalEconomicoDTO crearPersonalEconomico(PersonalEconomicoDTO personalEconomicoDTO) {
        if (personalEconomicoDTO == null || personalEconomicoDTO.getIdEconomico() == 0) {
            throw new IDEconomicoException("El DTO de creación de personal económico no puede ser nulo y debe contener un ID económico.");
        }
        if (personalEconomicoDTO.getIdPersona() != 0) {
            Optional<Personal> existingPersonal = personalRepository.findById(personalEconomicoDTO.getIdPersona());
            if (existingPersonal.isPresent()) {
                throw new IDEconomicoException("Ya existe un personal con el ID proporcionado.");
            }
        }

        Economico economico = this.economicoRepository.findById(personalEconomicoDTO.getIdEconomico())
                .orElseThrow(() -> new IllegalArgumentException("No existe un económico con el ID proporcionado."));

        // 1. Crear Retribución con valores por defecto
        Retribucion retribucion = ValoresDefecto.getRetribucionDefault();

        // 2. Crear Bases de Cotización con valores por defecto (0 para todos los meses)
        BasesCotizacion basesCotizacion = ValoresDefecto.getBasesCotizacionDefault();

        // 3. Crear Horas Personal con valores por defecto
        HorasPersonal horasPersonal = ValoresDefecto.getHorasPersonalDefault(economico);

        // 4. Crear costeHora con valores por defecto (null para todos los campos)
        CosteHoraPersonal costeHoraPersonal = ValoresDefecto.getCosteHoraPersonalDefault();

        // 5. Crear Personal con todas las entidades relacionadas
        Personal newPersonal = Personal.builder()
                .nombre(personalEconomicoDTO.getNombre())
                .apellidos(personalEconomicoDTO.getApellidos())
                .dni(personalEconomicoDTO.getDni())
                .puesto(personalEconomicoDTO.getPuesto())
                .departamento(personalEconomicoDTO.getDepartamento())
                .titulacion1(personalEconomicoDTO.getTitulacion1())
                .titulacion2(personalEconomicoDTO.getTitulacion2())
                .titulacion3(personalEconomicoDTO.getTitulacion3())
                .titulacion4(personalEconomicoDTO.getTitulacion4())
                .esPersonalInvestigador(personalEconomicoDTO.isEsPersonalInvestigador())
                .esContratoIndefinido(personalEconomicoDTO.isEsContratoIndefinido())
                .claveOcupacion(personalEconomicoDTO.getClaveOcupacion())
                .economico(economico)
                .retribucion(retribucion)
                .basesCotizacion(basesCotizacion)
                .horasPersonal(horasPersonal)
                .bonificaciones(new ArrayList<>())
                .bajasLaborales(new ArrayList<>())
                .costeHoraPersonal(costeHoraPersonal)
                .build();

        // 6. Establecer relaciones bidireccionales
        retribucion.setPersonal(newPersonal);
        basesCotizacion.setPersona(newPersonal);
        horasPersonal.setPersonal(newPersonal);

        try {
            this.costeHoraService.calcularCosteHoraEconomico(economico);
            Personal savedPersonal = personalRepository.save(newPersonal);
            return PersonalEconomicoDTO.builder()
                    .idPersona(savedPersonal.getIdPersona())
                    .nombre(savedPersonal.getNombre())
                    .apellidos(savedPersonal.getApellidos())
                    .dni(savedPersonal.getDni())
                    .puesto(savedPersonal.getPuesto())
                    .departamento(savedPersonal.getDepartamento())
                    .titulacion1(savedPersonal.getTitulacion1())
                    .titulacion2(savedPersonal.getTitulacion2())
                    .titulacion3(savedPersonal.getTitulacion3())
                    .titulacion4(savedPersonal.getTitulacion4())
                    .esPersonalInvestigador(savedPersonal.isEsPersonalInvestigador())
                    .esContratoIndefinido(savedPersonal.isEsContratoIndefinido())
                    .claveOcupacion(savedPersonal.getClaveOcupacion())
                    .idEconomico(savedPersonal.getEconomico().getIdEconomico())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el personal económico: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarPersonalEconomico(Long id, Long economico) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del personal económico a eliminar no puede ser nulo o menor o igual a cero.");
        }
        if (economico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        Personal personal = personalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un personal económico con el ID proporcionado."));

        if (!Objects.equals(personal.getEconomico().getIdEconomico(), economico)) {
            throw new IllegalArgumentException("El personal económico no pertenece al económico proporcionado.");
        }

        Long idEconomico = personal.getEconomico().getIdEconomico();

        // ProyectoPersonal tiene FK desde el lado de Proyecto — borrar explícitamente
        proyectoPersonalRepository.deleteByPersonalIdPersona(id);

        // El resto de hijos (retribucion, basesCotizacion, horasPersonal, costeHoraPersonal,
        // bonificaciones, bajasLaborales, periodosContrato) se eliminan por cascade desde Personal
        personalRepository.delete(personal);
        personalRepository.flush();

        // Refrescar el económico desde BD para que no contenga el personal eliminado
        Economico eco = economicoRepository.findById(idEconomico)
                .orElseThrow(() -> new IllegalArgumentException("Económico no encontrado tras eliminación"));
        this.costeHoraService.calcularCosteHoraEconomico(eco);
    }

    @Override
    public void actualizarPersonalEconomico(PersonalEconomicoDTO personalEconomicoDTO) {
        if (personalEconomicoDTO.getIdEconomico() <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        if (personalEconomicoDTO.getIdPersona() <= 0) {
            throw new IllegalArgumentException("El ID del personal económico a actualizar no puede ser nulo o menor o igual a cero.");
        }
        Optional<Personal> personal = personalRepository.findById(personalEconomicoDTO.getIdPersona());
        if (personal.isPresent()) {
            Economico economico = this.economicoRepository.findById(personalEconomicoDTO.getIdEconomico())
                    .orElseThrow(() -> new IllegalArgumentException("No existe un económico con el ID proporcionado."));
            if (!Objects.equals(personal.get().getEconomico().getIdEconomico(), economico.getIdEconomico())) {
                throw new IllegalArgumentException("El personal económico no pertenece al económico proporcionado.");
            }
            Personal updatedPersonal = getPersonal(personalEconomicoDTO, personal, economico);
            personalRepository.save(updatedPersonal);
            this.costeHoraService.calcularCosteHoraEconomico(economico);
        } else {
            throw new IllegalArgumentException("No existe un personal económico con el ID proporcionado.");
        }
    }

    private static Personal getPersonal(PersonalEconomicoDTO personalEconomicoDTO, Optional<Personal> personal, Economico economico) {
        if (personalEconomicoDTO == null || personal.isEmpty()) {
            throw new IllegalArgumentException("El DTO de actualización de personal económico no puede ser nulo y debe contener un ID de personal.");
        }
        Personal updatedPersonal = personal.get();
        updatedPersonal.setNombre(personalEconomicoDTO.getNombre());
        updatedPersonal.setApellidos(personalEconomicoDTO.getApellidos());
        updatedPersonal.setDni(personalEconomicoDTO.getDni());
        updatedPersonal.setPuesto(personalEconomicoDTO.getPuesto());
        updatedPersonal.setDepartamento(personalEconomicoDTO.getDepartamento());
        updatedPersonal.setTitulacion1(personalEconomicoDTO.getTitulacion1());
        updatedPersonal.setTitulacion2(personalEconomicoDTO.getTitulacion2());
        updatedPersonal.setTitulacion3(personalEconomicoDTO.getTitulacion3());
        updatedPersonal.setTitulacion4(personalEconomicoDTO.getTitulacion4());
        // esPersonalInvestigador y esContratoIndefinido se gestionan desde sus propias secciones
        // No se sobrescriben desde el formulario de edición de datos básicos
        updatedPersonal.setClaveOcupacion(personalEconomicoDTO.getClaveOcupacion());
        updatedPersonal.setEconomico(economico);
        return updatedPersonal;
    }

    @Override
    public Page<RetribucionesPersonalDTO> obtenerRetribucionesPersonalPorEconomico(Long id, Pageable pageable) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del personal no puede ser nulo o menor o igual a cero.");
        }
        return personalRepository.findPersonalByeconomicoId(id, Pageable.unpaged())
                .map(personal -> RetribucionesPersonalDTO.builder()
                        .idPersonal(personal.getIdPersona())
                        .nombre(personal.getNombre())
                        .dni(personal.getDni())
                        .idRetribucion(personal.getRetribucion().getIdRetribucion())
                        .importeRetribucionNoIT(personal.getRetribucion().getImporteRetribucionNoIT())
                        .importeRetribucionExpecie(personal.getRetribucion().getImporteRetribucionExpecie())
                        .aportacionesPrevencionSocial(personal.getRetribucion().getAportaciones_prevencion_social())
                        .dietasViajeExentas(personal.getRetribucion().getDietas_viaje_exentas())
                        .rentasExentas190(personal.getRetribucion().getRentas_exentas_190())
                        .build());
    }

    @Override
    public Page<BbccPersonalDTO> obtenerCotizacionesPersonalPorEconomico(Long idEconomico, Pageable pageable) {
        if (idEconomico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }

        List<BbccPersonalDTO> result = new ArrayList<>();

        // Obtener todas las BasesCotizacion vinculadas a periodos de contrato para este económico
        List<BasesCotizacion> periodoBases = basesCotizacionRepository.findAllPeriodoLinkedByEconomico(idEconomico);

        // Recopilar IDs de personal que ya tienen filas de periodo
        java.util.Set<Long> personalConPeriodos = new java.util.HashSet<>();
        for (BasesCotizacion bases : periodoBases) {
            personalConPeriodos.add(bases.getPersona().getIdPersona());
            PeriodoContrato periodo = bases.getPeriodoContrato();
            result.add(toBbccDTO(bases, periodo));
        }

        // Para personal SIN periodos, incluir la fila por defecto (legacy)
        Page<Personal> personalList = personalRepository.findPersonalByeconomicoId(idEconomico, Pageable.unpaged());
        if (personalList != null) {
            for (Personal personal : personalList.getContent()) {
                if (!personalConPeriodos.contains(personal.getIdPersona())) {
                    result.add(toBbccDTO(personal.getBasesCotizacion(), null));
                }
            }
        }

        // Ordenar por nombre
        result.sort((a, b) -> {
            int cmp = a.getNombre().compareToIgnoreCase(b.getNombre());
            if (cmp != 0) return cmp;
            // Dentro del mismo empleado, ordenar por fecha de alta
            if (a.getFechaAlta() != null && b.getFechaAlta() != null) {
                return a.getFechaAlta().compareTo(b.getFechaAlta());
            }
            return 0;
        });

        // Emular paginación manual
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), result.size());
        List<BbccPersonalDTO> pageContent = start < result.size() ? result.subList(start, end) : List.of();
        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, result.size());
    }

    private BbccPersonalDTO toBbccDTO(BasesCotizacion bases, PeriodoContrato periodo) {
        Personal personal = bases.getPersona();
        BbccPersonalDTO.BbccPersonalDTOBuilder builder = BbccPersonalDTO.builder()
                .idPersonal(personal.getIdPersona())
                .nombre(personal.getNombre())
                .dni(personal.getDni())
                .id_baseCotizacion(bases.getId_baseCotizacion())
                .basesCotizacionContingenciasComunesEnero(bases.getBasesCotizacionContingenciasComunesEnero())
                .basesCotizacionContingenciasComunesFebrero(bases.getBasesCotizacionContingenciasComunesFebrero())
                .basesCotizacionContingenciasComunesMarzo(bases.getBasesCotizacionContingenciasComunesMarzo())
                .basesCotizacionContingenciasComunesAbril(bases.getBasesCotizacionContingenciasComunesAbril())
                .basesCotizacionContingenciasComunesMayo(bases.getBasesCotizacionContingenciasComunesMayo())
                .basesCotizacionContingenciasComunesJunio(bases.getBasesCotizacionContingenciasComunesJunio())
                .basesCotizacionContingenciasComunesJulio(bases.getBasesCotizacionContingenciasComunesJulio())
                .basesCotizacionContingenciasComunesAgosto(bases.getBasesCotizacionContingenciasComunesAgosto())
                .basesCotizacionContingenciasComunesSeptiembre(bases.getBasesCotizacionContingenciasComunesSeptiembre())
                .basesCotizacionContingenciasComunesOctubre(bases.getBasesCotizacionContingenciasComunesOctubre())
                .basesCotizacionContingenciasComunesNoviembre(bases.getBasesCotizacionContingenciasComunesNoviembre())
                .basesCotizacionContingenciasComunesDiciembre(bases.getBasesCotizacionContingenciasComunesDiciembre());

        if (periodo != null) {
            builder.idPeriodoContrato(periodo.getId())
                    .claveContrato(periodo.getClaveContrato().getClave())
                    .descripcionContrato(periodo.getClaveContrato().getDescripcion())
                    .fechaAlta(periodo.getFechaAlta())
                    .fechaBaja(periodo.getFechaBaja())
                    .anioFiscal(periodo.getAnioFiscal());
        }

        return builder.build();
    }

    @Override
    public void actualizarRetribucionPersonal(ActualizacionDTO<Double, CamposRetribuciones> actualizarRetribucionDTO) {
        if (actualizarRetribucionDTO.getId() <= 0) {
            throw new IllegalArgumentException("El ID de la retribución a actualizar no puede ser nulo o menor o igual a cero.");
        }
        Retribucion retribucion = retribucionRepository.findById(actualizarRetribucionDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("No existe una retribución con el ID proporcionado."));

        switch (actualizarRetribucionDTO.getCampoActualizado()) {
            case CamposRetribuciones.importeRetribucionNoIT:
                retribucion.setImporteRetribucionNoIT(actualizarRetribucionDTO.getValor().longValue());
                break;
            case CamposRetribuciones.importeRetribucionExpecie:
                retribucion.setImporteRetribucionExpecie(actualizarRetribucionDTO.getValor().longValue());
                break;
            case CamposRetribuciones.aportacionesPrevencionSocial:
                retribucion.setAportaciones_prevencion_social(actualizarRetribucionDTO.getValor().longValue());
                break;
            case CamposRetribuciones.dietasViajeExentas:
                retribucion.setDietas_viaje_exentas(actualizarRetribucionDTO.getValor().longValue());
                break;
            case CamposRetribuciones.rentasExentas190:
                retribucion.setRentas_exentas_190(actualizarRetribucionDTO.getValor().longValue());
                break;
            default:
                throw new IllegalArgumentException("Campo actualizado no válido: " + actualizarRetribucionDTO.getCampoActualizado());
        }
        try {
            retribucionRepository.save(retribucion);
            // Recalcular DESPUÉS de guardar el nuevo valor
            this.costeHoraService.calcularCosteHoraEconomico(retribucion.getPersonal().getEconomico());
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la retribución del personal: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizarBbccPersonal(ActualizarBbccPersonalDTO actualizarBbccPersonalDTO) {
        if (actualizarBbccPersonalDTO.getIdBbccPersonal() <= 0) {
            throw new IllegalArgumentException("El ID de las bases de cotización a actualizar no puede ser nulo o menor o igual a cero.");
        }
        BasesCotizacion basesCotizacion = basesCotizacionRepository.findById(actualizarBbccPersonalDTO.getIdBbccPersonal())
                .orElseThrow(() -> new IllegalArgumentException("No existen bases de cotización con el ID proporcionado."));
        switch (actualizarBbccPersonalDTO.getCampoActualizado()) {
            case "basesCotizacionContingenciasComunesEnero":
                basesCotizacion.setBasesCotizacionContingenciasComunesEnero(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesFebrero":
                basesCotizacion.setBasesCotizacionContingenciasComunesFebrero(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesMarzo":
                basesCotizacion.setBasesCotizacionContingenciasComunesMarzo(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesAbril":
                basesCotizacion.setBasesCotizacionContingenciasComunesAbril(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesMayo":
                basesCotizacion.setBasesCotizacionContingenciasComunesMayo(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesJunio":
                basesCotizacion.setBasesCotizacionContingenciasComunesJunio(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesJulio":
                basesCotizacion.setBasesCotizacionContingenciasComunesJulio(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesAgosto":
                basesCotizacion.setBasesCotizacionContingenciasComunesAgosto(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesSeptiembre":
                basesCotizacion.setBasesCotizacionContingenciasComunesSeptiembre(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesOctubre":
                basesCotizacion.setBasesCotizacionContingenciasComunesOctubre(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesNoviembre":
                basesCotizacion.setBasesCotizacionContingenciasComunesNoviembre(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            case "basesCotizacionContingenciasComunesDiciembre":
                basesCotizacion.setBasesCotizacionContingenciasComunesDiciembre(actualizarBbccPersonalDTO.getValor().longValue());
                break;
            default:
                throw new IllegalArgumentException("Campo actualizado no válido: " + actualizarBbccPersonalDTO.getCampoActualizado());
        }
        try {
            basesCotizacionRepository.save(basesCotizacion);
            this.costeHoraService.calcularCosteHoraEconomico(basesCotizacion.getPersona().getEconomico());
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar las bases de cotización del personal: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<BajasLaboralesDTO> obtenerBajasLaboralesPorEconomico(Long idEconomico, Pageable pageable) {
        if (idEconomico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        Economico economico = economicoRepository.findById(idEconomico)
                .orElseThrow(() -> new IDEconomicoException("Económico no encontrado con ID: " + idEconomico));
        int anualidad = Math.toIntExact(economico.getAnualidad());
        long diasAnualidad = java.time.Year.of(anualidad).isLeap() ? 366 : 365;
        BigDecimal diasAnualidadDecimal = BigDecimal.valueOf(diasAnualidad);

        Page<BajaLaboral> bajaslaboralespersonalEconomico = bajaLaboralRepository.findByPersonalEconomicoIdEconomico(idEconomico, pageable);
        if (bajaslaboralespersonalEconomico != null && !bajaslaboralespersonalEconomico.isEmpty()) {
            return bajaslaboralespersonalEconomico.map(bajaLaboral -> {
                Personal personal = bajaLaboral.getPersonal();
                LocalDate inicioAnio = LocalDate.of(anualidad, 1, 1);
                LocalDate finAnio = LocalDate.of(anualidad, 12, 31);
                
                LocalDate fechaInicioEfectiva = bajaLaboral.getFechaInicio().isBefore(inicioAnio) ? inicioAnio : bajaLaboral.getFechaInicio();
                LocalDate fechaFinEfectiva = bajaLaboral.getFechaFin() == null || bajaLaboral.getFechaFin().isAfter(finAnio) ? finAnio : bajaLaboral.getFechaFin();
                
                BigDecimal horasBaja = BigDecimal.ZERO;
                if (!fechaInicioEfectiva.isAfter(fechaFinEfectiva)) {
                    for (PeriodoContrato pc : personal.getPeriodosContrato()) {
                        LocalDate pcInicio = pc.getFechaAlta().isBefore(inicioAnio) ? inicioAnio : pc.getFechaAlta();
                        LocalDate pcFin = pc.getFechaBaja() == null || pc.getFechaBaja().isAfter(finAnio) ? finAnio : pc.getFechaBaja();
                        
                        LocalDate overlapInicio = pcInicio.isAfter(fechaInicioEfectiva) ? pcInicio : fechaInicioEfectiva;
                        LocalDate overlapFin = pcFin.isBefore(fechaFinEfectiva) ? pcFin : fechaFinEfectiva;
                        
                        if (!overlapInicio.isAfter(overlapFin)) {
                            long diasSolapados = java.time.temporal.ChronoUnit.DAYS.between(overlapInicio, overlapFin) + 1;
                            BigDecimal horasDia = BigDecimal.valueOf(pc.getHorasConvenio()).divide(diasAnualidadDecimal, 6, RoundingMode.HALF_UP);
                            BigDecimal horasPc = horasDia.multiply(BigDecimal.valueOf(diasSolapados))
                                    .multiply(pc.getPorcentajeJornada())
                                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                            horasBaja = horasBaja.add(horasPc);
                        }
                    }
                }

                return BajasLaboralesDTO.builder()
                        .idPersona(personal.getIdPersona())
                        .nombre(personal.getNombre() + " " + personal.getApellidos())
                        .dni(personal.getDni())
                        .idBajaLaboral(bajaLaboral.getId())
                        .fechaInicio(bajaLaboral.getFechaInicio())
                        .fechaFin(bajaLaboral.getFechaFin())
                        .horasDeBaja(horasBaja.setScale(0, RoundingMode.HALF_UP).longValue())
                        .build();
            });
        } else {
            return Page.empty();
        }
    }

    @Override
    public List<ListadoPersonalSelectorEconomicoDTO> obtenerTodoPersonalSelectorEconomico(Long idEconomico) {
        if (idEconomico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        logger.info("Obteniendo todo el personal del económico con ID: {}", idEconomico);
        List<Personal> personalList = personalRepository.findByEconomicoId(idEconomico);
        if (!personalList.isEmpty()) {
            List<ListadoPersonalSelectorEconomicoDTO> listadoPersonal = new ArrayList<>();
            for (Personal personal : personalList) {
                listadoPersonal.add(ListadoPersonalSelectorEconomicoDTO.builder()
                        .idPersona(personal.getIdPersona())
                        .nombre(personal.getNombre() + " " + personal.getApellidos())
                        .build());
            }
            logger.info("Se ha obtenido un total de {} registros de personal para el económico con ID: {}", listadoPersonal.size(), idEconomico);
            return listadoPersonal;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public void crearBajaLaboral(CrearBajaLaboralDTO bajaLaboralDTO) {
        if (bajaLaboralDTO == null || bajaLaboralDTO.getIdPersona() <= 0) {
            throw new IllegalArgumentException("El DTO de creación de baja laboral no puede ser nulo y debe contener un ID de persona válido.");
        }
        Personal personal = personalRepository.findById(bajaLaboralDTO.getIdPersona())
                .orElseThrow(() -> new IllegalArgumentException("No existe un personal con el ID proporcionado."));

        BajaLaboral nuevaBajaLaboral = BajaLaboral.builder()
                .personal(personal)
                .fechaInicio(bajaLaboralDTO.getFechaInicio())
                .fechaFin(bajaLaboralDTO.getFechaFin())
                .horasPersonal(personal.getHorasPersonal())
                .horasDeBaja(0L)
                .build();

        try {
            bajaLaboralRepository.save(nuevaBajaLaboral);
            personal.getBajasLaborales().add(nuevaBajaLaboral);
            personal.getHorasPersonal().actualizarHorasMaximasAnuales();
            personalRepository.save(personal);
            this.costeHoraService.calcularCosteHoraEconomico(personal.getEconomico());
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la baja laboral: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarBajaLaboral(Long idBajaLaboral) {
       //Verificar que la baja existe
        BajaLaboral bajaLaboral = bajaLaboralRepository.findById(idBajaLaboral).orElseThrow(() -> new EntityNotFoundException("No se encontró la baja laboral con ID: " + idBajaLaboral));

        HorasPersonal horasPersonal = bajaLaboral.getHorasPersonal();
        Personal personal = bajaLaboral.getPersonal();

        // IMPORTANTE: Eliminar las referencias bidireccionales ANTES de eliminar la entidad
        if (horasPersonal != null) {
            horasPersonal.getBajas().remove(bajaLaboral);
            horasEmpleadoRepository.save(horasPersonal);
        }

        if (personal != null) {
            personal.getBajasLaborales().remove(bajaLaboral);
            personalRepository.save(personal);
            this.costeHoraService.calcularCosteHoraEconomico(personal.getEconomico());
        }

        // Ahora eliminar la entidad
        bajaLaboralRepository.delete(bajaLaboral);

        // Flush para forzar la operación
        bajaLaboralRepository.flush();

        // Recalcular las horas máximas después de eliminar
        if (horasPersonal != null) {
            horasPersonal.setHorasMaximasAnuales((long) horasPersonal.getHorasEfectivas());
            horasEmpleadoRepository.save(horasPersonal);
        }

        System.out.println("Baja laboral eliminada: ID " + idBajaLaboral);
    }

    @Override
    public void actualizarBajaLaboral(ActualizarBajaLaboralDTO actualizarBajaLaboralDTO) {
        if (actualizarBajaLaboralDTO.getIdBajaLaboral() <= 0) {
            throw new IllegalArgumentException("El ID de la baja laboral a actualizar no puede ser nulo o menor o igual a cero.");
        }

        BajaLaboral bajaLaboral = bajaLaboralRepository.findById(actualizarBajaLaboralDTO.getIdBajaLaboral())
                .orElseThrow(() -> new IllegalArgumentException("No existe una baja laboral con el ID proporcionado."));

        switch (actualizarBajaLaboralDTO.getCampoActualizado()) {
            case "fechaInicio":
                bajaLaboral.setFechaInicio(actualizarBajaLaboralDTO.getValor());
                break;
            case "fechaFin":
                bajaLaboral.setFechaFin(actualizarBajaLaboralDTO.getValor());
                break;
            default:
                throw new IllegalArgumentException("Campo actualizado no válido: " + actualizarBajaLaboralDTO.getCampoActualizado());
        }

        try {
            bajaLaboralRepository.save(bajaLaboral);
            this.costeHoraService.calcularCosteHoraEconomico(bajaLaboral.getPersonal().getEconomico());
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la baja laboral: " + e.getMessage(), e);
        }
    }

    @Override
    public void crearBonificacion(CrearBonificacionDTO dto) {
        Personal personal = personalRepository.findById(dto.getIdPersona())
                .orElseThrow(() -> new IllegalArgumentException("No existe personal con ID: " + dto.getIdPersona()));

        BonificacionesTrabajador bonificacion = BonificacionesTrabajador.builder()
                .tipoBonificacion(TiposBonificacion.valueOf(dto.getTipoBonificacion()))
                .porcentajeBonificacion(dto.getPorcentajeBonificacion())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .anioFiscal(dto.getAnioFiscal())
                .descripcion(dto.getDescripcion())
                .personal(personal)
                .build();

        bonificacionService.validarBonificacion(bonificacion);

        personal.getBonificaciones().add(bonificacion);
        personalRepository.save(personal);
        this.costeHoraService.calcularCosteHoraEconomico(personal.getEconomico());
    }

    @Override
    public Page<BonificacionesEmpleadoEconomicoDTO> obtenerBonificacionesEmpleadoPorEconomico(Long idEconomico, Pageable pageable) {
        Page<BonificacionesTrabajador> bonificacionesTrabajador = bonificacionesTrabajadorRepository.findBonificacionesTrabajadorsByIdEconomico(idEconomico,pageable);
        if (bonificacionesTrabajador != null && !bonificacionesTrabajador.isEmpty()) {
            return bonificacionesTrabajador.map(bonificaciones -> BonificacionesEmpleadoEconomicoDTO.builder()
                    .idPersona(bonificaciones.getPersonal().getIdPersona())
                    .nombre(bonificaciones.getPersonal().getNombre())
                    .dni(bonificaciones.getPersonal().getDni())
                    .idBonificacionTrabajador(bonificaciones.getIdBonificacionTrabajador())
                    .tipoBonificacion(bonificaciones.getTipoBonificacion())
                    .porcentajeBonificacion(bonificaciones.getPorcentajeBonificacion())
                    .fechaInicio(bonificaciones.getFechaInicio())
                    .fechaFin(bonificaciones.getFechaFin())
                    .anioFiscal(bonificaciones.getAnioFiscal())
                    .descripcion(bonificaciones.getDescripcion())
                    .build());
        } else {
            return Page.empty();
        }
    }

    @Override
    public void actualizarBonificacionEmpleado(ActualizarBonificacionDTO actualizarBonificacionEmpleadoDTO) {
        BonificacionesTrabajador bonificacionesTrabajador = bonificacionesTrabajadorRepository
                .findById(actualizarBonificacionEmpleadoDTO.getIdBonificacionTrabajador())
                .orElseThrow(() -> new IllegalArgumentException("No existe la bonificación con ID: "
                        + actualizarBonificacionEmpleadoDTO.getIdBonificacionTrabajador()));

        switch (actualizarBonificacionEmpleadoDTO.getCampoActualizado()) {
            case "porcentajeBonificacion":
                bonificacionesTrabajador.setPorcentajeBonificacion(convertirABigDecimal(actualizarBonificacionEmpleadoDTO.getValor()));
                break;
            case "tipoBonificacion":
                bonificacionesTrabajador.setTipoBonificacion(TiposBonificacion.valueOf(actualizarBonificacionEmpleadoDTO.getValor().toString()));
                break;
            case "fechaInicio":
                bonificacionesTrabajador.setFechaInicio(java.time.LocalDate.parse(actualizarBonificacionEmpleadoDTO.getValor().toString()));
                break;
            case "fechaFin":
                bonificacionesTrabajador.setFechaFin(java.time.LocalDate.parse(actualizarBonificacionEmpleadoDTO.getValor().toString()));
                break;
            case "descripcion":
                bonificacionesTrabajador.setDescripcion(actualizarBonificacionEmpleadoDTO.getValor().toString());
                break;
            default:
                throw new IllegalArgumentException("Campo actualizado no válido: " + actualizarBonificacionEmpleadoDTO.getCampoActualizado());
        }
        bonificacionesTrabajadorRepository.save(bonificacionesTrabajador);
        this.costeHoraService.calcularCosteHoraEconomico(bonificacionesTrabajador.getPersonal().getEconomico());
    }

    @Override
    public void eliminarBonificacionEmpleado(Long idBonificacion) {
        if (idBonificacion <= 0) {
            throw new IllegalArgumentException("El ID de la bonificación a eliminar no puede ser nulo o menor o igual a cero.");
        }
        BonificacionesTrabajador bonificacionesTrabajador = bonificacionesTrabajadorRepository.findById(idBonificacion).orElseThrow(() -> new IllegalArgumentException("No existe una bonificación con el ID proporcionado."));
        Personal persona = bonificacionesTrabajador.getPersonal();
        try {
            if (persona != null) {
                persona.getBonificaciones().remove(bonificacionesTrabajador);
                personalRepository.save(persona);
                this.costeHoraService.calcularCosteHoraEconomico(persona.getEconomico());
            } else {
                bonificacionesTrabajadorRepository.delete(bonificacionesTrabajador);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la bonificación del empleado: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<ResumenCostePersonalDTO> obtenerResumenCostePersonal(Long idEconomico, Pageable pageable) {
        if (idEconomico<= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        return actualizarCosteHoraPersonal(idEconomico, pageable);
    }

    @Override
    public Page<ResumenCostePersonalDTO> actualizarCosteHoraPersonal(Long idEconomico, Pageable pageable) {
        if (idEconomico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        Economico economico = this.economicoRepository.findById(idEconomico).orElseThrow(() -> new IllegalArgumentException("No existe un económico con el ID proporcionado."));
        this.costeHoraService.calcularCosteHoraEconomico(economico);
        Page<Personal> personalList = personalRepository.findPersonalByeconomicoId(idEconomico, pageable);
        return getResumenCostePersonalDTOS(personalList);
    }

    private Page<ResumenCostePersonalDTO> getResumenCostePersonalDTOS(Page<Personal> personalList) {
        if (personalList != null && !personalList.isEmpty()) {
            return personalList.map(persona -> {
                    var ch = persona.getCosteHoraPersonal();
                    return ResumenCostePersonalDTO.builder()
                    .idPersonal(persona.getIdPersona())
                    .nombre(persona.getNombre())
                    .dni(persona.getDni())
                    .puesto(persona.getPuesto())
                    .titulacion(obtenerTitulacionPrincipal(persona))
                    .departamento(persona.getDepartamento())
                    .idCosteHoraPersonal(ch.getId())
                    .retribucionTotal(ch.getRetribucionTotal())
                    .costeSS(ch.getCosteSS())
                    .horasAlta(ch.getHorasMaximas())
                    .horasMaximas(ch.getHorasMaximas())
                    .horasEfectivas(ch.getHorasEfectivas())
                    .horasBaja(ch.getHorasBaja())
                    .costeHora(ch.getCosteHora())
                    .cuotaCC(ch.getCuotaCC())
                    .cuotaATEP(ch.getCuotaATEP())
                    .cuotaDesempleo(ch.getCuotaDesempleo())
                    .cuotaFogasa(ch.getCuotaFogasa())
                    .cuotaFP(ch.getCuotaFP())
                    .cuotaMEI(ch.getCuotaMEI())
                    .tipoATEPAplicado(ch.getTipoATEPAplicado())
                    .origenTipoATEP(ch.getOrigenTipoATEP())
                    .ssEmpresaBruta(ch.getSsEmpresaBruta())
                    .ahorroBonificaciones(ch.getAhorroBonificaciones())
                    .ahorroInvestigador(ch.getAhorroInvestigador())
                    .ahorroOtrasBonificaciones(ch.getAhorroOtrasBonificaciones())
                    .build();
                    });
        } else {
            return Page.empty();
        }
    }

    private String obtenerTitulacionPrincipal(Personal persona) {
        if (persona.getTitulacion1() != null && !persona.getTitulacion1().isBlank()) {
            return persona.getTitulacion1();
        }
        if (persona.getTitulacion2() != null && !persona.getTitulacion2().isBlank()) {
            return persona.getTitulacion2();
        }
        if (persona.getTitulacion3() != null && !persona.getTitulacion3().isBlank()) {
            return persona.getTitulacion3();
        }
        if (persona.getTitulacion4() != null && !persona.getTitulacion4().isBlank()) {
            return persona.getTitulacion4();
        }
        return null;
    }

    private BigDecimal convertirABigDecimal(Object valor) {
        return switch (valor) {
            case null -> null;
            case BigDecimal bigDecimal -> bigDecimal;
            case Integer i -> BigDecimal.valueOf(i.longValue());
            case Long l -> BigDecimal.valueOf(l);
            case Double v -> BigDecimal.valueOf(v);
            case String s -> new BigDecimal(s);
            case Number number -> BigDecimal.valueOf(number.doubleValue());
            default -> throw new IllegalArgumentException("No se puede convertir el valor a BigDecimal: " + valor);
        };
    }

    private void actualizarTodasBajasLaborales() {
        List<BajaLaboral> todasBajas = bajaLaboralRepository.findAll();
        for (BajaLaboral baja : todasBajas) {
            baja.recalcularHorasDeBaja();
            baja.getHorasPersonal().actualizarHorasMaximasAnuales();
            bajaLaboralRepository.save(baja);
        }
    }
}
