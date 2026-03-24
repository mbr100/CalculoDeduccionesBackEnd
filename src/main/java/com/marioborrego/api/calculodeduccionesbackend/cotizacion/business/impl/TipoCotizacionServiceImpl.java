package com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces.TipoCotizacionService;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TipoCotizacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.TipoCotizacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearTipoCotizacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.TipoCotizacionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TipoCotizacionServiceImpl implements TipoCotizacionService {

    private final TipoCotizacionRepository repository;

    public TipoCotizacionServiceImpl(TipoCotizacionRepository repository) {
        this.repository = repository;
    }

    @Override
    public TipoCotizacionDTO crear(CrearTipoCotizacionDTO dto) {
        if (repository.existsByCnaeAndAnualidad(dto.getCnae(), dto.getAnualidad())) {
            throw new RuntimeException("Ya existe un tipo de cotización para CNAE " + dto.getCnae() + " y anualidad " + dto.getAnualidad());
        }

        TipoCotizacion entidad = TipoCotizacion.builder()
                .cnae(dto.getCnae())
                .anualidad(dto.getAnualidad())
                .descripcion(dto.getDescripcion())
                .contingenciasComunes(dto.getContingenciasComunes())
                .accidentesTrabajoIT(dto.getAccidentesTrabajoIT())
                .accidentesTrabajoIMS(dto.getAccidentesTrabajoIMS())
                .desempleoIndefinido(dto.getDesempleoIndefinido())
                .desempleoTemporal(dto.getDesempleoTemporal())
                .fogasa(dto.getFogasa())
                .formacionProfesional(dto.getFormacionProfesional())
                .mei(dto.getMei())
                .build();

        TipoCotizacion guardado = repository.save(entidad);
        return toDTO(guardado);
    }

    @Override
    public TipoCotizacionDTO obtenerPorCnaeYAnualidad(String cnae, Integer anualidad) {
        TipoCotizacion entidad = repository.findByCnaeAndAnualidad(cnae, anualidad)
                .orElseThrow(() -> new RuntimeException("No se encontró tipo de cotización para CNAE " + cnae + " y anualidad " + anualidad));
        return toDTO(entidad);
    }

    @Override
    public List<TipoCotizacionDTO> listarTodos(Integer anualidad) {
        List<TipoCotizacion> lista = anualidad != null
                ? repository.findByAnualidad(anualidad)
                : repository.findAll();
        return lista.stream().map(this::toDTO).toList();
    }

    @Override
    public TipoCotizacionDTO actualizar(Long id, CrearTipoCotizacionDTO dto) {
        TipoCotizacion entidad = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró tipo de cotización con ID " + id));

        entidad.setCnae(dto.getCnae());
        entidad.setAnualidad(dto.getAnualidad());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setContingenciasComunes(dto.getContingenciasComunes());
        entidad.setAccidentesTrabajoIT(dto.getAccidentesTrabajoIT());
        entidad.setAccidentesTrabajoIMS(dto.getAccidentesTrabajoIMS());
        entidad.setDesempleoIndefinido(dto.getDesempleoIndefinido());
        entidad.setDesempleoTemporal(dto.getDesempleoTemporal());
        entidad.setFogasa(dto.getFogasa());
        entidad.setFormacionProfesional(dto.getFormacionProfesional());
        entidad.setMei(dto.getMei());

        TipoCotizacion guardado = repository.save(entidad);
        return toDTO(guardado);
    }

    @Override
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("No se encontró tipo de cotización con ID " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public boolean existePorCnaeYAnualidad(String cnae, Integer anualidad) {
        return repository.existsByCnaeAndAnualidad(cnae, anualidad);
    }

    private TipoCotizacionDTO toDTO(TipoCotizacion entidad) {
        return TipoCotizacionDTO.builder()
                .id(entidad.getId())
                .cnae(entidad.getCnae())
                .anualidad(entidad.getAnualidad())
                .descripcion(entidad.getDescripcion())
                .contingenciasComunes(entidad.getContingenciasComunes())
                .accidentesTrabajoIT(entidad.getAccidentesTrabajoIT())
                .accidentesTrabajoIMS(entidad.getAccidentesTrabajoIMS())
                .accidentesTrabajoTotal(entidad.getAccidentesTrabajoTotal())
                .desempleoIndefinido(entidad.getDesempleoIndefinido())
                .desempleoTemporal(entidad.getDesempleoTemporal())
                .fogasa(entidad.getFogasa())
                .formacionProfesional(entidad.getFormacionProfesional())
                .mei(entidad.getMei())
                .build();
    }
}
