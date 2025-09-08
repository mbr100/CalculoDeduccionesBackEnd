package com.marioborrego.api.calculodeduccionesbackend.personal.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.ActualizacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.altasEjercicio.ActualizarAltaEjercicioDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.altasEjercicio.AltaEjercicioDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bbcc.ActualizarBbccPersonalDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bbcc.BbccPersonalDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.ActualizarBajaLaboralDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.BajasLaboralesDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.CrearBajaLaboralDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bajasLaborales.ListadoPersonalSelectorEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.ActualizarBonificacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.BonificacionesEmpleadoEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.personal.ListarPersonalEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.personal.PersonalEconomicoDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.resumenCostes.ResumenCostePersonalDTO;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.retribuciones.CamposRetribuciones;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.retribuciones.RetribucionesPersonalDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PersonalService {
    Page<ListarPersonalEconomicoDTO> obtenerTodoPersonalEconomico(Long idEconomico, Pageable pageable);
    PersonalEconomicoDTO crearPersonalEconomico(PersonalEconomicoDTO personalEconomicoDTO);
    void eliminarPersonalEconomico(Long id, Long economico);
    void actualizarPersonalEconomico(PersonalEconomicoDTO personalEconomicoDTO);
    Page<RetribucionesPersonalDTO> obtenerRetribucionesPersonalPorEconomico(Long idEconomico, Pageable pageable);
    Page<BbccPersonalDTO> obtenerCotizacionesPersonalPorEconomico(Long idEconomico, Pageable pageable);
    void actualizarRetribucionPersonal(ActualizacionDTO<Double, CamposRetribuciones> actualizarRetribucionDTO);
    void actualizarBbccPersonal(ActualizarBbccPersonalDTO actualizarBbccPersonalDTO);
    Page<AltaEjercicioDTO> obtenerTodoPersonalAltaEjercicio(Long idEconomico, Pageable pageable);
    void actualizarAltaEjercicio(ActualizarAltaEjercicioDTO actualizarAltaEjercicioDTO);
    Page<BajasLaboralesDTO> obtenerBajasLaboralesPorEconomico(Long idEconomico, Pageable pageable);
    List<ListadoPersonalSelectorEconomicoDTO> obtenerTodoPersonalSelectorEconomico(Long idEconomico);
    void crearBajaLaboral(CrearBajaLaboralDTO bajaLaboralDTO);
    void eliminarBajaLaboral(Long idBajaLaboral);
    void actualizarBajaLaboral(ActualizarBajaLaboralDTO actualizarBajaLaboralDTO);
    Page<BonificacionesEmpleadoEconomicoDTO> obtenerBonificacionesEmpleadoPorEconomico(Long idEconomico, Pageable pageable);
    void actualizarBonificacionEmpleado(ActualizarBonificacionDTO actualizarBonificacionEmpleadoDTO);
    void eliminarBonificacionEmpleado(Long idBonificacion);
    Page<ResumenCostePersonalDTO> obtenerResumenCostePersonal(Long idEconomico, Pageable pageable);
    Page<ResumenCostePersonalDTO> actualizarCosteHoraPersonal(Long idEconomico, Pageable pageable);
}
