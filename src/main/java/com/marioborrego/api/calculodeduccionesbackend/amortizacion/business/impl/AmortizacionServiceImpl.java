package com.marioborrego.api.calculodeduccionesbackend.amortizacion.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.amortizacion.business.interfaces.AmortizacionService;
import com.marioborrego.api.calculodeduccionesbackend.amortizacion.domain.models.ActivoAmortizable;
import com.marioborrego.api.calculodeduccionesbackend.amortizacion.domain.models.ImputacionActivoFase;
import com.marioborrego.api.calculodeduccionesbackend.amortizacion.domain.repository.ActivoAmortizableRepository;
import com.marioborrego.api.calculodeduccionesbackend.amortizacion.domain.repository.ImputacionActivoFaseRepository;
import com.marioborrego.api.calculodeduccionesbackend.amortizacion.presentation.dto.*;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.FaseProyecto;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.Proyecto;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.FaseProyectoRepository;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.ProyectoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AmortizacionServiceImpl implements AmortizacionService {

    private final ActivoAmortizableRepository activoRepository;
    private final ImputacionActivoFaseRepository imputacionRepository;
    private final EconomicoRepository economicoRepository;
    private final ProyectoRepository proyectoRepository;
    private final FaseProyectoRepository faseProyectoRepository;

    public AmortizacionServiceImpl(ActivoAmortizableRepository activoRepository,
                                   ImputacionActivoFaseRepository imputacionRepository,
                                   EconomicoRepository economicoRepository,
                                   ProyectoRepository proyectoRepository,
                                   FaseProyectoRepository faseProyectoRepository) {
        this.activoRepository = activoRepository;
        this.imputacionRepository = imputacionRepository;
        this.economicoRepository = economicoRepository;
        this.proyectoRepository = proyectoRepository;
        this.faseProyectoRepository = faseProyectoRepository;
    }

    // ==================== ACTIVOS ====================

    @Override
    public List<ActivoAmortizableDTO> listarActivos(Long idEconomico) {
        return activoRepository.findByEconomicoIdEconomico(idEconomico).stream()
                .map(this::toActivoDTO)
                .toList();
    }

    @Override
    public List<ActivoAmortizableDTO> listarActivosPorProyecto(Long idProyecto) {
        return activoRepository.findByProyectoIdProyecto(idProyecto).stream()
                .map(this::toActivoDTO)
                .toList();
    }

    @Override
    public ActivoAmortizableDTO crearActivo(CrearActivoAmortizableDTO dto) {
        var economico = economicoRepository.findById(dto.getIdEconomico())
                .orElseThrow(() -> new IllegalArgumentException("Económico no encontrado: " + dto.getIdEconomico()));

        Proyecto proyecto = null;
        if (dto.getIdProyecto() != null) {
            proyecto = proyectoRepository.findById(dto.getIdProyecto())
                    .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + dto.getIdProyecto()));
        }

        ActivoAmortizable activo = ActivoAmortizable.builder()
                .descripcion(dto.getDescripcion().trim())
                .proveedor(dto.getProveedor().trim())
                .numeroFactura(dto.getNumeroFactura() != null ? dto.getNumeroFactura().trim() : null)
                .valorAdquisicion(dto.getValorAdquisicion())
                .porcentajeAmortizacion(dto.getPorcentajeAmortizacion())
                .porcentajeUsoProyecto(dto.getPorcentajeUsoProyecto())
                .economico(economico)
                .proyecto(proyecto)
                .build();

        activo = activoRepository.save(activo);
        log.info("Activo amortizable creado: {} (económico {})", activo.getDescripcion(), dto.getIdEconomico());
        return toActivoDTO(activo);
    }

    @Override
    public ActivoAmortizableDTO actualizarActivo(ActualizarActivoAmortizableDTO dto) {
        ActivoAmortizable activo = activoRepository.findById(dto.getIdActivo())
                .orElseThrow(() -> new IllegalArgumentException("Activo no encontrado: " + dto.getIdActivo()));

        if (dto.getDescripcion() != null) activo.setDescripcion(dto.getDescripcion().trim());
        if (dto.getProveedor() != null) activo.setProveedor(dto.getProveedor().trim());
        if (dto.getNumeroFactura() != null) activo.setNumeroFactura(dto.getNumeroFactura().trim());
        if (dto.getValorAdquisicion() != null) activo.setValorAdquisicion(dto.getValorAdquisicion());
        if (dto.getPorcentajeAmortizacion() != null) activo.setPorcentajeAmortizacion(dto.getPorcentajeAmortizacion());
        if (dto.getPorcentajeUsoProyecto() != null) activo.setPorcentajeUsoProyecto(dto.getPorcentajeUsoProyecto());

        if (Boolean.TRUE.equals(dto.getClearProyecto())) {
            activo.setProyecto(null);
        } else if (dto.getIdProyecto() != null) {
            Proyecto proyecto = proyectoRepository.findById(dto.getIdProyecto())
                    .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + dto.getIdProyecto()));
            activo.setProyecto(proyecto);
        }

        activo = activoRepository.save(activo);
        return toActivoDTO(activo);
    }

    @Override
    @Transactional
    public void eliminarActivo(Long idActivo) {
        if (!activoRepository.existsById(idActivo)) {
            throw new IllegalArgumentException("Activo no encontrado: " + idActivo);
        }
        activoRepository.deleteById(idActivo);
        log.info("Activo amortizable eliminado: {}", idActivo);
    }

    // ==================== IMPUTACIONES ====================

    @Override
    public List<ImputacionActivoFaseDTO> obtenerImputaciones(Long idActivo) {
        return imputacionRepository.findByActivoIdActivo(idActivo).stream()
                .map(this::toImputacionDTO)
                .toList();
    }

    @Override
    @Transactional
    public void actualizarImputacion(ActualizarImputacionActivoFaseDTO dto) {
        ActivoAmortizable activo = activoRepository.findById(dto.getIdActivo())
                .orElseThrow(() -> new IllegalArgumentException("Activo no encontrado: " + dto.getIdActivo()));

        FaseProyecto fase = faseProyectoRepository.findById(dto.getIdFase())
                .orElseThrow(() -> new IllegalArgumentException("Fase no encontrada: " + dto.getIdFase()));

        var existente = imputacionRepository.findByActivoIdActivoAndFaseIdFase(dto.getIdActivo(), dto.getIdFase());

        if (dto.getImporte() == 0.0 && existente.isPresent()) {
            imputacionRepository.delete(existente.get());
            return;
        }

        ImputacionActivoFase imputacion;
        if (existente.isPresent()) {
            imputacion = existente.get();
            imputacion.setImporte(dto.getImporte());
        } else {
            imputacion = ImputacionActivoFase.builder()
                    .activo(activo)
                    .fase(fase)
                    .importe(dto.getImporte())
                    .build();
        }
        imputacionRepository.save(imputacion);
    }

    // ==================== RESUMEN ====================

    @Override
    public List<ResumenActivoProyectoDTO> calcularResumenPorEconomico(Long idEconomico) {
        List<ActivoAmortizable> activos = activoRepository.findByEconomicoIdEconomico(idEconomico);

        Map<Long, List<ActivoAmortizable>> porProyecto = activos.stream()
                .filter(a -> a.getProyecto() != null)
                .collect(Collectors.groupingBy(a -> a.getProyecto().getIdProyecto()));

        List<ResumenActivoProyectoDTO> resultado = new ArrayList<>();
        for (var entry : porProyecto.entrySet()) {
            Proyecto proyecto = entry.getValue().get(0).getProyecto();
            double totalImputable = entry.getValue().stream()
                    .mapToDouble(this::calcularImporteImputable)
                    .sum();

            List<ResumenActivoFaseDTO> porFase = calcularResumenPorFases(entry.getKey());

            resultado.add(ResumenActivoProyectoDTO.builder()
                    .idProyecto(proyecto.getIdProyecto())
                    .acronimoProyecto(proyecto.getAcronimo())
                    .totalImputable(totalImputable)
                    .porFase(porFase)
                    .build());
        }
        return resultado;
    }

    @Override
    public List<ResumenActivoFaseDTO> calcularResumenPorFases(Long idProyecto) {
        List<ActivoAmortizable> activos = activoRepository.findByProyectoIdProyecto(idProyecto);

        List<ImputacionActivoFase> todasImputaciones = new ArrayList<>();
        for (ActivoAmortizable activo : activos) {
            todasImputaciones.addAll(imputacionRepository.findByActivoIdActivo(activo.getIdActivo()));
        }

        Map<Long, List<ImputacionActivoFase>> porFase = todasImputaciones.stream()
                .collect(Collectors.groupingBy(i -> i.getFase().getIdFase()));

        List<ResumenActivoFaseDTO> resultado = new ArrayList<>();
        for (var entry : porFase.entrySet()) {
            FaseProyecto fase = entry.getValue().get(0).getFase();
            double total = entry.getValue().stream()
                    .mapToDouble(ImputacionActivoFase::getImporte)
                    .sum();

            resultado.add(ResumenActivoFaseDTO.builder()
                    .idFase(fase.getIdFase())
                    .nombreFase(fase.getNombre())
                    .totalImputado(total)
                    .build());
        }
        return resultado;
    }

    // ==================== HELPERS ====================

    private double calcularImporteImputable(ActivoAmortizable activo) {
        double cuotaAmortizacion = activo.getValorAdquisicion() * (activo.getPorcentajeAmortizacion() / 100.0);
        return cuotaAmortizacion * (activo.getPorcentajeUsoProyecto() / 100.0);
    }

    private ActivoAmortizableDTO toActivoDTO(ActivoAmortizable a) {
        double cuotaAmortizacion = a.getValorAdquisicion() * (a.getPorcentajeAmortizacion() / 100.0);
        double importeImputable = cuotaAmortizacion * (a.getPorcentajeUsoProyecto() / 100.0);

        List<ImputacionActivoFaseDTO> imputaciones = imputacionRepository
                .findByActivoIdActivo(a.getIdActivo()).stream()
                .map(this::toImputacionDTO)
                .toList();

        return ActivoAmortizableDTO.builder()
                .idActivo(a.getIdActivo())
                .descripcion(a.getDescripcion())
                .proveedor(a.getProveedor())
                .numeroFactura(a.getNumeroFactura())
                .valorAdquisicion(a.getValorAdquisicion())
                .porcentajeAmortizacion(a.getPorcentajeAmortizacion())
                .porcentajeUsoProyecto(a.getPorcentajeUsoProyecto())
                .cuotaAmortizacion(cuotaAmortizacion)
                .importeImputable(importeImputable)
                .idEconomico(a.getEconomico().getIdEconomico())
                .idProyecto(a.getProyecto() != null ? a.getProyecto().getIdProyecto() : null)
                .acronimoProyecto(a.getProyecto() != null ? a.getProyecto().getAcronimo() : null)
                .imputaciones(imputaciones)
                .build();
    }

    private ImputacionActivoFaseDTO toImputacionDTO(ImputacionActivoFase i) {
        return ImputacionActivoFaseDTO.builder()
                .id(i.getId())
                .idActivo(i.getActivo().getIdActivo())
                .idFase(i.getFase().getIdFase())
                .nombreFase(i.getFase().getNombre())
                .importe(i.getImporte())
                .build();
    }
}
