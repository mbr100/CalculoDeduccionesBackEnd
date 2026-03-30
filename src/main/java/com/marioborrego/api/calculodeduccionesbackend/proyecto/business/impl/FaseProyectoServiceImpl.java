package com.marioborrego.api.calculodeduccionesbackend.proyecto.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.PartidaGastoDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.AsignacionFase;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.FaseProyecto;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.Proyecto;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.ProyectoPersonal;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.AsignacionFaseRepository;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.FaseProyectoRepository;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.ProyectoPersonalRepository;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.ProyectoRepository;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.presentation.dto.fases.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class FaseProyectoServiceImpl {

    private final FaseProyectoRepository faseProyectoRepository;
    private final AsignacionFaseRepository asignacionFaseRepository;
    private final ProyectoRepository proyectoRepository;
    private final ProyectoPersonalRepository proyectoPersonalRepository;

    public FaseProyectoServiceImpl(FaseProyectoRepository faseProyectoRepository,
                                   AsignacionFaseRepository asignacionFaseRepository,
                                   ProyectoRepository proyectoRepository,
                                   ProyectoPersonalRepository proyectoPersonalRepository) {
        this.faseProyectoRepository = faseProyectoRepository;
        this.asignacionFaseRepository = asignacionFaseRepository;
        this.proyectoRepository = proyectoRepository;
        this.proyectoPersonalRepository = proyectoPersonalRepository;
    }

    public List<FaseProyectoDTO> listarFases(Long idProyecto) {
        return faseProyectoRepository.findByProyectoIdProyecto(idProyecto).stream()
                .map(f -> FaseProyectoDTO.builder()
                        .idFase(f.getIdFase())
                        .nombre(f.getNombre())
                        .build())
                .toList();
    }

    public FaseProyectoDTO crearFase(CrearFaseProyectoDTO dto) {
        Proyecto proyecto = proyectoRepository.findById(dto.getIdProyecto())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + dto.getIdProyecto()));

        FaseProyecto fase = FaseProyecto.builder()
                .nombre(dto.getNombre())
                .proyecto(proyecto)
                .build();
        fase = faseProyectoRepository.save(fase);

        return FaseProyectoDTO.builder()
                .idFase(fase.getIdFase())
                .nombre(fase.getNombre())
                .build();
    }

    public FaseProyectoDTO actualizarFase(ActualizarFaseProyectoDTO dto) {
        FaseProyecto fase = faseProyectoRepository.findById(dto.getIdFase())
                .orElseThrow(() -> new IllegalArgumentException("Fase no encontrada: " + dto.getIdFase()));

        fase.setNombre(dto.getNombre());
        fase = faseProyectoRepository.save(fase);

        return FaseProyectoDTO.builder()
                .idFase(fase.getIdFase())
                .nombre(fase.getNombre())
                .build();
    }

    @Transactional
    public void eliminarFase(Long idFase) {
        if (!faseProyectoRepository.existsById(idFase)) {
            throw new IllegalArgumentException("Fase no encontrada: " + idFase);
        }
        faseProyectoRepository.deleteById(idFase);
    }

    @Transactional(readOnly = true)
    public MatrizAsignacionFasesDTO obtenerMatrizAsignacionFases(Long idProyecto) {
        List<FaseProyecto> fases = faseProyectoRepository.findByProyectoIdProyecto(idProyecto);
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + idProyecto));

        List<FaseProyectoDTO> fasesDTO = fases.stream()
                .map(f -> FaseProyectoDTO.builder().idFase(f.getIdFase()).nombre(f.getNombre()).build())
                .toList();

        List<AsignacionFase> todasAsignaciones = asignacionFaseRepository.findByProyectoPersonalProyectoIdProyecto(idProyecto);

        List<FilaAsignacionFaseDTO> filas = proyecto.getProyectoPersonales().stream()
                .map(pp -> {
                    var personal = pp.getPersonal();
                    Double costeHora = personal.getCosteHoraPersonal() != null
                            && personal.getCosteHoraPersonal().getCosteHora() != null
                            ? personal.getCosteHoraPersonal().getCosteHora().doubleValue()
                            : 0.0;

                    List<Double> porcentajes = fases.stream()
                            .map(fase -> todasAsignaciones.stream()
                                    .filter(a -> a.getProyectoPersonal().getId().equals(pp.getId())
                                            && a.getFaseProyecto().getIdFase().equals(fase.getIdFase()))
                                    .map(AsignacionFase::getPorcentajeDedicacion)
                                    .findFirst()
                                    .orElse(0.0))
                            .toList();

                    return FilaAsignacionFaseDTO.builder()
                            .idPersonal(personal.getIdPersona())
                            .nombreCompleto(personal.getNombre() + " " + personal.getApellidos())
                            .idProyectoPersonal(pp.getId())
                            .horasAsignadas(pp.getHorasAsignadas() != null ? pp.getHorasAsignadas() : 0.0)
                            .costeHora(costeHora)
                            .porcentajes(porcentajes)
                            .build();
                })
                .toList();

        return MatrizAsignacionFasesDTO.builder()
                .fases(fasesDTO)
                .filas(filas)
                .build();
    }

    @Transactional
    public void actualizarAsignacionFase(ActualizarAsignacionFaseDTO dto) {
        ProyectoPersonal pp = proyectoPersonalRepository.findById(dto.getIdProyectoPersonal())
                .orElseThrow(() -> new IllegalArgumentException("ProyectoPersonal no encontrado: " + dto.getIdProyectoPersonal()));
        FaseProyecto fase = faseProyectoRepository.findById(dto.getIdFase())
                .orElseThrow(() -> new IllegalArgumentException("Fase no encontrada: " + dto.getIdFase()));

        // Validar que la suma de porcentajes no supere 100%
        double sumaOtros = pp.getAsignacionesFase().stream()
                .filter(a -> !a.getFaseProyecto().getIdFase().equals(dto.getIdFase()))
                .mapToDouble(AsignacionFase::getPorcentajeDedicacion)
                .sum();

        if (sumaOtros + dto.getPorcentajeDedicacion() > 100.0) {
            throw new IllegalArgumentException("La suma de porcentajes no puede superar el 100%. Suma actual de otras fases: " + sumaOtros + "%");
        }

        var existente = asignacionFaseRepository.findByProyectoPersonalIdAndFaseProyectoIdFase(pp.getId(), fase.getIdFase());

        if (existente.isPresent()) {
            if (dto.getPorcentajeDedicacion() > 0) {
                existente.get().setPorcentajeDedicacion(dto.getPorcentajeDedicacion());
                asignacionFaseRepository.save(existente.get());
            } else {
                asignacionFaseRepository.delete(existente.get());
            }
        } else if (dto.getPorcentajeDedicacion() > 0) {
            AsignacionFase nueva = AsignacionFase.builder()
                    .proyectoPersonal(pp)
                    .faseProyecto(fase)
                    .porcentajeDedicacion(dto.getPorcentajeDedicacion())
                    .build();
            asignacionFaseRepository.save(nueva);
        }
    }

    @Transactional(readOnly = true)
    public List<ResumenGastoFaseDTO> calcularResumenGastoPorFase(Long idProyecto) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + idProyecto));

        List<FaseProyecto> fases = faseProyectoRepository.findByProyectoIdProyecto(idProyecto);
        List<AsignacionFase> todasAsignaciones = asignacionFaseRepository.findByProyectoPersonalProyectoIdProyecto(idProyecto);

        Long porcentajeDeduccion = determinarPorcentajeDeduccion(proyecto.getCalificacion());

        return fases.stream().map(fase -> {
            double gastoPersonalFase = todasAsignaciones.stream()
                    .filter(a -> a.getFaseProyecto().getIdFase().equals(fase.getIdFase()))
                    .mapToDouble(a -> {
                        var pp = a.getProyectoPersonal();
                        double horas = pp.getHorasAsignadas() != null ? pp.getHorasAsignadas() : 0.0;
                        double costeHora = pp.getPersonal().getCosteHoraPersonal() != null
                                && pp.getPersonal().getCosteHoraPersonal().getCosteHora() != null
                                ? pp.getPersonal().getCosteHoraPersonal().getCosteHora().doubleValue()
                                : 0.0;
                        return horas * (a.getPorcentajeDedicacion() / 100.0) * costeHora;
                    })
                    .sum();

            List<PartidaGastoDTO> partidas = Arrays.asList(
                    PartidaGastoDTO.builder().tipoGasto("Personal").importe(gastoPersonalFase).build(),
                    PartidaGastoDTO.builder().tipoGasto("Colaboraciones Externas").importe(0.0).build(),
                    PartidaGastoDTO.builder().tipoGasto("Materiales Fungibles").importe(0.0).build(),
                    PartidaGastoDTO.builder().tipoGasto("Amortizaciones").importe(0.0).build(),
                    PartidaGastoDTO.builder().tipoGasto("Otros").importe(0.0).build()
            );

            double deduccion = gastoPersonalFase * porcentajeDeduccion / 100.0;

            return ResumenGastoFaseDTO.builder()
                    .idFase(fase.getIdFase())
                    .nombreFase(fase.getNombre())
                    .partidas(partidas)
                    .total(gastoPersonalFase)
                    .porcentajeDeduccion(porcentajeDeduccion)
                    .deduccion(deduccion)
                    .build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<ResumenGastoFasePersonaDTO> calcularDesgloseFasePersona(Long idProyecto) {
        List<FaseProyecto> fases = faseProyectoRepository.findByProyectoIdProyecto(idProyecto);
        List<AsignacionFase> todasAsignaciones = asignacionFaseRepository.findByProyectoPersonalProyectoIdProyecto(idProyecto);

        List<ResumenGastoFasePersonaDTO> resultado = new ArrayList<>();

        for (FaseProyecto fase : fases) {
            todasAsignaciones.stream()
                    .filter(a -> a.getFaseProyecto().getIdFase().equals(fase.getIdFase()))
                    .forEach(a -> {
                        var pp = a.getProyectoPersonal();
                        var personal = pp.getPersonal();
                        double horas = pp.getHorasAsignadas() != null ? pp.getHorasAsignadas() : 0.0;
                        double costeHora = personal.getCosteHoraPersonal() != null
                                && personal.getCosteHoraPersonal().getCosteHora() != null
                                ? personal.getCosteHoraPersonal().getCosteHora().doubleValue()
                                : 0.0;
                        double horasDedicadas = horas * (a.getPorcentajeDedicacion() / 100.0);

                        resultado.add(ResumenGastoFasePersonaDTO.builder()
                                .idFase(fase.getIdFase())
                                .nombreFase(fase.getNombre())
                                .idPersonal(personal.getIdPersona())
                                .nombreCompleto(personal.getNombre() + " " + personal.getApellidos())
                                .horasAsignadas(horas)
                                .porcentajeDedicacion(a.getPorcentajeDedicacion())
                                .horasDedicadas(horasDedicadas)
                                .costeHora(costeHora)
                                .gastoPersonal(horasDedicadas * costeHora)
                                .build());
                    });
        }

        return resultado;
    }

    private Long determinarPorcentajeDeduccion(com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Calificacion calificacion) {
        if (calificacion == null) return 0L;
        return switch (calificacion) {
            case IT -> 12L;
            case I_MAS_D, I_MAS_D_MAS_I -> 25L;
            case NADA -> 0L;
        };
    }
}
