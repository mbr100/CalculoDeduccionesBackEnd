package com.marioborrego.api.calculodeduccionesbackend.empresa.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.empresa.business.interfaces.EconomicoService;
import com.marioborrego.api.calculodeduccionesbackend.empresa.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.empresa.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.empresa.presentation.dto.EconomicoListadoGeneralDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EconomicoServiceImpl implements EconomicoService {

    private final EconomicoRepository economicoRepository;

    public EconomicoServiceImpl(EconomicoRepository economicoRepository) {
        this.economicoRepository = economicoRepository;
    }

    @Override
    public List<EconomicoListadoGeneralDto> obtenerTodosLosEconomicos() {
        try {
            List<Economico> economicos = economicoRepository.findAllActivos();
            if (economicos.isEmpty()) {
                return List.of();
            }
            return economicos.stream().map(empresa -> EconomicoListadoGeneralDto.builder()
                            .id(empresa.getIdEconomico())
                            .nombre(empresa.getNombre())
                            .cif(empresa.getCif())
                            .CNAE(empresa.getCNAE())
                            .anualidad(empresa.getAnualidad())
                            .esPyme(empresa.isEsPyme())
                            .build())
                    .toList();
        } catch (Exception e) {
            // Aquí podrías manejar la excepción de manera más específica o registrar el error
            throw new RuntimeException("Error al obtener el listado de económicos", e);
        }
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
}
