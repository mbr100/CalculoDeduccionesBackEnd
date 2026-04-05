package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.presentation.dto.*;

import java.util.List;

public interface ColaboracionesService {

    // Colaboradoras
    List<ColaboradoraDTO> listarColaboradoras(Long idEconomico);
    ColaboradoraDTO crearColaboradora(CrearColaboradoraDTO dto);
    ColaboradoraDTO actualizarColaboradora(ActualizarColaboradoraDTO dto);
    void eliminarColaboradora(Long idColaboradora);

    // Contratos
    List<ContratoColaboracionDTO> listarContratos(Long idEconomico);
    List<ContratoColaboracionDTO> listarContratosPorColaboradora(Long idColaboradora);
    ContratoColaboracionDTO crearContrato(CrearContratoColaboracionDTO dto);
    ContratoColaboracionDTO actualizarContrato(ActualizarContratoColaboracionDTO dto);
    void eliminarContrato(Long idContrato);

    // Facturas
    List<FacturaColaboracionDTO> listarFacturas(Long idEconomico);
    List<FacturaColaboracionDTO> listarFacturasPorColaboradora(Long idColaboradora);
    List<FacturaColaboracionDTO> listarFacturasPorProyecto(Long idProyecto);
    List<FacturaColaboracionDTO> listarFacturasPorContrato(Long idContrato);
    FacturaColaboracionDTO crearFactura(CrearFacturaColaboracionDTO dto);
    FacturaColaboracionDTO actualizarFactura(ActualizarFacturaColaboracionDTO dto);
    void eliminarFactura(Long idFactura);

    // Imputaciones a fase
    List<ImputacionFacturaFaseDTO> obtenerImputacionesPorFactura(Long idFactura);
    void actualizarImputacionFase(ActualizarImputacionFacturaFaseDTO dto);

    // Resumen
    List<ResumenColaboracionesProyectoDTO> calcularResumenPorEconomico(Long idEconomico);
    List<ResumenColaboracionesFaseDTO> calcularResumenPorFases(Long idProyecto);
}
