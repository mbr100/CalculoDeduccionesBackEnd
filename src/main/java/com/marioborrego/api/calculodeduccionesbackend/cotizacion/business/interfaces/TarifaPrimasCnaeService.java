package com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearTarifaPrimasCnaeDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.TarifaPrimasCnaeDTO;

import java.util.List;

public interface TarifaPrimasCnaeService {

    TarifaPrimasCnaeDTO crear(CrearTarifaPrimasCnaeDTO dto);

    TarifaPrimasCnaeDTO obtenerPorCnaeYAnio(String cnae, Integer anio);

    List<TarifaPrimasCnaeDTO> listarTodos(Integer anio);

    TarifaPrimasCnaeDTO actualizar(Long id, CrearTarifaPrimasCnaeDTO dto);

    void eliminar(Long id);

    boolean existePorCnaeYAnio(String cnae, Integer anio);
}
