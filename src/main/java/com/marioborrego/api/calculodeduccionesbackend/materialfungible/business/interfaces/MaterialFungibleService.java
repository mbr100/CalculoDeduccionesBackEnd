package com.marioborrego.api.calculodeduccionesbackend.materialfungible.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.materialfungible.presentation.dto.*;

import java.util.List;

public interface MaterialFungibleService {

    // Facturas
    List<FacturaMaterialDTO> listarFacturas(Long idEconomico);
    List<FacturaMaterialDTO> listarFacturasPorProyecto(Long idProyecto);
    FacturaMaterialDTO crearFactura(CrearFacturaMaterialDTO dto);
    FacturaMaterialDTO actualizarFactura(ActualizarFacturaMaterialDTO dto);
    void eliminarFactura(Long idFactura);

    // Imputaciones
    List<ImputacionMaterialFaseDTO> obtenerImputaciones(Long idFactura);
    void actualizarImputacion(ActualizarImputacionMaterialFaseDTO dto);

    // Resumen
    List<ResumenMaterialProyectoDTO> calcularResumenPorEconomico(Long idEconomico);
    List<ResumenMaterialFaseDTO> calcularResumenPorFases(Long idProyecto);
}
