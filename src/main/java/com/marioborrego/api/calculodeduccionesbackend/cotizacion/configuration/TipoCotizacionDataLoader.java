package com.marioborrego.api.calculodeduccionesbackend.cotizacion.configuration;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TipoCotizacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.TipoCotizacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class TipoCotizacionDataLoader {

    private final Logger log = LoggerFactory.getLogger(TipoCotizacionDataLoader.class);

    @Bean
    CommandLineRunner cargarTiposCotizacion(TipoCotizacionRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                log.info("Tipos de cotización ya cargados, omitiendo seed");
                return;
            }

            log.info("Cargando tipos de cotización por defecto...");

            // Porcentajes comunes 2025
            BigDecimal cc = new BigDecimal("23.60");
            BigDecimal fogasa = new BigDecimal("0.20");
            BigDecimal fp = new BigDecimal("0.60");
            BigDecimal desempleoIndef = new BigDecimal("5.50");
            BigDecimal desempleoTemp = new BigDecimal("6.70");
            BigDecimal mei2025 = new BigDecimal("0.67");
            BigDecimal mei2026 = new BigDecimal("0.75");

            List<TipoCotizacion> tipos2025 = List.of(
                // CNAE 62 - Programación, consultoría informática
                buildTipo("62", 2025, "Programación, consultoría y otras actividades informáticas",
                        cc, "0.65", "0.35", desempleoIndef, desempleoTemp, fogasa, fp, mei2025),
                // CNAE 6201 - Programación informática
                buildTipo("6201", 2025, "Programación informática",
                        cc, "0.65", "0.35", desempleoIndef, desempleoTemp, fogasa, fp, mei2025),
                // CNAE 72 - Investigación y desarrollo
                buildTipo("72", 2025, "Investigación y desarrollo",
                        cc, "0.65", "0.35", desempleoIndef, desempleoTemp, fogasa, fp, mei2025),
                // CNAE 7219 - Otra investigación y desarrollo experimental en ciencias naturales y técnicas
                buildTipo("7219", 2025, "Otra I+D en ciencias naturales y técnicas",
                        cc, "0.65", "0.35", desempleoIndef, desempleoTemp, fogasa, fp, mei2025),
                // CNAE 41 - Construcción de edificios
                buildTipo("41", 2025, "Construcción de edificios",
                        cc, "3.60", "3.60", desempleoIndef, desempleoTemp, fogasa, fp, mei2025),
                // CNAE 28 - Fabricación de maquinaria y equipo
                buildTipo("28", 2025, "Fabricación de maquinaria y equipo n.c.o.p.",
                        cc, "2.20", "1.65", desempleoIndef, desempleoTemp, fogasa, fp, mei2025),
                // CNAE 10 - Industria de la alimentación
                buildTipo("10", 2025, "Industria de la alimentación",
                        cc, "1.50", "1.00", desempleoIndef, desempleoTemp, fogasa, fp, mei2025),
                // CNAE 4711 - Comercio al por menor en establecimientos no especializados
                buildTipo("4711", 2025, "Comercio al por menor en establecimientos no especializados",
                        cc, "0.65", "0.45", desempleoIndef, desempleoTemp, fogasa, fp, mei2025),
                // CNAE 8610 - Actividades hospitalarias
                buildTipo("8610", 2025, "Actividades hospitalarias",
                        cc, "1.00", "1.00", desempleoIndef, desempleoTemp, fogasa, fp, mei2025),
                // CNAE 71 - Servicios técnicos de arquitectura e ingeniería
                buildTipo("71", 2025, "Servicios técnicos de arquitectura e ingeniería",
                        cc, "0.65", "0.35", desempleoIndef, desempleoTemp, fogasa, fp, mei2025)
            );

            // Mismos CNAEs para 2026 con MEI actualizado
            List<TipoCotizacion> tipos2026 = List.of(
                buildTipo("62", 2026, "Programación, consultoría y otras actividades informáticas",
                        cc, "0.65", "0.35", desempleoIndef, desempleoTemp, fogasa, fp, mei2026),
                buildTipo("6201", 2026, "Programación informática",
                        cc, "0.65", "0.35", desempleoIndef, desempleoTemp, fogasa, fp, mei2026),
                buildTipo("72", 2026, "Investigación y desarrollo",
                        cc, "0.65", "0.35", desempleoIndef, desempleoTemp, fogasa, fp, mei2026),
                buildTipo("7219", 2026, "Otra I+D en ciencias naturales y técnicas",
                        cc, "0.65", "0.35", desempleoIndef, desempleoTemp, fogasa, fp, mei2026),
                buildTipo("41", 2026, "Construcción de edificios",
                        cc, "3.60", "3.60", desempleoIndef, desempleoTemp, fogasa, fp, mei2026),
                buildTipo("28", 2026, "Fabricación de maquinaria y equipo n.c.o.p.",
                        cc, "2.20", "1.65", desempleoIndef, desempleoTemp, fogasa, fp, mei2026),
                buildTipo("10", 2026, "Industria de la alimentación",
                        cc, "1.50", "1.00", desempleoIndef, desempleoTemp, fogasa, fp, mei2026),
                buildTipo("4711", 2026, "Comercio al por menor en establecimientos no especializados",
                        cc, "0.65", "0.45", desempleoIndef, desempleoTemp, fogasa, fp, mei2026),
                buildTipo("8610", 2026, "Actividades hospitalarias",
                        cc, "1.00", "1.00", desempleoIndef, desempleoTemp, fogasa, fp, mei2026),
                buildTipo("71", 2026, "Servicios técnicos de arquitectura e ingeniería",
                        cc, "0.65", "0.35", desempleoIndef, desempleoTemp, fogasa, fp, mei2026)
            );

            repository.saveAll(tipos2025);
            repository.saveAll(tipos2026);
            log.info("Cargados {} tipos de cotización (2025 + 2026)", tipos2025.size() + tipos2026.size());
        };
    }

    private TipoCotizacion buildTipo(String cnae, int anualidad, String descripcion,
                                      BigDecimal cc, String it, String ims,
                                      BigDecimal desempleoIndef, BigDecimal desempleoTemp,
                                      BigDecimal fogasa, BigDecimal fp, BigDecimal mei) {
        return TipoCotizacion.builder()
                .cnae(cnae)
                .anualidad(anualidad)
                .descripcion(descripcion)
                .contingenciasComunes(cc)
                .accidentesTrabajoIT(new BigDecimal(it))
                .accidentesTrabajoIMS(new BigDecimal(ims))
                .desempleoIndefinido(desempleoIndef)
                .desempleoTemporal(desempleoTemp)
                .fogasa(fogasa)
                .formacionProfesional(fp)
                .mei(mei)
                .build();
    }
}
