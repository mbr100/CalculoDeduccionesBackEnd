package com.marioborrego.api.calculodeduccionesbackend.economico.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.economico.business.interfaces.EconomicoService;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.*;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.exceptions.EconomicoNoEncontrado;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.exceptions.NewEconomicoException;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BajaLaboral;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.PeriodoContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EconomicoServiceImpl implements EconomicoService {

    private final EconomicoRepository economicoRepository;
    private static final String ECONOMICO_NOT_FOUND_MESSAGE = "Económico no encontrado con ID: ";


    public EconomicoServiceImpl(EconomicoRepository economicoRepository) {
        this.economicoRepository = economicoRepository;
    }

    @Override
    public boolean eliminarEconomico(EconomicoListadoGeneralDto economico) {
        try {
            Economico economicoEntity = economicoRepository.findById(economico.getId())
                    .orElseThrow(() -> new EconomicoNoEncontrado(ECONOMICO_NOT_FOUND_MESSAGE+ economico.getId()));
            economicoEntity.setActivo(false);
            economicoRepository.save(economicoEntity);
            return true;
        } catch (Exception e) {
            throw new NewEconomicoException("Error al eliminar el económico" + e.getMessage());
        }
    }

    public Page<EconomicoListadoGeneralDto> obtenerEconomicosPaginados(Pageable pageable) {
        try {
            Page<Economico> economicos = economicoRepository.findAllActivosPaginado(pageable);
            return economicos.map(empresa -> EconomicoListadoGeneralDto.builder()
                    .id(empresa.getIdEconomico())
                    .nombre(empresa.getNombre())
                    .cif(empresa.getCif())
                    .CNAE(Math.toIntExact(empresa.getCNAE()))
                    .anualidad(Math.toIntExact(empresa.getAnualidad()))
                    .esPyme(empresa.isEsPyme())
                    .build()
            );
        } catch (Exception e) {
            throw new InternalError("Error al obtener el listado de económicos", e);
        }
    }

    @Override
    public boolean comprobarExistenciaEconomico(String cif, Long anualidad) {
        try {
            List<Economico> economicos = economicoRepository.findByCifAndAnualidadAndActivoTrue(cif, anualidad);
            return !economicos.isEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Error al comprobar la existencia del económico", e);
        }
    }

    @Override
    public EconomicoCreadoDTO crearEconomico(CrearEconomicoDTO crearEconomicoDTO) {
        try {
            Economico economico =Economico.builder()
                    .nombre(crearEconomicoDTO.getNombre())
                    .cif(crearEconomicoDTO.getCif())
                    .direccion(crearEconomicoDTO.getDireccion())
                    .telefono(crearEconomicoDTO.getTelefono())
                    .nombreContacto(crearEconomicoDTO.getNombreContacto())
                    .emailContacto(crearEconomicoDTO.getEmailContacto())
                    .horasConvenio(Long.valueOf(crearEconomicoDTO.getHorasConvenio()))
                    .horasConvenio(Long.valueOf(crearEconomicoDTO.getHorasConvenio()))
                    .urllogo(crearEconomicoDTO.getUrllogo())
                    .urlWeb(crearEconomicoDTO.getUrlWeb())
                    .CNAE(Long.valueOf(crearEconomicoDTO.getCnae()))
                    .anualidad(crearEconomicoDTO.getAnualidad())
                    .esPyme(crearEconomicoDTO.getEsPyme())
                    .selloPymeInnovadora(Boolean.TRUE.equals(crearEconomicoDTO.getSelloPymeInnovadora()))
                    .activo(true)
                    .build();
            Economico e =economicoRepository.save(economico);
            return EconomicoCreadoDTO.builder()
                    .id(e.getIdEconomico())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el económico", e);
        }
    }

    @Override
    public EconomicoDTO obtenerEconomico(Long idEconomico) {
        try {
            Economico economico = economicoRepository.findById(idEconomico).orElseThrow(() -> new EconomicoNoEncontrado(ECONOMICO_NOT_FOUND_MESSAGE + idEconomico));
            return EconomicoDTO.builder()
                    .id(economico.getIdEconomico())
                    .nombre(economico.getNombre())
                    .cif(economico.getCif())
                    .direccion(economico.getDireccion())
                    .telefono(economico.getTelefono())
                    .nombreContacto(economico.getNombreContacto())
                    .emailContacto(economico.getEmailContacto())
                    .horasConvenio(economico.getHorasConvenio())
                    .urllogo(economico.getUrllogo())
                    .urlWeb(economico.getUrlWeb())
                    .cnae(economico.getCNAE())
                    .anualidad(economico.getAnualidad())
                    .esPyme(economico.isEsPyme())
                    .selloPymeInnovadora(economico.isSelloPymeInnovadora())
                    .presentacionEmpresa(economico.getPresentacionEmpresa())
                    .descripcionIDI(economico.getDescripcionIDI())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el económico", e);
        }
    }

    @Override
    public EconomicoDTO actualizarDatosEconomico(ActualizarDatosEconomicoDTO economico) {
        try {
            Economico economicoEntity = economicoRepository.findById(economico.getId()).orElseThrow(() -> new EconomicoNoEncontrado(ECONOMICO_NOT_FOUND_MESSAGE + economico.getId()));
            
            Long oldHoras = economicoEntity.getHorasConvenio();
            Long newHoras = economico.getHorasConvenio();

            economicoEntity.setNombre(economico.getNombre());
            economicoEntity.setDireccion(economico.getDireccion());
            economicoEntity.setTelefono(economico.getTelefono());
            economicoEntity.setNombreContacto(economico.getNombreContacto());
            economicoEntity.setEmailContacto(economico.getEmailContacto());
            economicoEntity.setHorasConvenio(newHoras);
            economicoEntity.setUrllogo(economico.getUrllogo());
            economicoEntity.setUrlWeb(economico.getUrlWeb());
            economicoEntity.setCNAE(Long.valueOf(economico.getCnae()));
            economicoEntity.setEsPyme(economico.getEsPyme());
            if (economico.getSelloPymeInnovadora() != null) {
                economicoEntity.setSelloPymeInnovadora(economico.getSelloPymeInnovadora());
            }
            economicoEntity.setPresentacionEmpresa(economico.getPresentacionEmpresa());
            economicoEntity.setDescripcionIDI(economico.getDescripcionIDI());

            // Si las horas de convenio han cambiado, actualizamos todo el personal asociado
            if (newHoras != null && !newHoras.equals(oldHoras)) {
                log.info("Propagando cambio de horas convenio ({}) a personal del económico {}", newHoras, economicoEntity.getIdEconomico());
                for (Personal p : economicoEntity.getPersonal()) {
                    // Actualizar horas anuales en HorasPersonal
                    if (p.getHorasPersonal() != null) {
                        p.getHorasPersonal().setHorasConvenioAnual(newHoras);
                        p.getHorasPersonal().actualizarHorasMaximasAnuales();
                    }
                    // Actualizar cada periodo de contrato
                    if (p.getPeriodosContrato() != null) {
                        for (PeriodoContrato pc : p.getPeriodosContrato()) {
                            pc.setHorasConvenio(newHoras);
                        }
                    }
                    // Recalcular horas de baja si existen
                    if (p.getBajasLaborales() != null) {
                        for (BajaLaboral bl : p.getBajasLaborales()) {
                            bl.recalcularHorasDeBaja();
                        }
                    }
                }
            }

            Economico saved = economicoRepository.save(economicoEntity);
            return EconomicoDTO.builder()
                    .id(saved.getIdEconomico())
                    .nombre(saved.getNombre())
                    .cif(saved.getCif())
                    .direccion(saved.getDireccion())
                    .telefono(saved.getTelefono())
                    .nombreContacto(saved.getNombreContacto())
                    .emailContacto(saved.getEmailContacto())
                    .horasConvenio(saved.getHorasConvenio())
                    .urllogo(saved.getUrllogo())
                    .urlWeb(saved.getUrlWeb())
                    .cnae(saved.getCNAE())
                    .anualidad(saved.getAnualidad())
                    .esPyme(saved.isEsPyme())
                    .selloPymeInnovadora(saved.isSelloPymeInnovadora())
                    .presentacionEmpresa(saved.getPresentacionEmpresa())
                    .descripcionIDI(saved.getDescripcionIDI())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar los datos del económico" + e.getMessage(), e);
        }
    }
}
