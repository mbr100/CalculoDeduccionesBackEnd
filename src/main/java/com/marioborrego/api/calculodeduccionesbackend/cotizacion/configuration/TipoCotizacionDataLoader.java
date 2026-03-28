package com.marioborrego.api.calculodeduccionesbackend.cotizacion.configuration;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveContrato;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveOcupacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ConfiguracionAnualSS;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TarifaPrimasCnae;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveContratoRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveOcupacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ConfiguracionAnualSSRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.TarifaPrimasCnaeRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.NaturalezaContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TipoJornada;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Configuration
public class TipoCotizacionDataLoader {

    private final Logger log = LoggerFactory.getLogger(TipoCotizacionDataLoader.class);

    @Bean
    @Order(10)
    CommandLineRunner cargarConfiguracionAnualSS(ConfiguracionAnualSSRepository repository) {
        return args -> {
            log.info("Comprobando configuración anual SS por defecto...");

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

            int insertadas = 0;
            for (var entry : meiPorAnio.entrySet()) {
                Integer anio = entry.getKey();
                if (repository.findByAnio(anio).isPresent()) {
                    continue;
                }

                repository.save(ConfiguracionAnualSS.builder()
                        .anio(anio)
                        .ccEmpresa(cc)
                        .ccTrabajador(ccTrab)
                        .desempleoEmpresaIndefinido(desempleoIndef)
                        .desempleoEmpresaTemporal(desempleoTemp)
                        .fogasa(fogasa)
                        .fpEmpresa(fp)
                        .meiEmpresa(new BigDecimal(entry.getValue()[0]))
                        .meiTrabajador(new BigDecimal(entry.getValue()[1]))
                        .build());
                insertadas++;
            }

            log.info("Configuración anual SS revisada: {} insertadas, {} totales", insertadas, repository.count());
        };
    }

    @Bean
    @Order(20)
    CommandLineRunner cargarTarifaPrimasCnae(TarifaPrimasCnaeRepository repository) {
        return args -> {
            log.info("Comprobando tarifas de primas CNAE por defecto...");

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

            int insertadas = 0;
            for (int anio = 2019; anio <= 2026; anio++) {
                for (CnaeConfig c : cnaes) {
                    if (repository.findByCnaeAndAnio(c.cnae(), anio).isPresent()) {
                        continue;
                    }

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
                    insertadas++;
                }
            }

            log.info("Tarifas CNAE revisadas: {} insertadas, {} totales", insertadas, repository.count());
        };
    }

    @Bean
    @Order(30)
    CommandLineRunner cargarClavesOcupacion(ClaveOcupacionRepository repository) {
        return args -> {
            log.info("Comprobando claves de ocupación (Cuadro II) por defecto...");

            List<ClaveOcupacion> claves = List.of(
                buildClave("a", "Personal en trabajos exclusivos de oficina", "0.65", "0.35"),
                buildClave("b", "Representantes de comercio", "1.00", "0.65"),
                buildClave("d", "Trabajos habituales de limpieza de calles", "2.10", "1.50"),
                buildClave("e", "Conductores de vehículos de transporte de mercancías (>3,5 Tm)", "3.20", "3.50"),
                buildClave("f", "Personal de vuelo", "1.80", "1.50"),
                buildClave("g", "Trabajos habituales en interior de minas", "4.00", "4.80"),
                buildClave("h", "Vigilantes, guardas jurados y personal de seguridad", "1.95", "1.30")
            );

            int insertadas = 0;
            for (ClaveOcupacion clave : claves) {
                if (repository.existsById(clave.getClave())) {
                    continue;
                }
                repository.save(clave);
                insertadas++;
            }

            log.info("Claves de ocupación revisadas: {} insertadas, {} totales", insertadas, repository.count());
        };
    }

    @Bean
    @Order(40)
    CommandLineRunner cargarClavesContrato(ClaveContratoRepository repository) {
        return args -> {
            log.info("Comprobando claves de contrato T-19 por defecto...");
            if (repository.count() > 0) {
                log.info("Claves de contrato ya existen, omitiendo carga");
                return;
            }

            List<ClaveContrato> claves = List.of(
                // INDEFINIDOS TIEMPO COMPLETO
                cc("100", "Indefinido TC ordinario", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_COMPLETO, true),
                cc("109", "Indefinido TC fomento/transformación temporal", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_COMPLETO, true),
                cc("130", "Indefinido TC discapacitados", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_COMPLETO, true),
                cc("139", "Indefinido TC discapacitados transformación", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_COMPLETO, true),
                cc("150", "Indefinido TC fomento contratación inicial", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_COMPLETO, true),
                cc("151", "Indefinido TC exclusión social", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_COMPLETO, true),
                cc("189", "Indefinido TC transformación contrato temporal", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_COMPLETO, true),
                // INDEFINIDOS TIEMPO PARCIAL
                cc("200", "Indefinido TP ordinario", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_PARCIAL, true),
                cc("209", "Indefinido TP fomento/transformación", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_PARCIAL, true),
                cc("230", "Indefinido TP discapacitados", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_PARCIAL, true),
                cc("239", "Indefinido TP discapacitados transformación", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_PARCIAL, true),
                cc("250", "Indefinido TP fomento contratación inicial", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_PARCIAL, true),
                cc("251", "Indefinido TP exclusión social", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_PARCIAL, true),
                cc("289", "Indefinido TP transformación contrato temporal", NaturalezaContrato.INDEFINIDO, TipoJornada.TIEMPO_PARCIAL, true),
                // INDEFINIDOS FIJO DISCONTINUO
                cc("300", "Fijo discontinuo ordinario", NaturalezaContrato.INDEFINIDO, TipoJornada.FIJO_DISCONTINUO, true),
                cc("309", "Fijo discontinuo fomento/transformación", NaturalezaContrato.INDEFINIDO, TipoJornada.FIJO_DISCONTINUO, true),
                cc("330", "Fijo discontinuo discapacitados", NaturalezaContrato.INDEFINIDO, TipoJornada.FIJO_DISCONTINUO, true),
                cc("339", "Fijo discontinuo discapacitados transformación", NaturalezaContrato.INDEFINIDO, TipoJornada.FIJO_DISCONTINUO, true),
                cc("350", "Fijo discontinuo fomento contratación", NaturalezaContrato.INDEFINIDO, TipoJornada.FIJO_DISCONTINUO, true),
                cc("389", "Fijo discontinuo transformación temporal", NaturalezaContrato.INDEFINIDO, TipoJornada.FIJO_DISCONTINUO, true),
                // TEMPORALES TIEMPO COMPLETO
                cc("401", "Obra o servicio TC (histórico)", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, false),
                cc("402", "Eventual circunstancias producción TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("403", "Inserción TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("404", "Predoctoral TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("406", "AAPP Plan Recuperación TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("407", "Artistas/técnicos/auxiliares TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("408", "Carácter administrativo TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("410", "Interinidad TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("411", "Docente investigador universitario TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("412", "Acceso personal investigador doctor TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("413", "Deportistas profesionales TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("418", "Interinidad administrativa TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("420", "Prácticas TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                ccFormacion("421", "Formación en alternancia TC"),
                cc("430", "Discapacitados TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("441", "Relevo TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("450", "Fomento contratación TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("451", "Exclusión social TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                cc("452", "Empresas inserción TC", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_COMPLETO, true),
                // TEMPORALES TIEMPO PARCIAL
                cc("500", "Temporal TP genérico", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("501", "Obra o servicio TP (histórico)", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, false),
                cc("502", "Eventual circunstancias producción TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("503", "Inserción TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("506", "AAPP Plan Recuperación TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("507", "Artistas/técnicos TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("508", "Carácter administrativo TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("510", "Interinidad TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("511", "Docente investigador universitario TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("513", "Deportistas profesionales TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("518", "Interinidad administrativa TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("520", "Prácticas TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                ccFormacionTP("521", "Formación en alternancia TP"),
                cc("530", "Discapacitados TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("540", "Relevo TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("541", "Relevo TP discapacitados", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("550", "Fomento contratación TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("551", "Exclusión social TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                cc("552", "Empresas inserción TP", NaturalezaContrato.TEMPORAL, TipoJornada.TIEMPO_PARCIAL, true),
                // BECARIOS (claves internas)
                ccBecario("B01", "Becario prácticas remuneradas TC", NaturalezaContrato.BECARIO_REMUNERADO, TipoJornada.TIEMPO_COMPLETO),
                ccBecario("B02", "Becario prácticas remuneradas TP", NaturalezaContrato.BECARIO_REMUNERADO, TipoJornada.TIEMPO_PARCIAL),
                ccBecario("B03", "Becario prácticas NO remuneradas TC", NaturalezaContrato.BECARIO_NO_REMUNERADO, TipoJornada.TIEMPO_COMPLETO),
                ccBecario("B04", "Becario prácticas NO remuneradas TP", NaturalezaContrato.BECARIO_NO_REMUNERADO, TipoJornada.TIEMPO_PARCIAL)
            );

            repository.saveAll(claves);
            log.info("Claves de contrato insertadas: {}", claves.size());
        };
    }

    private ClaveContrato cc(String clave, String desc, NaturalezaContrato nat, TipoJornada jornada, boolean vigente) {
        return ClaveContrato.builder()
                .clave(clave).descripcion(desc).naturaleza(nat).jornada(jornada)
                .cotizaDesempleo(true).cotizaFogasa(true).cotizaFp(true).cotizaMei(true).cotizaCcEstandar(true)
                .vigente(vigente).build();
    }

    private ClaveContrato ccFormacion(String clave, String desc) {
        return ClaveContrato.builder()
                .clave(clave).descripcion(desc).naturaleza(NaturalezaContrato.FORMACION).jornada(TipoJornada.TIEMPO_COMPLETO)
                .cotizaDesempleo(false).cotizaFogasa(false).cotizaFp(false).cotizaMei(false).cotizaCcEstandar(false)
                .vigente(true).build();
    }

    private ClaveContrato ccFormacionTP(String clave, String desc) {
        return ClaveContrato.builder()
                .clave(clave).descripcion(desc).naturaleza(NaturalezaContrato.FORMACION).jornada(TipoJornada.TIEMPO_PARCIAL)
                .cotizaDesempleo(false).cotizaFogasa(false).cotizaFp(false).cotizaMei(false).cotizaCcEstandar(false)
                .vigente(true).build();
    }

    private ClaveContrato ccBecario(String clave, String desc, NaturalezaContrato nat, TipoJornada jornada) {
        return ClaveContrato.builder()
                .clave(clave).descripcion(desc).naturaleza(nat).jornada(jornada)
                .cotizaDesempleo(false).cotizaFogasa(false).cotizaFp(false).cotizaMei(false).cotizaCcEstandar(false)
                .vigente(true).build();
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
