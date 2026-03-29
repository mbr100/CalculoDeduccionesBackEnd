package com.marioborrego.api.calculodeduccionesbackend.proyecto.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.business.impl.CosteHoraService;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PersonalRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.ActualizacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.business.interfaces.ProyectoService;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.Proyecto;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.ProyectoPersonal;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Calificacion;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Estrategia;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.ProyectoRepository;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.*;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.enums.ProyectoCampo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ProyectoServiceImpl implements ProyectoService {
    private final ProyectoRepository proyectoRepository;
    private final EconomicoRepository economicoRepository;
    private final PersonalRepository personalRepository;
    private final CosteHoraService costeHoraService;


    public ProyectoServiceImpl(ProyectoRepository proyectoRepository,
                               EconomicoRepository economicoRepository,
                               PersonalRepository personalRepository,
                               CosteHoraService costeHoraService) {
        this.personalRepository = personalRepository;
        this.economicoRepository = economicoRepository;
        this.proyectoRepository = proyectoRepository;
        this.costeHoraService = costeHoraService;
    }

    @Override
    public Page<ListadoDeProyectosResponseDTO> listarProyectosPorEconomico(Pageable pageable, Long idEconomico) {
        if (idEconomico == null || idEconomico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }
        Page<Proyecto> proyectos = proyectoRepository.findByEconomico(idEconomico, pageable);
        if (proyectos.isEmpty()) {
            return Page.empty();
        } else {
            return proyectos.map(proyecto -> ListadoDeProyectosResponseDTO.builder()
                    .idProyecto(proyecto.getIdProyecto())
                    .acronimo(proyecto.getAcronimo())
                    .titulo(proyecto.getTitulo())
                    .fechaInicio(proyecto.getFechaInicio())
                    .fechaFin(proyecto.getFechaFin())
                    .estrategia(proyecto.getEstrategia())
                    .calificacion(proyecto.getCalificacion())
                    .build());
        }
    }

    @Override
    public void crearProyecto(CrearProyectoDTO crearProyectoDTO) {
        if (crearProyectoDTO.getIdProyecto() != null) {
            throw new IllegalArgumentException("El ID del proyecto debe ser nulo al crear un nuevo proyecto.");
        }
       try {
           Economico e = economicoRepository.findById(crearProyectoDTO.getIdEconomico())
                   .orElseThrow(() -> new IllegalArgumentException("No se encontró un económico con el ID proporcionado: " + crearProyectoDTO.getIdEconomico()));
           if (crearProyectoDTO.getAcronimo().length() != 10) {
               throw new IllegalArgumentException("El acrónimo debe tener exactamente 10 caracteres.");
           }

           Proyecto proyecto = Proyecto.builder()
                   .acronimo(crearProyectoDTO.getAcronimo())
                   .titulo(crearProyectoDTO.getTitulo())
                   .fechaInicio(crearProyectoDTO.getFechaInicio())
                   .fechaFin(crearProyectoDTO.getFechaFin())
                   .estrategia(crearProyectoDTO.getEstrategia())
                   .calificacion(crearProyectoDTO.getCalificacion())
                   .economico(e)
                   .build();
              proyectoRepository.save(proyecto);
       } catch (Exception ex) {
           throw new RuntimeException("Error al crear el proyecto: " + ex.getMessage(), ex);
       }
    }

    public void actualizarProyecto(ActualizacionDTO<Object, ProyectoCampo> dto) {
        Proyecto proyecto = proyectoRepository.findById(dto.getId()).orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        Object valor = dto.getValor();

        switch (dto.getCampoActualizado()) {
            case ACRONIMO:
                proyecto.setAcronimo(valor.toString());
                break;

            case TITULO:
                proyecto.setTitulo(valor.toString());
                break;

            case FECHA_INICIO:
                proyecto.setFechaInicio(parseToLocalDate(valor));
                break;

            case FECHA_FIN:
                proyecto.setFechaFin(parseToLocalDate(valor));
                break;

            case ESTRATEGIA:
                proyecto.setEstrategia(parseToEnum(valor, Estrategia.class));
                break;

            case CALIFICACION:
                proyecto.setCalificacion(parseToEnum(valor, Calificacion.class));
                break;
            default:
                throw new IllegalArgumentException("Campo no soportado: " + dto.getCampoActualizado());
        }

        proyectoRepository.save(proyecto);
    }

    @Override
    public void eliminarProyecto(Long idProyecto) {
        if (!proyectoRepository.existsById(idProyecto)) {
            throw new IllegalArgumentException("No se encontró un proyecto con el ID proporcionado: " + idProyecto);
        }
        proyectoRepository.deleteById(idProyecto);
    }

    @Override
    @Transactional
    public MatrizAsignacionesDTO listarPersonalPorProyectoAsignacion(Long idEconomico) {
        if (idEconomico == null || idEconomico <= 0) {
            throw new IllegalArgumentException("El ID del económico no puede ser nulo o menor o igual a cero.");
        }

        Economico economico = economicoRepository.findById(idEconomico)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un económico con el ID proporcionado: " + idEconomico));

        // Recalcula y persiste horas/coste para devolver siempre datos actualizados.
        costeHoraService.calcularCosteHoraEconomico(economico);

        List<Proyecto> proyectos = proyectoRepository.findAllByIdEconomico(idEconomico);
        // Releer tras el recálculo para usar los valores ya persistidos en BBDD.
        List<Personal> personas = personalRepository.findByEconomicoId(idEconomico);
        if (proyectos.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron proyectos para el ID de económico proporcionado: " + idEconomico);
        }
        if (personas.isEmpty()){
            throw new IllegalArgumentException("No se encontraron personas para el ID de económico proporcionado: " + idEconomico);
        }
        List<ProyectoAsignacionDTO> nombresProyectos = proyectos.stream()
                .map(proyecto -> ProyectoAsignacionDTO.builder()
                            .idProyecto(proyecto.getIdProyecto())
                            .acronimo(proyecto.getAcronimo())
                            .build()
                ).toList();
        List<FilaAsignacionDTO> filas = personas.stream().map(persona -> {
                    List<Double> horas = proyectos.stream().map(p -> persona.getProyectoPersonales().stream()
                                    .filter(pp -> pp.getProyecto().getIdProyecto().equals(p.getIdProyecto()))
                                    .map(ProyectoPersonal::getHorasAsignadas)
                                    .findFirst()
                                    .orElse(0.0)
                            ).toList();
                    Double horasEfectivas = persona.getCosteHoraPersonal() != null
                            && persona.getCosteHoraPersonal().getHorasMaximas() != null
                            ? persona.getCosteHoraPersonal().getHorasMaximas().doubleValue()
                            : 0.0;
                    return FilaAsignacionDTO.builder()
                            .idPersonal(persona.getIdPersona())
                            .nombreCompleto(persona.getNombre() + " " + persona.getApellidos())
                            .horas(horas)
                            .horasEfectivas(horasEfectivas)
                            .build();
                })
                .toList();
        return MatrizAsignacionesDTO.builder()
                .proyectos(nombresProyectos)
                .filas(filas)
                .build();
    }

    @Override
    public void actualizarAsignaciones(ActualizarAsignacionDTO asignacion) {
        Proyecto proyecto = proyectoRepository.findById(asignacion.getIdProyecto())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un proyecto con el ID proporcionado: " + asignacion.getIdProyecto()));
        Personal persona = personalRepository.findById(asignacion.getIdPersonal())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró una persona con el ID proporcionado: " + asignacion.getIdPersonal()));

        ProyectoPersonal proyectoPersonal = persona.getProyectoPersonales().stream()
                .filter(pp -> pp.getProyecto().getIdProyecto().equals(proyecto.getIdProyecto()))
                .findFirst()
                .orElse(null);

        if (proyectoPersonal == null) {
            if (asignacion.getHoras() > 0) {
                ProyectoPersonal nuevoPP = ProyectoPersonal.builder()
                        .personal(persona)
                        .proyecto(proyecto)
                        .horasAsignadas(Double.valueOf(asignacion.getHoras()))
                        .build();
                persona.getProyectoPersonales().add(nuevoPP);
            }
        } else {
            if (asignacion.getHoras() > 0) {
                proyectoPersonal.setHorasAsignadas(Double.valueOf(asignacion.getHoras()));
            } else {
                persona.getProyectoPersonales().remove(proyectoPersonal);
            }
        }
        personalRepository.save(persona);
    }

    private LocalDate parseToLocalDate(Object valor) {
        if (valor instanceof LocalDate ld) {
            return ld;
        } else if (valor instanceof String str) {
            OffsetDateTime odt = OffsetDateTime.parse(str, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            return odt.toLocalDate();
        } else if (valor instanceof Long epochMillis) {
            return LocalDate.ofEpochDay(epochMillis / (24 * 60 * 60 * 1000));
        }
        throw new IllegalArgumentException("No se puede convertir a LocalDate: " + valor);
    }

    @SuppressWarnings("unchecked")
    private <E extends Enum<E>> E parseToEnum(Object valor, Class<E> enumClass) {
        if (valor instanceof String str) {
            return Enum.valueOf(enumClass, str);
        } else if (valor instanceof Enum<?> e) {
            return (E) e;
        }
        throw new IllegalArgumentException("No se puede convertir a Enum: " + valor);
    }
}
