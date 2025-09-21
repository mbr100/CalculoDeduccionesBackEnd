package com.marioborrego.api.calculodeduccionesbackend.economico.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.economico.business.interfaces.GastoProyectoService;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.GastoProyectoDetalladoDTO;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.PartidaGastoDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.ProyectoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GastoProyectoServiceImpl implements GastoProyectoService {

    private final ProyectoRepository proyectoRepository;

    public GastoProyectoServiceImpl(ProyectoRepository proyectoRepository) {
        this.proyectoRepository = proyectoRepository;
    }

    @Override
    public List<GastoProyectoDetalladoDTO> obtenerGastoDetalladoPorEconomico(Long idEconomico) {
        List<Object[]> resultados = proyectoRepository.calcularGastoPersonalPorProyecto(idEconomico);

        return resultados.stream().map(r -> {
            Long idProyecto = ((Number) r[0]).longValue();
            String acronimo = (String) r[1];
            String titulo = (String) r[2];
            Double gastoPersonal = r[3] != null ? ((Number) r[3]).doubleValue() : 0.0;

            PartidaGastoDTO partidaPersonal = new PartidaGastoDTO("PERSONAL", gastoPersonal);
            return GastoProyectoDetalladoDTO.builder()
                    .idProyecto(idProyecto)
                    .acronimo(acronimo)
                    .titulo(titulo)
                    .partidas(List.of(partidaPersonal)).build();
        }).collect(Collectors.toList());
    }
}
