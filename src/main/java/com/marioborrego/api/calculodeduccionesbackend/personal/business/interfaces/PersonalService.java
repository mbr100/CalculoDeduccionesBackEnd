package com.marioborrego.api.calculodeduccionesbackend.personal.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface PersonalService {
    Page<ListarPersonalEconomicoDTO> obtenerTodoPersonalEconomico(Long idEconomico, Pageable pageable);
    PersonalEconomicoDTO crearPersonalEconomico(PersonalEconomicoDTO personalEconomicoDTO);
    void eliminarPersonalEconomico(int id, Long economico);
    void actualizarPersonalEconomico(PersonalEconomicoDTO personalEconomicoDTO);
    Page<RetribucionesPersonalDTO> obtenerRetribucionesPersonalPorEconomico(Long idEconomico);
    Page<BbccPersonalDTO> obtenerCotizacionesPersonalPorEconomico(Long idEconomico);
    void actualizarRetribucionPersonal(ActualizarRetribucionDTO actualizarRetribucionDTO);
    void actualizarBbccPersonal(ActualizarBbccPersonalDTO actualizarBbccPersonalDTO);
}
