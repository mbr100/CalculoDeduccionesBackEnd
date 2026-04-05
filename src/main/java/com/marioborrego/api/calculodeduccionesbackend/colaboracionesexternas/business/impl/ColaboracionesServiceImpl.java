package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.business.interfaces.ColaboracionesService;
import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.*;
import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.enums.ValidezIDI;
import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.repository.*;
import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto.*;
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
public class ColaboracionesServiceImpl implements ColaboracionesService {

    private final ColaboradoraRepository colaboradoraRepository;
    private final ContratoColaboracionRepository contratoRepository;
    private final FacturaColaboracionRepository facturaRepository;
    private final ImputacionFacturaFaseRepository imputacionRepository;
    private final EconomicoRepository economicoRepository;
    private final ProyectoRepository proyectoRepository;
    private final FaseProyectoRepository faseProyectoRepository;

    public ColaboracionesServiceImpl(ColaboradoraRepository colaboradoraRepository,
                                     ContratoColaboracionRepository contratoRepository,
                                     FacturaColaboracionRepository facturaRepository,
                                     ImputacionFacturaFaseRepository imputacionRepository,
                                     EconomicoRepository economicoRepository,
                                     ProyectoRepository proyectoRepository,
                                     FaseProyectoRepository faseProyectoRepository) {
        this.colaboradoraRepository = colaboradoraRepository;
        this.contratoRepository = contratoRepository;
        this.facturaRepository = facturaRepository;
        this.imputacionRepository = imputacionRepository;
        this.economicoRepository = economicoRepository;
        this.proyectoRepository = proyectoRepository;
        this.faseProyectoRepository = faseProyectoRepository;
    }

    // ==================== COLABORADORAS ====================

    @Override
    public List<ColaboradoraDTO> listarColaboradoras(Long idEconomico) {
        return colaboradoraRepository.findByEconomicoIdEconomico(idEconomico).stream()
                .map(this::toColaboradoraDTO)
                .toList();
    }

    @Override
    public ColaboradoraDTO crearColaboradora(CrearColaboradoraDTO dto) {
        var economico = economicoRepository.findById(dto.getIdEconomico())
                .orElseThrow(() -> new IllegalArgumentException("Económico no encontrado: " + dto.getIdEconomico()));

        Colaboradora colaboradora = Colaboradora.builder()
                .cif(dto.getCif().trim().toUpperCase())
                .nombre(dto.getNombre().trim())
                .economico(economico)
                .build();
        colaboradora = colaboradoraRepository.save(colaboradora);
        log.info("Colaboradora creada: {} - {}", colaboradora.getCif(), colaboradora.getNombre());
        return toColaboradoraDTO(colaboradora);
    }

    @Override
    public ColaboradoraDTO actualizarColaboradora(ActualizarColaboradoraDTO dto) {
        Colaboradora colaboradora = colaboradoraRepository.findById(dto.getIdColaboradora())
                .orElseThrow(() -> new IllegalArgumentException("Colaboradora no encontrada: " + dto.getIdColaboradora()));

        if (dto.getCif() != null) colaboradora.setCif(dto.getCif().trim().toUpperCase());
        if (dto.getNombre() != null) colaboradora.setNombre(dto.getNombre().trim());

        colaboradora = colaboradoraRepository.save(colaboradora);
        return toColaboradoraDTO(colaboradora);
    }

    @Override
    @Transactional
    public void eliminarColaboradora(Long idColaboradora) {
        if (!colaboradoraRepository.existsById(idColaboradora)) {
            throw new IllegalArgumentException("Colaboradora no encontrada: " + idColaboradora);
        }
        colaboradoraRepository.deleteById(idColaboradora);
        log.info("Colaboradora eliminada: {}", idColaboradora);
    }

    // ==================== CONTRATOS ====================

    @Override
    public List<ContratoColaboracionDTO> listarContratos(Long idEconomico) {
        return contratoRepository.findByColaboradoraEconomicoIdEconomico(idEconomico).stream()
                .map(this::toContratoDTO)
                .toList();
    }

    @Override
    public List<ContratoColaboracionDTO> listarContratosPorColaboradora(Long idColaboradora) {
        return contratoRepository.findByColaboradoraIdColaboradora(idColaboradora).stream()
                .map(this::toContratoDTO)
                .toList();
    }

    @Override
    public ContratoColaboracionDTO crearContrato(CrearContratoColaboracionDTO dto) {
        Colaboradora colaboradora = colaboradoraRepository.findById(dto.getIdColaboradora())
                .orElseThrow(() -> new IllegalArgumentException("Colaboradora no encontrada: " + dto.getIdColaboradora()));

        ContratoColaboracion contrato = ContratoColaboracion.builder()
                .nombreContrato(dto.getNombreContrato().trim())
                .objeto(dto.getObjeto())
                .tipoContrato(dto.getTipoContrato())
                .validez(dto.getValidez())
                .importeCubierto(dto.getImporteCubierto())
                .colaboradora(colaboradora)
                .build();
        contrato = contratoRepository.save(contrato);
        log.info("Contrato creado: {} para colaboradora {}", contrato.getNombreContrato(), colaboradora.getNombre());
        return toContratoDTO(contrato);
    }

    @Override
    public ContratoColaboracionDTO actualizarContrato(ActualizarContratoColaboracionDTO dto) {
        ContratoColaboracion contrato = contratoRepository.findById(dto.getIdContrato())
                .orElseThrow(() -> new IllegalArgumentException("Contrato no encontrado: " + dto.getIdContrato()));

        if (dto.getNombreContrato() != null) contrato.setNombreContrato(dto.getNombreContrato().trim());
        if (dto.getObjeto() != null) contrato.setObjeto(dto.getObjeto());
        if (dto.getTipoContrato() != null) contrato.setTipoContrato(dto.getTipoContrato());
        if (dto.getValidez() != null) contrato.setValidez(dto.getValidez());
        if (dto.getImporteCubierto() != null) contrato.setImporteCubierto(dto.getImporteCubierto());

        contrato = contratoRepository.save(contrato);
        return toContratoDTO(contrato);
    }

    @Override
    @Transactional
    public void eliminarContrato(Long idContrato) {
        ContratoColaboracion contrato = contratoRepository.findById(idContrato)
                .orElseThrow(() -> new IllegalArgumentException("Contrato no encontrado: " + idContrato));

        // Desvincular facturas asociadas (no eliminarlas)
        List<FacturaColaboracion> facturas = facturaRepository.findByContratoIdContrato(idContrato);
        for (FacturaColaboracion factura : facturas) {
            factura.setContrato(null);
            facturaRepository.save(factura);
        }

        contratoRepository.delete(contrato);
        log.info("Contrato eliminado: {}", idContrato);
    }

    // ==================== FACTURAS ====================

    @Override
    public List<FacturaColaboracionDTO> listarFacturas(Long idEconomico) {
        return facturaRepository.findByColaboradoraEconomicoIdEconomico(idEconomico).stream()
                .map(this::toFacturaDTO)
                .toList();
    }

    @Override
    public List<FacturaColaboracionDTO> listarFacturasPorColaboradora(Long idColaboradora) {
        return facturaRepository.findByColaboradoraIdColaboradora(idColaboradora).stream()
                .map(this::toFacturaDTO)
                .toList();
    }

    @Override
    public List<FacturaColaboracionDTO> listarFacturasPorProyecto(Long idProyecto) {
        return facturaRepository.findByProyectoIdProyecto(idProyecto).stream()
                .map(this::toFacturaDTO)
                .toList();
    }

    @Override
    public List<FacturaColaboracionDTO> listarFacturasPorContrato(Long idContrato) {
        return facturaRepository.findByContratoIdContrato(idContrato).stream()
                .map(this::toFacturaDTO)
                .toList();
    }

    @Override
    public FacturaColaboracionDTO crearFactura(CrearFacturaColaboracionDTO dto) {
        Colaboradora colaboradora = colaboradoraRepository.findById(dto.getIdColaboradora())
                .orElseThrow(() -> new IllegalArgumentException("Colaboradora no encontrada: " + dto.getIdColaboradora()));

        ContratoColaboracion contrato = null;
        if (dto.getIdContrato() != null) {
            contrato = contratoRepository.findById(dto.getIdContrato())
                    .orElseThrow(() -> new IllegalArgumentException("Contrato no encontrado: " + dto.getIdContrato()));
        }

        Proyecto proyecto = null;
        if (dto.getIdProyecto() != null) {
            proyecto = proyectoRepository.findById(dto.getIdProyecto())
                    .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado: " + dto.getIdProyecto()));
        }

        Double porcentajeProrrata = dto.getPorcentajeProrrata() != null ? dto.getPorcentajeProrrata() : 0.0;
        Double porcentajeValidez = resolverPorcentajeValidez(dto.getValidez(), dto.getPorcentajeValidez());

        FacturaColaboracion factura = FacturaColaboracion.builder()
                .numeroFactura(dto.getNumeroFactura().trim())
                .conceptos(dto.getConceptos())
                .importe(dto.getImporte())
                .baseImponible(dto.getBaseImponible())
                .iva(dto.getIva())
                .porcentajeProrrata(porcentajeProrrata)
                .validez(dto.getValidez())
                .porcentajeValidez(porcentajeValidez)
                .colaboradora(colaboradora)
                .contrato(contrato)
                .proyecto(proyecto)
                .build();
        factura = facturaRepository.save(factura);
        log.info("Factura creada: {} para colaboradora {}", factura.getNumeroFactura(), colaboradora.getNombre());
        return toFacturaDTO(factura);
    }

    @Override
    public FacturaColaboracionDTO actualizarFactura(ActualizarFacturaColaboracionDTO dto) {
        FacturaColaboracion factura = facturaRepository.findById(dto.getIdFactura())
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + dto.getIdFactura()));

        if (dto.getNumeroFactura() != null) factura.setNumeroFactura(dto.getNumeroFactura().trim());
        if (dto.getConceptos() != null) factura.setConceptos(dto.getConceptos());
        if (dto.getImporte() != null) factura.setImporte(dto.getImporte());
        if (dto.getBaseImponible() != null) factura.setBaseImponible(dto.getBaseImponible());
        if (dto.getIva() != null) factura.setIva(dto.getIva());
        if (dto.getPorcentajeProrrata() != null) factura.setPorcentajeProrrata(dto.getPorcentajeProrrata());

        if (dto.getValidez() != null) {
            factura.setValidez(dto.getValidez());
            factura.setPorcentajeValidez(resolverPorcentajeValidez(dto.getValidez(), dto.getPorcentajeValidez()));
        } else if (dto.getPorcentajeValidez() != null) {
            factura.setPorcentajeValidez(dto.getPorcentajeValidez());
        }

        if (dto.getIdContrato() != null) {
            ContratoColaboracion contrato = contratoRepository.findById(dto.getIdContrato())
                    .orElseThrow(() -> new IllegalArgumentException("Contrato no encontrado: " + dto.getIdContrato()));
            factura.setContrato(contrato);
        }

        if (dto.getIdProyecto() != null) {
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
        log.info("Factura eliminada: {}", idFactura);
    }

    // ==================== IMPUTACIONES A FASE ====================

    @Override
    public List<ImputacionFacturaFaseDTO> obtenerImputacionesPorFactura(Long idFactura) {
        return imputacionRepository.findByFacturaIdFactura(idFactura).stream()
                .map(this::toImputacionDTO)
                .toList();
    }

    @Override
    @Transactional
    public void actualizarImputacionFase(ActualizarImputacionFacturaFaseDTO dto) {
        FacturaColaboracion factura = facturaRepository.findById(dto.getIdFactura())
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada: " + dto.getIdFactura()));

        FaseProyecto fase = faseProyectoRepository.findById(dto.getIdFase())
                .orElseThrow(() -> new IllegalArgumentException("Fase no encontrada: " + dto.getIdFase()));

        var existente = imputacionRepository.findByFacturaIdFacturaAndFaseIdFase(dto.getIdFactura(), dto.getIdFase());

        if (dto.getImporte() == 0.0 && existente.isPresent()) {
            imputacionRepository.delete(existente.get());
            return;
        }

        ImputacionFacturaFase imputacion;
        if (existente.isPresent()) {
            imputacion = existente.get();
            imputacion.setImporte(dto.getImporte());
        } else {
            imputacion = ImputacionFacturaFase.builder()
                    .factura(factura)
                    .fase(fase)
                    .importe(dto.getImporte())
                    .build();
        }
        imputacionRepository.save(imputacion);
    }

    // ==================== RESUMEN ====================

    @Override
    public List<ResumenColaboracionesProyectoDTO> calcularResumenPorEconomico(Long idEconomico) {
        List<FacturaColaboracion> facturas = facturaRepository.findByColaboradoraEconomicoIdEconomico(idEconomico);

        Map<Long, List<FacturaColaboracion>> porProyecto = facturas.stream()
                .filter(f -> f.getProyecto() != null)
                .collect(Collectors.groupingBy(f -> f.getProyecto().getIdProyecto()));

        List<ResumenColaboracionesProyectoDTO> resultado = new ArrayList<>();
        for (var entry : porProyecto.entrySet()) {
            Proyecto proyecto = entry.getValue().get(0).getProyecto();
            double totalImputable = entry.getValue().stream()
                    .mapToDouble(this::calcularImporteImputable)
                    .sum();

            List<ResumenColaboracionesFaseDTO> porFase = calcularResumenPorFases(entry.getKey());

            resultado.add(ResumenColaboracionesProyectoDTO.builder()
                    .idProyecto(proyecto.getIdProyecto())
                    .acronimoProyecto(proyecto.getAcronimo())
                    .totalFacturadoImputable(totalImputable)
                    .porFase(porFase)
                    .build());
        }
        return resultado;
    }

    @Override
    public List<ResumenColaboracionesFaseDTO> calcularResumenPorFases(Long idProyecto) {
        List<FacturaColaboracion> facturas = facturaRepository.findByProyectoIdProyecto(idProyecto);

        List<ImputacionFacturaFase> todasImputaciones = new ArrayList<>();
        for (FacturaColaboracion factura : facturas) {
            todasImputaciones.addAll(imputacionRepository.findByFacturaIdFactura(factura.getIdFactura()));
        }

        Map<Long, List<ImputacionFacturaFase>> porFase = todasImputaciones.stream()
                .collect(Collectors.groupingBy(i -> i.getFase().getIdFase()));

        List<ResumenColaboracionesFaseDTO> resultado = new ArrayList<>();
        for (var entry : porFase.entrySet()) {
            FaseProyecto fase = entry.getValue().get(0).getFase();
            double total = entry.getValue().stream()
                    .mapToDouble(ImputacionFacturaFase::getImporte)
                    .sum();

            resultado.add(ResumenColaboracionesFaseDTO.builder()
                    .idFase(fase.getIdFase())
                    .nombreFase(fase.getNombre())
                    .totalImputado(total)
                    .build());
        }
        return resultado;
    }

    // ==================== HELPERS ====================

    private Double resolverPorcentajeValidez(ValidezIDI validez, Double porcentajeCustom) {
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

    private double calcularImporteFinal(FacturaColaboracion factura) {
        return factura.getBaseImponible() + (factura.getIva() * factura.getPorcentajeProrrata() / 100.0);
    }

    private double calcularImporteImputable(FacturaColaboracion factura) {
        return calcularImporteFinal(factura) * (factura.getPorcentajeValidez() / 100.0);
    }

    private double calcularTotalFacturadoContrato(Long idContrato) {
        return facturaRepository.findByContratoIdContrato(idContrato).stream()
                .mapToDouble(this::calcularImporteFinal)
                .sum();
    }

    private ColaboradoraDTO toColaboradoraDTO(Colaboradora c) {
        return ColaboradoraDTO.builder()
                .idColaboradora(c.getIdColaboradora())
                .cif(c.getCif())
                .nombre(c.getNombre())
                .build();
    }

    private ContratoColaboracionDTO toContratoDTO(ContratoColaboracion c) {
        double totalFacturado = calcularTotalFacturadoContrato(c.getIdContrato());
        return ContratoColaboracionDTO.builder()
                .idContrato(c.getIdContrato())
                .nombreContrato(c.getNombreContrato())
                .objeto(c.getObjeto())
                .tipoContrato(c.getTipoContrato())
                .validez(c.getValidez())
                .importeCubierto(c.getImporteCubierto())
                .idColaboradora(c.getColaboradora().getIdColaboradora())
                .nombreColaboradora(c.getColaboradora().getNombre())
                .totalFacturado(totalFacturado)
                .superaCobertura(totalFacturado > c.getImporteCubierto())
                .build();
    }

    private FacturaColaboracionDTO toFacturaDTO(FacturaColaboracion f) {
        double importeFinal = calcularImporteFinal(f);
        double importeImputable = importeFinal * (f.getPorcentajeValidez() / 100.0);

        List<ImputacionFacturaFaseDTO> imputaciones = imputacionRepository
                .findByFacturaIdFactura(f.getIdFactura()).stream()
                .map(this::toImputacionDTO)
                .toList();

        return FacturaColaboracionDTO.builder()
                .idFactura(f.getIdFactura())
                .numeroFactura(f.getNumeroFactura())
                .conceptos(f.getConceptos())
                .importe(f.getImporte())
                .baseImponible(f.getBaseImponible())
                .iva(f.getIva())
                .porcentajeProrrata(f.getPorcentajeProrrata())
                .validez(f.getValidez())
                .porcentajeValidez(f.getPorcentajeValidez())
                .importeFinal(importeFinal)
                .importeImputable(importeImputable)
                .idColaboradora(f.getColaboradora().getIdColaboradora())
                .nombreColaboradora(f.getColaboradora().getNombre())
                .idContrato(f.getContrato() != null ? f.getContrato().getIdContrato() : null)
                .nombreContrato(f.getContrato() != null ? f.getContrato().getNombreContrato() : null)
                .idProyecto(f.getProyecto() != null ? f.getProyecto().getIdProyecto() : null)
                .acronimoProyecto(f.getProyecto() != null ? f.getProyecto().getAcronimo() : null)
                .imputacionesFase(imputaciones)
                .build();
    }

    private ImputacionFacturaFaseDTO toImputacionDTO(ImputacionFacturaFase i) {
        return ImputacionFacturaFaseDTO.builder()
                .id(i.getId())
                .idFactura(i.getFactura().getIdFactura())
                .idFase(i.getFase().getIdFase())
                .nombreFase(i.getFase().getNombre())
                .importe(i.getImporte())
                .build();
    }
}
