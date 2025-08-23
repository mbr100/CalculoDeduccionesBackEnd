package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.helper.ValoresDefecto;
import com.marioborrego.api.calculodeduccionesbackend.personal.business.interfaces.PersonalService;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.*;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.*;
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
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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

    private final Logger logger = LoggerFactory.getLogger(PersonalServiceImpl.class);



    public PersonalServiceImpl(PersonalRepository personalRepository, EconomicoRepository economicoRepository, RetribucionRepository retribucionRepository,
                               BasesCotizacionRepository basesCotizacionRepository, HorasEmpleadoRepository horasEmpleadoRepository,
                               BajaLaboralRepository bajaLaboralRepository, BonificacionesTrabajadorRepository bonificacionesTrabajadorRepository) {
        this.bonificacionesTrabajadorRepository = bonificacionesTrabajadorRepository;
        this.basesCotizacionRepository = basesCotizacionRepository;
        this.retribucionRepository = retribucionRepository;
        this.economicoRepository = economicoRepository;
        this.personalRepository = personalRepository;
        this.horasEmpleadoRepository = horasEmpleadoRepository;
        this.bajaLaboralRepository = bajaLaboralRepository;
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
                    .build());
        }
        return null;
    }

    @Override
    public PersonalEconomicoDTO crearPersonalEconomico(PersonalEconomicoDTO personalEconomicoDTO) {
        if (personalEconomicoDTO == null || personalEconomicoDTO.getIdEconomico() == 0) {
            throw new IllegalArgumentException("El DTO de creación de personal económico no puede ser nulo y debe contener un ID económico.");
        }
        if (personalEconomicoDTO.getIdPersona() != 0) {
            Optional<Personal> existingPersonal = personalRepository.findById(personalEconomicoDTO.getIdPersona());
            if (existingPersonal.isPresent()) {
                throw new IllegalArgumentException("Ya existe un personal con el ID proporcionado.");
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
                .economico(economico)
                .retribucion(retribucion)
                .basesCotizacion(basesCotizacion)
                .horasPersonal(horasPersonal)
                .bonificacionesTrabajador(null) // Será null si no se especifica
                .bajasLaborales(new ArrayList<>())
                .build();

        // 6. Establecer relaciones bidireccionales
        retribucion.setPersonal(newPersonal);
        basesCotizacion.setPersona(newPersonal);
        horasPersonal.setPersonal(newPersonal);

        try {
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
                    .idEconomico(savedPersonal.getEconomico().getIdEconomico())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el personal económico: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarPersonalEconomico(int id, Long economico) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del personal económico a eliminar no puede ser nulo o menor o igual a cero.");
        }
        if (economico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        Optional<Personal> personal = personalRepository.findById(id);
        if (personal.isPresent()) {
            if (personal.get().getEconomico().getIdEconomico() != (economico)) {
                throw new IllegalArgumentException("El personal económico no pertenece al económico proporcionado.");
            }
            personalRepository.delete(personal.get());
        } else {
            throw new IllegalArgumentException("No existe un personal económico con el ID proporcionado.");
        }
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
            if (personal.get().getEconomico().getIdEconomico() != economico.getIdEconomico()) {
                throw new IllegalArgumentException("El personal económico no pertenece al económico proporcionado.");
            }
            Personal updatedPersonal = getPersonal(personalEconomicoDTO, personal, economico);
            personalRepository.save(updatedPersonal);
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
        updatedPersonal.setEsPersonalInvestigador(personalEconomicoDTO.isEsPersonalInvestigador());
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
        Page<Personal> personalList = personalRepository.findPersonalByeconomicoId(idEconomico, Pageable.unpaged());
        if (personalList != null && !personalList.isEmpty()) {
            return personalList.map(personal -> BbccPersonalDTO.builder()
                    .idPersonal(personal.getIdPersona())
                    .nombre(personal.getNombre())
                    .dni(personal.getDni())
                    .id_baseCotizacion(personal.getBasesCotizacion().getId_baseCotizacion())
                    .basesCotizacionContingenciasComunesEnero(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesEnero())
                    .basesCotizacionContingenciasComunesFebrero(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesFebrero())
                    .basesCotizacionContingenciasComunesMarzo(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesMarzo())
                    .basesCotizacionContingenciasComunesAbril(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesAbril())
                    .basesCotizacionContingenciasComunesMayo(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesMayo())
                    .basesCotizacionContingenciasComunesJunio(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesJunio())
                    .basesCotizacionContingenciasComunesJulio(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesJulio())
                    .basesCotizacionContingenciasComunesAgosto(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesAgosto())
                    .basesCotizacionContingenciasComunesSeptiembre(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesSeptiembre())
                    .basesCotizacionContingenciasComunesOctubre(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesOctubre())
                    .basesCotizacionContingenciasComunesNoviembre(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesNoviembre())
                    .basesCotizacionContingenciasComunesDiciembre(personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesDiciembre())
                    .build());
        } else {
            return Page.empty();
        }
    }

    @Override
    public void actualizarRetribucionPersonal(ActualizarRetribucionDTO actualizarRetribucionDTO) {
        if (actualizarRetribucionDTO.getIdRetribucion() <= 0) {
            throw new IllegalArgumentException("El ID de la retribución a actualizar no puede ser nulo o menor o igual a cero.");
        }
        Retribucion retribucion = retribucionRepository.findById(actualizarRetribucionDTO.getIdRetribucion())
                .orElseThrow(() -> new IllegalArgumentException("No existe una retribución con el ID proporcionado."));
        switch (actualizarRetribucionDTO.getCampoActualizado()) {
            case "importeRetribucionNoIT":
                retribucion.setImporteRetribucionNoIT(actualizarRetribucionDTO.getValor().longValue());
                break;
            case "importeRetribucionExpecie":
                retribucion.setImporteRetribucionExpecie(actualizarRetribucionDTO.getValor().longValue());
                break;
            case "aportacionesPrevencionSocial":
                retribucion.setAportaciones_prevencion_social(actualizarRetribucionDTO.getValor().longValue());
                break;
            case "dietasViajeExentas":
                retribucion.setDietas_viaje_exentas(actualizarRetribucionDTO.getValor().longValue());
                break;
            case "rentasExentas190":
                retribucion.setRentas_exentas_190(actualizarRetribucionDTO.getValor().longValue());
                break;
            default:
                throw new IllegalArgumentException("Campo actualizado no válido: " + actualizarRetribucionDTO.getCampoActualizado());
        }
        try {
            retribucionRepository.save(retribucion);
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
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar las bases de cotización del personal: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<AltaEjercicioDTO> obtenerTodoPersonalAltaEjercicio(Long idEconomico, Pageable pageable) {
        if (idEconomico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        Page<Personal> personalList = personalRepository.findPersonalByeconomicoId(idEconomico, pageable);
        if (personalList != null && !personalList.isEmpty()) {
            return personalList.map(personal -> AltaEjercicioDTO.builder()
                    .idPersona(personal.getIdPersona())
                    .nombre(personal.getNombre())
                    .dni(personal.getDni())
                    .idAltaEjercicio(personal.getHorasPersonal().getId())
                    .fechaAltaEjercicio(personal.getHorasPersonal().getFechaAltaEjercicio())
                    .fechaBajaEjercicio(personal.getHorasPersonal().getFechaBajaEjercicio())
                    .horasConvenioAnual(personal.getHorasPersonal().getHorasConvenioAnual())
                    .horasMaximasAnuales(personal.getHorasPersonal().getHorasMaximasAnuales())
                    .build());
        } else {
            return Page.empty();
        }
    }

    @Override
    public void actualizarAltaEjercicio(ActualizarAltaEjercicioDTO actualizarAltaEjercicioDTO) {
        if (actualizarAltaEjercicioDTO.getIdAltaEjercicio() <= 0) {
            throw new IllegalArgumentException("El ID del alta de ejercicio no puede ser nulo o menor o igual a cero.");
        }

        HorasPersonal horasPersonal = horasEmpleadoRepository.findById(actualizarAltaEjercicioDTO.getIdAltaEjercicio())
                .orElseThrow(() -> new IllegalArgumentException("No existe un alta de ejercicio con el ID proporcionado."));

        switch (actualizarAltaEjercicioDTO.getCampoActualizado()) {
            case "fechaAltaEjercicio":
                horasPersonal.setFechaAltaEjercicio(OffsetDateTime.parse(actualizarAltaEjercicioDTO.getValor()).toLocalDate());
                break;
            case "fechaBajaEjercicio":
                horasPersonal.setFechaBajaEjercicio(OffsetDateTime.parse(actualizarAltaEjercicioDTO.getValor()).toLocalDate());
                break;
            case "horasConvenioAnual":
                horasPersonal.setHorasConvenioAnual(Long.valueOf(actualizarAltaEjercicioDTO.getValor()));
                break;
            default:
                throw new IllegalArgumentException("Campo actualizado no válido: " + actualizarAltaEjercicioDTO.getCampoActualizado());
        }

        try {
            personalRepository.save(horasPersonal.getPersonal()); // se persiste a través de la relación con Personal
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar el alta de ejercicio del personal: " + e.getMessage(), e);
        }
    }

    @Override
    public Page<BajasLaboralesDTO> obtenerBajasLaboralesPorEconomico(Long idEconomico, Pageable pageable) {
        if (idEconomico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        this.actualizarTodasBajasLaborales();
        Page<BajaLaboral> bajaslaboralespersonalEconomico = bajaLaboralRepository.findByPersonalEconomicoIdEconomico(idEconomico, pageable);
        if (bajaslaboralespersonalEconomico != null && !bajaslaboralespersonalEconomico.isEmpty()) {
            return bajaslaboralespersonalEconomico.map(bajaLaboral -> BajasLaboralesDTO.builder()
                    .idPersona(bajaLaboral.getPersonal().getIdPersona())
                    .nombre(bajaLaboral.getPersonal().getNombre())
                    .dni(bajaLaboral.getPersonal().getDni())
                    .idBajaLaboral(bajaLaboral.getId())
                    .fechaInicio(bajaLaboral.getFechaInicio())
                    .fechaFin(bajaLaboral.getFechaFin())
                    .horasDeBaja(bajaLaboral.getHorasDeBaja())
                    .build());
        } else {
            return Page.empty();
        }
    }

    @Override
    public List<ListadoPersonalSelectorEconomicoDTO> obtenerTodoPersonalSelectorEconomico(int idEconomico) {
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
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la baja laboral: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminarBajaLaboral(Long idBajaLaboral) {
       //Verificar que la baja existe
        BajaLaboral bajaLaboral = bajaLaboralRepository.findById(idBajaLaboral)
                .orElseThrow(() -> new EntityNotFoundException("No se encontró la baja laboral con ID: " + idBajaLaboral));

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
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la baja laboral: " + e.getMessage(), e);
        }
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
                    .build());
        } else {
            return Page.empty();
        }
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
