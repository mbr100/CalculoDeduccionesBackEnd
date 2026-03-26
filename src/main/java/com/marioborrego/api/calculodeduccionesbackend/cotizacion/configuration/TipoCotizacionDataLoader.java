package com.marioborrego.api.calculodeduccionesbackend.cotizacion.configuration;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveOcupacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ConfiguracionAnualSS;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TarifaPrimasCnae;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveOcupacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ConfiguracionAnualSSRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.TarifaPrimasCnaeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Configuration
public class TipoCotizacionDataLoader {

    private final Logger log = LoggerFactory.getLogger(TipoCotizacionDataLoader.class);

    @Bean
    CommandLineRunner cargarConfiguracionAnualSS(ConfiguracionAnualSSRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                log.info("Configuración anual SS ya cargada, omitiendo seed");
                return;
            }

            log.info("Cargando configuración anual SS por defecto...");

            // Porcentajes comunes estables
            BigDecimal cc = new BigDecimal("23.60");
            BigDecimal ccTrab = new BigDecimal("4.70");
            BigDecimal desempleoIndef = new BigDecimal("5.50");
            BigDecimal desempleoTemp = new BigDecimal("6.70");
            BigDecimal fogasa = new BigDecimal("0.20");
            BigDecimal fp = new BigDecimal("0.60");

            // MEI por año (empresa / trabajador)
            Map<Integer, String[]> meiPorAnio = Map.of(
                2019, new String[]{"0.00", "0.00"},
                2020, new String[]{"0.00", "0.00"},
                2021, new String[]{"0.00", "0.00"},
                2022, new String[]{"0.00", "0.00"},
                2023, new String[]{"0.50", "0.10"},
                2024, new String[]{"0.58", "0.12"},
                2025, new String[]{"0.67", "0.13"},
                2026, new String[]{"0.75", "0.15"}
            );

            for (var entry : meiPorAnio.entrySet()) {
                repository.save(ConfiguracionAnualSS.builder()
                        .anio(entry.getKey())
                        .ccEmpresa(cc)
                        .ccTrabajador(ccTrab)
                        .desempleoEmpresaIndefinido(desempleoIndef)
                        .desempleoEmpresaTemporal(desempleoTemp)
                        .fogasa(fogasa)
                        .fpEmpresa(fp)
                        .meiEmpresa(new BigDecimal(entry.getValue()[0]))
                        .meiTrabajador(new BigDecimal(entry.getValue()[1]))
                        .build());
            }

            log.info("Cargadas {} configuraciones anuales SS (2019-2026)", repository.count());
        };
    }

    @Bean
    CommandLineRunner cargarTarifaPrimasCnae(TarifaPrimasCnaeRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                log.info("Tarifas de primas CNAE ya cargadas, omitiendo seed");
                return;
            }

            log.info("Cargando tarifas de primas CNAE por defecto...");

            // CNAEs con sus tipos AT/EP (IT, IMS) - datos estables entre años
            record CnaeConfig(String cnae, String descripcion, String it, String ims) {}
            List<CnaeConfig> cnaes = List.of(
                new CnaeConfig("62", "Programación, consultoría y otras actividades informáticas", "0.65", "0.35"),
                new CnaeConfig("6201", "Programación informática", "0.65", "0.35"),
                new CnaeConfig("72", "Investigación y desarrollo", "0.65", "0.35"),
                new CnaeConfig("7219", "Otra I+D en ciencias naturales y técnicas", "0.65", "0.35"),
                new CnaeConfig("41", "Construcción de edificios", "3.60", "3.60"),
                new CnaeConfig("28", "Fabricación de maquinaria y equipo n.c.o.p.", "2.20", "1.65"),
                new CnaeConfig("10", "Industria de la alimentación", "1.50", "1.00"),
                new CnaeConfig("4711", "Comercio al por menor en establecimientos no especializados", "0.65", "0.45"),
                new CnaeConfig("8610", "Actividades hospitalarias", "1.00", "1.00"),
                new CnaeConfig("71", "Servicios técnicos de arquitectura e ingeniería", "0.65", "0.35")
            );

            int total = 0;
            for (int anio = 2019; anio <= 2026; anio++) {
                for (CnaeConfig c : cnaes) {
                    BigDecimal it = new BigDecimal(c.it());
                    BigDecimal ims = new BigDecimal(c.ims());
                    repository.save(TarifaPrimasCnae.builder()
                            .cnae(c.cnae())
                            .anio(anio)
                            .descripcion(c.descripcion())
                            .tipoIt(it)
                            .tipoIms(ims)
                            .tipoTotal(it.add(ims))
                            .versionCnae("2009")
                            .build());
                    total++;
                }
            }

            log.info("Cargadas {} tarifas de primas CNAE (2019-2026)", total);
        };
    }

    @Bean
    CommandLineRunner cargarClavesOcupacion(ClaveOcupacionRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                log.info("Claves de ocupación ya cargadas, omitiendo seed");
                return;
            }

            log.info("Cargando claves de ocupación (Cuadro II) por defecto...");

            List<ClaveOcupacion> claves = List.of(
                buildClave("a", "Personal en trabajos exclusivos de oficina", "0.65", "0.35"),
                buildClave("b", "Representantes de comercio", "1.00", "0.65"),
                buildClave("d", "Trabajos habituales de limpieza de calles", "2.10", "1.50"),
                buildClave("e", "Conductores de vehículos de transporte de mercancías (>3,5 Tm)", "3.20", "3.50"),
                buildClave("f", "Personal de vuelo", "1.80", "1.50"),
                buildClave("g", "Trabajos habituales en interior de minas", "4.00", "4.80"),
                buildClave("h", "Vigilantes, guardas jurados y personal de seguridad", "1.95", "1.30")
            );

            repository.saveAll(claves);
            log.info("Cargadas {} claves de ocupación", claves.size());
        };
    }

    private ClaveOcupacion buildClave(String clave, String descripcion, String it, String ims) {
        BigDecimal tipoIt = new BigDecimal(it);
        BigDecimal tipoIms = new BigDecimal(ims);
        return ClaveOcupacion.builder()
                .clave(clave)
                .descripcion(descripcion)
                .tipoIt(tipoIt)
                .tipoIms(tipoIms)
                .tipoTotal(tipoIt.add(tipoIms))
                .activa(true)
                .build();
    }
}
