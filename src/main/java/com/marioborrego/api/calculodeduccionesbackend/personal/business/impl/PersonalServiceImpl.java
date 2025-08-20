package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.business.interfaces.PersonalService;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.*;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.BasesCotizacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PersonalRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.RetribucionRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

@Service
public class PersonalServiceImpl implements PersonalService {
    private final PersonalRepository personalRepository;
    private final EconomicoRepository economicoRepository;
    private final RetribucionRepository retribucionRepository;
    private final BasesCotizacionRepository basesCotizacionRepository;

    public PersonalServiceImpl(PersonalRepository personalRepository, EconomicoRepository economicoRepository, RetribucionRepository retribucionRepository, BasesCotizacionRepository basesCotizacionRepository) {
        this.basesCotizacionRepository = basesCotizacionRepository;
        this.retribucionRepository = retribucionRepository;
        this.economicoRepository = economicoRepository;
        this.personalRepository = personalRepository;
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
        Retribucion retribucion = Retribucion.builder()
                .importeRetribucionNoIT(0L)
                .importeRetribucionExpecie(0L)
                .aportaciones_prevencion_social(0L)
                .dietas_viaje_exentas(0L)
                .rentas_exentas_190(0L)
                .build();

        // 2. Crear Bases de Cotización con valores por defecto (0 para todos los meses)
        BasesCotizacion basesCotizacion = BasesCotizacion.builder()
                .basesCotizacionContingenciasComunesEnero(0L)
                .basesCotizacionContingenciasComunesFebrero(0L)
                .basesCotizacionContingenciasComunesMarzo(0L)
                .basesCotizacionContingenciasComunesAbril(0L)
                .basesCotizacionContingenciasComunesMayo(0L)
                .basesCotizacionContingenciasComunesJunio(0L)
                .basesCotizacionContingenciasComunesJulio(0L)
                .basesCotizacionContingenciasComunesAgosto(0L)
                .basesCotizacionContingenciasComunesSeptiembre(0L)
                .basesCotizacionContingenciasComunesOctubre(0L)
                .basesCotizacionContingenciasComunesNoviembre(0L)
                .basesCotizacionContingenciasComunesDiciembre(0L)
                .build();

        // 3. Crear Horas Personal con valores por defecto
        HorasPersonal horasPersonal = HorasPersonal.builder()
                .ejercicio(economico.getAnualidad()) // Año actual
                .fechaAltaEjercicio(LocalDate.of(economico.getAnualidad(), 1,1)) // 1 de enero del año actual
                .fechaBajaEjercicio(LocalDate.of(economico.getAnualidad(), 12,31)) // 31 de diciembre del año actual
                .horasConvenioAnual(economico.getHorasConvenio()) // Usar las horas del convenio de la empresa
                .bajas(new ArrayList<>())
                .build();

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
                .bajasLaborales(new ArrayList<BajaLaboral>())
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
        if (personalEconomicoDTO.getIdEconomico()<= 0) {
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
            personalRepository.save(updatedPersonal);
        } else {
            throw new IllegalArgumentException("No existe un personal económico con el ID proporcionado.");
        }
    }

    @Override
    public Page<RetribucionesPersonalDTO> obtenerRetribucionesPersonalPorEconomico(Long id) {
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
    public Page<BbccPersonalDTO> obtenerCotizacionesPersonalPorEconomico(Long idEconomico) {
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
        switch (actualizarRetribucionDTO.getCampoActualizado()){
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
}
