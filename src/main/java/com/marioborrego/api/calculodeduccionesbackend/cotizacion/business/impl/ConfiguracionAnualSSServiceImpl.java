package com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces.ConfiguracionAnualSSService;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ConfiguracionAnualSS;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ConfiguracionAnualSSRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.ConfiguracionAnualSSDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearConfiguracionAnualSSDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ConfiguracionAnualSSServiceImpl implements ConfiguracionAnualSSService {

    private final ConfiguracionAnualSSRepository repository;

    public ConfiguracionAnualSSServiceImpl(ConfiguracionAnualSSRepository repository) {
        this.repository = repository;
    }

    @Override
    public ConfiguracionAnualSSDTO crear(CrearConfiguracionAnualSSDTO dto) {
        if (repository.existsByAnio(dto.getAnio())) {
            throw new RuntimeException("Ya existe configuración para el año " + dto.getAnio());
        }

        ConfiguracionAnualSS entidad = ConfiguracionAnualSS.builder()
                .anio(dto.getAnio())
                .ccEmpresa(dto.getCcEmpresa())
                .ccTrabajador(dto.getCcTrabajador())
                .desempleoEmpresaIndefinido(dto.getDesempleoEmpresaIndefinido())
                .desempleoEmpresaTemporal(dto.getDesempleoEmpresaTemporal())
                .fogasa(dto.getFogasa())
                .fpEmpresa(dto.getFpEmpresa())
                .meiEmpresa(dto.getMeiEmpresa())
                .meiTrabajador(dto.getMeiTrabajador())
                .build();

        return toDTO(repository.save(entidad));
    }

    @Override
    public ConfiguracionAnualSSDTO obtenerPorAnio(Integer anio) {
        ConfiguracionAnualSS entidad = repository.findByAnio(anio)
                .orElseThrow(() -> new RuntimeException("No se encontró configuración SS para el año " + anio));
        return toDTO(entidad);
    }

    @Override
    public List<ConfiguracionAnualSSDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public ConfiguracionAnualSSDTO actualizar(Long id, CrearConfiguracionAnualSSDTO dto) {
        ConfiguracionAnualSS entidad = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró configuración SS con ID " + id));

        entidad.setAnio(dto.getAnio());
        entidad.setCcEmpresa(dto.getCcEmpresa());
        entidad.setCcTrabajador(dto.getCcTrabajador());
        entidad.setDesempleoEmpresaIndefinido(dto.getDesempleoEmpresaIndefinido());
        entidad.setDesempleoEmpresaTemporal(dto.getDesempleoEmpresaTemporal());
        entidad.setFogasa(dto.getFogasa());
        entidad.setFpEmpresa(dto.getFpEmpresa());
        entidad.setMeiEmpresa(dto.getMeiEmpresa());
        entidad.setMeiTrabajador(dto.getMeiTrabajador());

        return toDTO(repository.save(entidad));
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("No se encontró configuración SS con ID " + id);
        }
        repository.deleteById(id);
    }

    private ConfiguracionAnualSSDTO toDTO(ConfiguracionAnualSS entidad) {
        return ConfiguracionAnualSSDTO.builder()
                .id(entidad.getId())
                .anio(entidad.getAnio())
                .ccEmpresa(entidad.getCcEmpresa())
                .ccTrabajador(entidad.getCcTrabajador())
                .desempleoEmpresaIndefinido(entidad.getDesempleoEmpresaIndefinido())
                .desempleoEmpresaTemporal(entidad.getDesempleoEmpresaTemporal())
                .fogasa(entidad.getFogasa())
                .fpEmpresa(entidad.getFpEmpresa())
                .meiEmpresa(entidad.getMeiEmpresa())
                .meiTrabajador(entidad.getMeiTrabajador())
                .build();
    }
}
