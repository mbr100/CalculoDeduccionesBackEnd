package com.marioborrego.api.calculodeduccionesbackend.economico.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.FacturaColaboracion;
import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.repository.FacturaColaboracionRepository;
import com.marioborrego.api.calculodeduccionesbackend.materialfungible.domain.repository.FacturaMaterialRepository;
import com.marioborrego.api.calculodeduccionesbackend.economico.business.interfaces.GastoProyectoService;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.views.GastoPersonalProyectoView;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.GastoProyectoDetalladoDTO;
import com.marioborrego.api.calculodeduccionesbackend.economico.presentation.dto.PartidaGastoDTO;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Calificacion;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.ProyectoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GastoProyectoServiceImpl implements GastoProyectoService {


    private final ProyectoRepository proyectoRepository;
    private final FacturaColaboracionRepository facturaColaboracionRepository;
    private final FacturaMaterialRepository facturaMaterialRepository;

    public GastoProyectoServiceImpl(ProyectoRepository proyectoRepository,
                                    FacturaColaboracionRepository facturaColaboracionRepository,
                                    FacturaMaterialRepository facturaMaterialRepository) {
        this.proyectoRepository = proyectoRepository;
        this.facturaColaboracionRepository = facturaColaboracionRepository;
        this.facturaMaterialRepository = facturaMaterialRepository;
    }

    @Override
    public List<GastoProyectoDetalladoDTO> calcularGastosPorEconomico(Long idEconomico) {
        List<GastoPersonalProyectoView> gastosPersonal = proyectoRepository.calcularGastoPersonalPorProyecto(idEconomico);

        return gastosPersonal.stream()
                .map(this::convertirAGastoDetallado)
                .collect(Collectors.toList());
    }


    private GastoProyectoDetalladoDTO convertirAGastoDetallado(GastoPersonalProyectoView view) {
        // Crear partida de personal
        PartidaGastoDTO partidaPersonal = PartidaGastoDTO.builder()
                .tipoGasto("Personal")
                .importe(view.getGastoPersonal() != null ? view.getGastoPersonal() : 0.0)
                .build();

        double totalColaboraciones = facturaColaboracionRepository.findByProyectoIdProyecto(view.getIdProyecto())
                .stream()
                .mapToDouble(f -> {
                    double importeFinal = f.getBaseImponible() + (f.getIva() * f.getPorcentajeProrrata() / 100.0);
                    return importeFinal * (f.getPorcentajeValidez() / 100.0);
                })
                .sum();

        PartidaGastoDTO partidaColaboraciones = PartidaGastoDTO.builder()
                .tipoGasto("Colaboraciones Externas")
                .importe(totalColaboraciones)
                .build();

        double totalMateriales = facturaMaterialRepository.findByProyectoIdProyecto(view.getIdProyecto())
                .stream()
                .mapToDouble(f -> {
                    double importeFinal = f.getBaseImponible() + (f.getIva() * f.getPorcentajeProrrata() / 100.0);
                    return importeFinal * (f.getPorcentajeValidez() / 100.0);
                })
                .sum();

        PartidaGastoDTO partidaMaterialesFungibles = PartidaGastoDTO.builder()
                .tipoGasto("Materiales Fungibles")
                .importe(totalMateriales)
                .build();

        PartidaGastoDTO partidaAmortizaciones = PartidaGastoDTO.builder()
                .tipoGasto("Amortizaciones")
                .importe(0.0)
                .build();

        PartidaGastoDTO partidaOtros = PartidaGastoDTO.builder()
                .tipoGasto("Otros")
                .importe(0.0)
                .build();


        List<PartidaGastoDTO> partidas = Arrays.asList(partidaPersonal, partidaColaboraciones, partidaMaterialesFungibles, partidaAmortizaciones, partidaOtros);

        // Determinar porcentaje de deducción según calificación
        Long porcentajeDeduccion = determinarPorcentajeDeduccion(view.getCalificacion());

        // Calcular total y deducciones
        Double gastoPersonal = view.getGastoPersonal() != null ? view.getGastoPersonal() : 0.0;
        Double total = gastoPersonal + totalColaboraciones + totalMateriales;
        Long totalDeduccion = Math.round(total * porcentajeDeduccion / 100.0);

        return GastoProyectoDetalladoDTO.builder()
                .idProyecto(view.getIdProyecto())
                .acronimo(view.getAcronimo())
                .titulo(view.getTitulo())
                .partidas(partidas)
                .porcentajeDeduccion(porcentajeDeduccion)
                .totalDeduccion(totalDeduccion)
                .deduccion(totalDeduccion)
                .total(total)
                .build();
    }

    /**
     * Determina el porcentaje de deducción según la calificación del proyecto
     */
    private Long determinarPorcentajeDeduccion(Calificacion calificacion) {
        if (calificacion == null) {
            return 0L;
        }
        return switch (calificacion) {
            case IT -> 12L;
            case I_MAS_D, I_MAS_D_MAS_I -> 25L;
            case NADA -> 0L;
        };
    }


}
