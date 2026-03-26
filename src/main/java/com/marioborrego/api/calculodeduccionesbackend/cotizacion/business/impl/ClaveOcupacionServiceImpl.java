package com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.business.interfaces.ClaveOcupacionService;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveOcupacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveOcupacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.ClaveOcupacionDTO;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.presentation.dto.CrearClaveOcupacionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClaveOcupacionServiceImpl implements ClaveOcupacionService {

    private final ClaveOcupacionRepository repository;

    public ClaveOcupacionServiceImpl(ClaveOcupacionRepository repository) {
        this.repository = repository;
    }

    @Override
    public ClaveOcupacionDTO crear(CrearClaveOcupacionDTO dto) {
        if (repository.existsById(dto.getClave())) {
            throw new RuntimeException("Ya existe clave de ocupación '" + dto.getClave() + "'");
        }

        ClaveOcupacion entidad = ClaveOcupacion.builder()
                .clave(dto.getClave())
                .descripcion(dto.getDescripcion())
                .tipoIt(dto.getTipoIt())
                .tipoIms(dto.getTipoIms())
                .tipoTotal(dto.getTipoIt().add(dto.getTipoIms()))
                .activa(dto.getActiva() != null ? dto.getActiva() : true)
                .build();

        return toDTO(repository.save(entidad));
    }

    @Override
    public List<ClaveOcupacionDTO> listarTodos() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Override
    public List<ClaveOcupacionDTO> listarActivas() {
        return repository.findByActivaTrue().stream().map(this::toDTO).toList();
    }

    @Override
    public ClaveOcupacionDTO actualizar(String clave, CrearClaveOcupacionDTO dto) {
        ClaveOcupacion entidad = repository.findById(clave)
                .orElseThrow(() -> new RuntimeException("No se encontró clave de ocupación '" + clave + "'"));

        entidad.setDescripcion(dto.getDescripcion());
        entidad.setTipoIt(dto.getTipoIt());
        entidad.setTipoIms(dto.getTipoIms());
        entidad.setTipoTotal(dto.getTipoIt().add(dto.getTipoIms()));
        if (dto.getActiva() != null) {
            entidad.setActiva(dto.getActiva());
        }

        return toDTO(repository.save(entidad));
    }

    @Override
    public void eliminar(String clave) {
        if (!repository.existsById(clave)) {
            throw new RuntimeException("No se encontró clave de ocupación '" + clave + "'");
        }
        repository.deleteById(clave);
    }

    private ClaveOcupacionDTO toDTO(ClaveOcupacion entidad) {
        return ClaveOcupacionDTO.builder()
                .clave(entidad.getClave())
                .descripcion(entidad.getDescripcion())
                .tipoIt(entidad.getTipoIt())
                .tipoIms(entidad.getTipoIms())
                .tipoTotal(entidad.getTipoTotal())
                .activa(entidad.getActiva())
                .build();
    }
}
