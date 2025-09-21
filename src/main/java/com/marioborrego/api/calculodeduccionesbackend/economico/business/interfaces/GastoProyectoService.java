package com.marioborrego.api.calculodeduccionesbackend.economico.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.GastoProyectoDetalladoDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GastoProyectoService {
    List<GastoProyectoDetalladoDTO> calcularGastosPorEconomico(Long idEconomico);
}
