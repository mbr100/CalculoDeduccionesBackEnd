package com.marioborrego.api.calculodeduccionesbackend.economico.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface EconomicoService {
    boolean eliminarEconomico(EconomicoListadoGeneralDto economico);
    Page<EconomicoListadoGeneralDto> obtenerEconomicosPaginados(Pageable pageable);
    boolean comprobarExistenciaEconomico(@NotBlank(message = "El CIF es obligatorio") @Size(min = 9, max = 9, message = "El CIF debe tener 9 caracteres") String cif, @NotNull(message = "La anualidad es obligatoria") Long anualidad);
    EconomicoCreadoDTO crearEconomico(CrearEconomicoDTO crearEconomicoDTO);
    EconomicoDTO obtenerEconomico(Long idEconomico);
    void actualizarDatosEconomico(ActualizarDatosEconomicoDTO economico);
}
