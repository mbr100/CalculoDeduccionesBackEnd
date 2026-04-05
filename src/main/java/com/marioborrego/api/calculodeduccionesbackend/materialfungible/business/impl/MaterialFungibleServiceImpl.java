package com.marioborrego.api.calculodeduccionesbackend.materialfungible.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.materialfungible.business.interfaces.MaterialFungibleService;
import com.marioborrego.api.calculodeduccionesbackend.materialfungible.domain.models.FacturaMaterial;
import com.marioborrego.api.calculodeduccionesbackend.materialfungible.domain.models.ImputacionMaterialFase;
import com.marioborrego.api.calculodeduccionesbackend.materialfungible.domain.repository.FacturaMaterialRepository;
import com.marioborrego.api.calculodeduccionesbackend.materialfungible.domain.repository.ImputacionMaterialFaseRepository;
import com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.dto.*;
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
public class MaterialFungibleServiceImpl implements MaterialFungibleService {

    private final FacturaMaterialRepository facturaRepository;
    private final ImputacionMaterialFaseRepository imputacionRepository;
    private final EconomicoRepository economicoRepository;
    private final ProyectoRepository proyectoRepository;
    private final FaseProyectoRepository faseProyectoRepository;

    public MaterialFungibleServiceImpl(FacturaMaterialRepository facturaRepository,
                                       ImputacionMaterialFaseRepository imputacionRepository,
                                       EconomicoRepository economicoRepository,
                                       ProyectoRepository proyectoRepository,
                                       FaseProyectoRepository faseProyectoRepository) {
        this.facturaRepository = facturaRepository;
        this.imputacionRepository = imputacionRepository;
        this.economicoRepository = economicoRepository;
        this.proyectoRepository = proyectoRepository;
        this.faseProyectoRepository = faseProyectoRepository;
    }

    // ==================== FACTURAS ====================

    @Override
    public List<FacturaMaterialDTO> listarFacturas(Long idEconomico) {
        return facturaRepository.findByEconomicoIdEconomico(idEconomico).stream()
                .map(this::toFacturaDTO)
                .toList();
    }

    @Override
    public List<FacturaMaterialDTO> listarFacturasPorProyecto(Long idProyecto) {
        return facturaRepository.findByProyectoIdProyecto(idProyecto).stream()
                .map(this::toFacturaDTO)
                .toList();
    }

    @Override
    public FacturaMaterialDTO crearFactura(CrearFacturaMaterialDTO dto) {
        var economico = economicoRepository.findById(dto.getIdEconomico())
                .orElseThrow(() -> new IllegalArgumentException("Económico no encontrado: " + dto.getIdEconomico()));

        Proyecto proyecto = null;
        if (dto.getIdProyecto() != null) {
            proyecto = proyectoRepository.findById(dto.getIdProyecto())
                    .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + dto.getIdProyecto()));
        }

        double porcentajeProrrata = dto.getPorcentajeProrrata() != null ? dto.getPorcentajeProrrata() : 0.0;
        double porcentajeValidez = resolverPorcentajeValidez(dto.getValidez(), dto.getPorcentajeValidez());

        FacturaMaterial factura = FacturaMaterial.builder()
                .numeroFactura(dto.getNumeroFactura().trim())
                .proveedor(dto.getProveedor().trim())
                .descripcion(dto.getDescripcion())
                .baseImponible(dto.getBaseImponible())
                .iva(dto.getIva())
                .porcentajeProrrata(porcentajeProrrata)
                .validez(dto.getValidez())
                .porcentajeValidez(porcentajeValidez)
                .economico(economico)
                .proyecto(proyecto)
                .build();

        factura = facturaRepository.save(factura);
        log.info("Factura material creada: {} (economico {})", factura.getNumeroFactura(), dto.getIdEconomico());
        return toFacturaDTO(factura);
    }

    @Override
    public FacturaMaterialDTO actualizarFactura(ActualizarFacturaMaterialDTO dto) {
        FacturaMaterial factura = facturaRepository.findById(dto.getIdFactura())
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + dto.getIdFactura()));

        if (dto.getNumeroFactura() != null) factura.setNumeroFactura(dto.getNumeroFactura().trim());
        if (dto.getProveedor() != null) factura.setProveedor(dto.getProveedor().trim());
        if (dto.getDescripcion() != null) factura.setDescripcion(dto.getDescripcion());
        if (dto.getBaseImponible() != null) factura.setBaseImponible(dto.getBaseImponible());
        if (dto.getIva() != null) factura.setIva(dto.getIva());
        if (dto.getPorcentajeProrrata() != null) factura.setPorcentajeProrrata(dto.getPorcentajeProrrata());

        if (dto.getValidez() != null) {
            factura.setValidez(dto.getValidez());
            factura.setPorcentajeValidez(resolverPorcentajeValidez(dto.getValidez(), dto.getPorcentajeValidez()));
        } else if (dto.getPorcentajeValidez() != null) {
            factura.setPorcentajeValidez(dto.getPorcentajeValidez());
        }

        if (Boolean.TRUE.equals(dto.getClearProyecto())) {
            factura.setProyecto(null);
        } else if (dto.getIdProyecto() != null) {
            Proyecto proyecto = proyectoRepository.findById(dto.getIdProyecto())
                    .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + dto.getIdProyecto()));
            factura.setProyecto(proyecto);
        }

        factura = facturaRepository.save(factura);
        return toFacturaDTO(factura);
    }

    @Override
    @Transactional
    public void eliminarFactura(Long idFactura) {
        if (!facturaRepository.existsById(idFactura)) {
            throw new IllegalArgumentException("Factura no encontrada: " + idFactura);
        }
        facturaRepository.deleteById(idFactura);
        log.info("Factura material eliminada: {}", idFactura);
    }

    // ==================== IMPUTACIONES ====================

    @Override
    public List<ImputacionMaterialFaseDTO> obtenerImputaciones(Long idFactura) {
        return imputacionRepository.findByFacturaIdFactura(idFactura).stream()
                .map(this::toImputacionDTO)
                .toList();
    }

    @Override
    @Transactional
    public void actualizarImputacion(ActualizarImputacionMaterialFaseDTO dto) {
        FacturaMaterial factura = facturaRepository.findById(dto.getIdFactura())
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + dto.getIdFactura()));

        FaseProyecto fase = faseProyectoRepository.findById(dto.getIdFase())
                .orElseThrow(() -> new IllegalArgumentException("Fase no encontrada: " + dto.getIdFase()));

        var existente = imputacionRepository.findByFacturaIdFacturaAndFaseIdFase(dto.getIdFactura(), dto.getIdFase());

        if (dto.getImporte() == 0.0 && existente.isPresent()) {
            imputacionRepository.delete(existente.get());
            return;
        }

        ImputacionMaterialFase imputacion;
        if (existente.isPresent()) {
            imputacion = existente.get();
            imputacion.setImporte(dto.getImporte());
        } else {
            imputacion = ImputacionMaterialFase.builder()
                    .factura(factura)
                    .fase(fase)
                    .importe(dto.getImporte())
                    .build();
        }
        imputacionRepository.save(imputacion);
    }

    // ==================== RESUMEN ====================

    @Override
    public List<ResumenMaterialProyectoDTO> calcularResumenPorEconomico(Long idEconomico) {
        List<FacturaMaterial> facturas = facturaRepository.findByEconomicoIdEconomico(idEconomico);

        Map<Long, List<FacturaMaterial>> porProyecto = facturas.stream()
                .filter(f -> f.getProyecto() != null)
                .collect(Collectors.groupingBy(f -> f.getProyecto().getIdProyecto()));

        List<ResumenMaterialProyectoDTO> resultado = new ArrayList<>();
        for (var entry : porProyecto.entrySet()) {
            Proyecto proyecto = entry.getValue().get(0).getProyecto();
            double totalImputable = entry.getValue().stream()
                    .mapToDouble(this::calcularImporteImputable)
                    .sum();

            List<ResumenMaterialFaseDTO> porFase = calcularResumenPorFases(entry.getKey());

            resultado.add(ResumenMaterialProyectoDTO.builder()
                    .idProyecto(proyecto.getIdProyecto())
                    .acronimoProyecto(proyecto.getAcronimo())
                    .totalImputable(totalImputable)
                    .porFase(porFase)
                    .build());
        }
        return resultado;
    }

    @Override
    public List<ResumenMaterialFaseDTO> calcularResumenPorFases(Long idProyecto) {
        List<FacturaMaterial> facturas = facturaRepository.findByProyectoIdProyecto(idProyecto);

        List<ImputacionMaterialFase> todasImputaciones = new ArrayList<>();
        for (FacturaMaterial factura : facturas) {
            todasImputaciones.addAll(imputacionRepository.findByFacturaIdFactura(factura.getIdFactura()));
        }

        Map<Long, List<ImputacionMaterialFase>> porFase = todasImputaciones.stream()
                .collect(Collectors.groupingBy(i -> i.getFase().getIdFase()));

        List<ResumenMaterialFaseDTO> resultado = new ArrayList<>();
        for (var entry : porFase.entrySet()) {
            FaseProyecto fase = entry.getValue().get(0).getFase();
            double total = entry.getValue().stream()
                    .mapToDouble(ImputacionMaterialFase::getImporte)
                    .sum();

            resultado.add(ResumenMaterialFaseDTO.builder()
                    .idFase(fase.getIdFase())
                    .nombreFase(fase.getNombre())
                    .totalImputado(total)
                    .build());
        }
        return resultado;
    }

    // ==================== HELPERS ====================

    private double resolverPorcentajeValidez(ValidezIDI validez, Double porcentajeCustom) {
        return switch (validez) {
            case VALIDA_IDI -> 100.0;
            case NO_VALIDA -> 0.0;
            case VALIDA_PARCIAL -> {
                if (porcentajeCustom == null || porcentajeCustom < 1 || porcentajeCustom > 99) {
                    throw new IllegalArgumentException("Para validez parcial, el porcentaje debe estar entre 1 y 99");
                }
                yield porcentajeCustom;
            }
        };
    }

    private double calcularImporteFinal(FacturaMaterial factura) {
        return factura.getBaseImponible() + (factura.getIva() * factura.getPorcentajeProrrata() / 100.0);
    }

    private double calcularImporteImputable(FacturaMaterial factura) {
        return calcularImporteFinal(factura) * (factura.getPorcentajeValidez() / 100.0);
    }

    private FacturaMaterialDTO toFacturaDTO(FacturaMaterial f) {
        double importeFinal = calcularImporteFinal(f);
        double importeImputable = importeFinal * (f.getPorcentajeValidez() / 100.0);

        List<ImputacionMaterialFaseDTO> imputaciones = imputacionRepository
                .findByFacturaIdFactura(f.getIdFactura()).stream()
                .map(this::toImputacionDTO)
                .toList();

        return FacturaMaterialDTO.builder()
                .idFactura(f.getIdFactura())
                .numeroFactura(f.getNumeroFactura())
                .proveedor(f.getProveedor())
                .descripcion(f.getDescripcion())
                .baseImponible(f.getBaseImponible())
                .iva(f.getIva())
                .porcentajeProrrata(f.getPorcentajeProrrata())
                .validez(f.getValidez())
                .porcentajeValidez(f.getPorcentajeValidez())
                .importeFinal(importeFinal)
                .importeImputable(importeImputable)
                .idEconomico(f.getEconomico().getIdEconomico())
                .idProyecto(f.getProyecto() != null ? f.getProyecto().getIdProyecto() : null)
                .acronimoProyecto(f.getProyecto() != null ? f.getProyecto().getAcronimo() : null)
                .imputaciones(imputaciones)
                .build();
    }

    private ImputacionMaterialFaseDTO toImputacionDTO(ImputacionMaterialFase i) {
        return ImputacionMaterialFaseDTO.builder()
                .id(i.getId())
                .idFactura(i.getFactura().getIdFactura())
                .idFase(i.getFase().getIdFase())
                .nombreFase(i.getFase().getNombre())
                .importe(i.getImporte())
                .build();
    }
}
