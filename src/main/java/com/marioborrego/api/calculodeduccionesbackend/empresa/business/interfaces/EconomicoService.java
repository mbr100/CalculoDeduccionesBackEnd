package com.marioborrego.api.calculodeduccionesbackend.empresa.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.empresa.presentation.dto.EconomicoListadoGeneralDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EconomicoService {
    boolean eliminarEconomico(EconomicoListadoGeneralDto economico);
    Page<EconomicoListadoGeneralDto> obtenerEconomicosPaginados(Pageable pageable);
}
