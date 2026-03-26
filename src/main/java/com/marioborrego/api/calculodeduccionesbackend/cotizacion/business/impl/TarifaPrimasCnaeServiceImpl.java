package com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces.TarifaPrimasCnaeService;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TarifaPrimasCnae;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.TarifaPrimasCnaeRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearTarifaPrimasCnaeDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.TarifaPrimasCnaeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TarifaPrimasCnaeServiceImpl implements TarifaPrimasCnaeService {

    private final TarifaPrimasCnaeRepository repository;

    public TarifaPrimasCnaeServiceImpl(TarifaPrimasCnaeRepository repository) {
        this.repository = repository;
    }

    @Override
    public TarifaPrimasCnaeDTO crear(CrearTarifaPrimasCnaeDTO dto) {
        if (repository.existsByCnaeAndAnio(dto.getCnae(), dto.getAnio())) {
            throw new RuntimeException("Ya existe tarifa para CNAE " + dto.getCnae() + " y año " + dto.getAnio());
        }

        TarifaPrimasCnae entidad = TarifaPrimasCnae.builder()
                .cnae(dto.getCnae())
                .anio(dto.getAnio())
                .descripcion(dto.getDescripcion())
                .tipoIt(dto.getTipoIt())
                .tipoIms(dto.getTipoIms())
                .tipoTotal(dto.getTipoIt().add(dto.getTipoIms()))
                .versionCnae(dto.getVersionCnae() != null ? dto.getVersionCnae() : "2009")
                .build();

        return toDTO(repository.save(entidad));
    }

    @Override
    public TarifaPrimasCnaeDTO obtenerPorCnaeYAnio(String cnae, Integer anio) {
        TarifaPrimasCnae entidad = repository.findByCnaeAndAnio(cnae, anio)
                .orElseThrow(() -> new RuntimeException("No se encontró tarifa para CNAE " + cnae + " y año " + anio));
        return toDTO(entidad);
    }

    @Override
    public List<TarifaPrimasCnaeDTO> listarTodos(Integer anio) {
        List<TarifaPrimasCnae> lista = anio != null
                ? repository.findByAnio(anio)
                : repository.findAll();
        return lista.stream().map(this::toDTO).toList();
    }

    @Override
    public TarifaPrimasCnaeDTO actualizar(Long id, CrearTarifaPrimasCnaeDTO dto) {
        TarifaPrimasCnae entidad = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró tarifa con ID " + id));

        entidad.setCnae(dto.getCnae());
        entidad.setAnio(dto.getAnio());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setTipoIt(dto.getTipoIt());
        entidad.setTipoIms(dto.getTipoIms());
        entidad.setTipoTotal(dto.getTipoIt().add(dto.getTipoIms()));
        entidad.setVersionCnae(dto.getVersionCnae() != null ? dto.getVersionCnae() : entidad.getVersionCnae());

        return toDTO(repository.save(entidad));
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("No se encontró tarifa con ID " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean existePorCnaeYAnio(String cnae, Integer anio) {
        return repository.existsByCnaeAndAnio(cnae, anio);
    }

    private TarifaPrimasCnaeDTO toDTO(TarifaPrimasCnae entidad) {
        return TarifaPrimasCnaeDTO.builder()
                .id(entidad.getId())
                .cnae(entidad.getCnae())
                .anio(entidad.getAnio())
                .descripcion(entidad.getDescripcion())
                .tipoIt(entidad.getTipoIt())
                .tipoIms(entidad.getTipoIms())
                .tipoTotal(entidad.getTipoTotal())
                .versionCnae(entidad.getVersionCnae())
                .build();
    }
}
