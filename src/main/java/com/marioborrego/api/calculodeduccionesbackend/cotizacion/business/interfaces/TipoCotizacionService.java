package com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearTipoCotizacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.TipoCotizacionDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TipoCotizacionService {

    TipoCotizacionDTO crear(CrearTipoCotizacionDTO dto);

    TipoCotizacionDTO obtenerPorCnaeYAnualidad(String cnae, Integer anualidad);

    List<TipoCotizacionDTO> listarTodos(Integer anualidad);

    TipoCotizacionDTO actualizar(Long id, CrearTipoCotizacionDTO dto);

    void eliminar(Long id);

    boolean existePorCnaeYAnualidad(String cnae, Integer anualidad);
}
