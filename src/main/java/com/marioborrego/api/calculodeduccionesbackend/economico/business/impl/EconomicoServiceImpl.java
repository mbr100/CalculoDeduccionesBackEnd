package com.marioborrego.api.calculodeduccionesbackend.economico.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.economico.business.interfaces.EconomicoService;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class EconomicoServiceImpl implements EconomicoService {

    private final EconomicoRepository economicoRepository;

    public EconomicoServiceImpl(EconomicoRepository economicoRepository) {
        this.economicoRepository = economicoRepository;
    }

    @Override
    public boolean eliminarEconomico(EconomicoListadoGeneralDto economico) {
        try {
            Economico economicoEntity = economicoRepository.findById(economico.getId())
                    .orElseThrow(() -> new RuntimeException("Económico no encontrado con ID: " + economico.getId()));
            economicoEntity.setActivo(false);
            economicoRepository.save(economicoEntity);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar el económico", e);
        }
    }

    public Page<EconomicoListadoGeneralDto> obtenerEconomicosPaginados(Pageable pageable) {
        try {
            Page<Economico> economicos = economicoRepository.findAllActivosPaginado(pageable);

            return economicos.map(empresa -> EconomicoListadoGeneralDto.builder()
                    .id(empresa.getIdEconomico())
                    .nombre(empresa.getNombre())
                    .cif(empresa.getCif())
                    .CNAE(empresa.getCNAE())
                    .anualidad(empresa.getAnualidad())
                    .esPyme(empresa.isEsPyme())
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el listado de económicos", e);
        }
    }

    @Override
    public boolean comprobarExistenciaEconomico(String cif, Integer anualidad) {
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
                    .CNAE(crearEconomicoDTO.getCnae())
                    .anualidad(crearEconomicoDTO.getAnualidad())
                    .esPyme(crearEconomicoDTO.getEsPyme())
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
            Economico economico = economicoRepository.findById(Math.toIntExact(idEconomico)).orElseThrow(() -> new RuntimeException("Económico no encontrado con ID: " + idEconomico));
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
                    .presentacionEmpresa(economico.getPresentacionEmpresa())
                    .descripcionIDI(economico.getDescripcionIDI())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el económico", e);
        }
    }

    @Override
    public void actualizarDatosEconomico(ActualizarDatosEconomicoDTO economico) {
        try {
            Economico economicoEntity = economicoRepository.findById(Math.toIntExact(economico.getId()))
                    .orElseThrow(() -> new RuntimeException("Económico no encontrado con ID: " + economico.getId()));
            economicoEntity.setNombre(economico.getNombre());
            economicoEntity.setDireccion(economico.getDireccion());
            economicoEntity.setTelefono(economico.getTelefono());
            economicoEntity.setNombreContacto(economico.getNombreContacto());
            economicoEntity.setEmailContacto(economico.getEmailContacto());
            economicoEntity.setHorasConvenio(economico.getHorasConvenio());
            economicoEntity.setUrllogo(economico.getUrllogo());
            economicoEntity.setUrlWeb(economico.getUrlWeb());
            economicoEntity.setCNAE(economico.getCnae());
            economicoEntity.setEsPyme(economico.getEsPyme());
            economicoEntity.setPresentacionEmpresa(economico.getPresentacionEmpresa());
            economicoEntity.setDescripcionIDI(economico.getDescripcionIDI());
            economicoRepository.save(economicoEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar los datos del económico" + e.getMessage(), e);
        }
    }
}
