package com.marioborrego.api.calculodeduccionesbackend.configuration;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.helper.ValoresDefecto;
import com.marioborrego.api.calculodeduccionesbackend.personal.business.impl.CosteHoraService;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BajaLaboral;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BasesCotizacion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BonificacionesTrabajador;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.CosteHoraPersonal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.HorasPersonal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Retribucion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.PeriodoContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveContrato;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveContratoRepository;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.Proyecto;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.ProyectoPersonal;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Calificacion;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.enums.Estrategia;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository.ProyectoPersonalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Profile("!test")
public class DataLoader {

    private final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    @Order(100)
    CommandLineRunner cargarDatosDemo(EconomicoRepository economicoRepository,
                                      ProyectoPersonalRepository proyectoPersonalRepository,
                                      CosteHoraService costeHoraService,
                                      ClaveContratoRepository claveContratoRepository) {
        return args -> {
            if (economicoRepository.count() > 0) {
                log.info("Economicos ya existentes, se omite la carga de datos demo");
                return;
            }

            List<EconomicoSeed> seeds = List.of(
                    crearTecnova2024(),
                    crearBiocore2025(),
                    crearRetailInsight2026()
            );

            int totalEconomicos = 0;
            int totalPersonal = 0;
            int totalProyectos = 0;
            int totalAsignaciones = 0;

            for (EconomicoSeed seed : seeds) {
                prepararHoras(seed.economico());
                Economico economicoGuardado = economicoRepository.save(seed.economico());
                totalEconomicos++;
                totalPersonal += economicoGuardado.getPersonal().size();
                totalProyectos += economicoGuardado.getProyectos().size();

                totalAsignaciones += crearAsignaciones(economicoGuardado, seed.asignaciones(), proyectoPersonalRepository);
                crearPeriodosDemo(economicoGuardado, claveContratoRepository);
                costeHoraService.calcularCosteHoraEconomico(economicoGuardado);
            }

            log.info(
                    "Datos demo cargados: {} economicos, {} empleados, {} proyectos y {} asignaciones",
                    totalEconomicos,
                    totalPersonal,
                    totalProyectos,
                    totalAsignaciones
            );
        };
    }

    private EconomicoSeed crearTecnova2024() {
        Economico economico = crearEconomico(
                "Tecnova Software IDI, S.L.",
                "B12345670",
                "Calle Innovacion 14, Madrid",
                "910000111",
                "Lucia Ramos",
                "lucia.ramos@tecnovaidi.com",
                1740L,
                6201L,
                2024L,
                true,
                true,
                "Consultora tecnologica con foco en automatizacion industrial, plataformas cloud y explotacion fiscal de proyectos I+D+i.",
                "El ejercicio 2024 combina desarrollo de plataforma IA, sensorizacion industrial y un equipo mixto con personal investigador y de soporte."
        );

        Personal laura = crearPersonal(
                economico,
                "Laura",
                "Martin Vega",
                "12345678A",
                "Investigadora principal",
                "I+D Software",
                "Ingenieria Informatica",
                "Master en IA aplicada",
                "Doctorado en computacion",
                null,
                true,
                true,
                null,
                retribucion(42000, 1800, 1200, 400, 0),
                bases(3200, 3200, 3200, 3200, 3200, 3200, 3200, 3200, 3200, 3200, 3200, 3200),
                horas(2024, 1740, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)),
                List.of(),
                List.of(bonificacionInvestigador(2024, "40.00", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)))
        );

        Personal sergio = crearPersonal(
                economico,
                "Sergio",
                "Perez Lozano",
                "87654321B",
                "Desarrollador full stack",
                "Producto",
                "Grado en Ingenieria del Software",
                null,
                null,
                null,
                false,
                false,
                null,
                retribucion(29000, 600, 300, 150, 0),
                bases(2450, 2450, 2450, 2450, 2450, 2450, 2450, 2450, 2450, 2450, 2450, 2450),
                horas(2024, 1740, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)),
                List.of(baja(LocalDate.of(2024, 11, 4), LocalDate.of(2024, 11, 15))),
                List.of()
        );

        Personal elena = crearPersonal(
                economico,
                "Elena",
                "Ruiz Campos",
                "11223344C",
                "Analista de seguridad",
                "Ciberseguridad",
                "Grado en Telecomunicaciones",
                "Master en seguridad ofensiva",
                null,
                null,
                false,
                true,
                "h",
                retribucion(36000, 900, 600, 300, 0),
                bases(3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000, 3000),
                horas(2024, 1740, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 12, 31)),
                List.of(),
                List.of(otraBonificacion(
                        2024,
                        "15.00",
                        "Bonificacion por programa de conciliacion y formacion dual",
                        LocalDate.of(2024, 4, 1),
                        LocalDate.of(2024, 9, 30)
                ))
        );

        economico.getPersonal().addAll(List.of(laura, sergio, elena));

        Proyecto plataformaIA = crearProyecto(
                economico,
                "TNV-IA-24",
                "Plataforma de IA para clasificacion avanzada de incidencias",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                Estrategia.CERTIFICACION,
                Calificacion.I_MAS_D
        );
        Proyecto gemeloDigital = crearProyecto(
                economico,
                "TNV-IOT-24",
                "Gemelo digital para monitorizacion de lineas industriales",
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 12, 15),
                Estrategia.AUTOLIQUIDATIVO,
                Calificacion.I_MAS_D_MAS_I
        );
        economico.getProyectos().add(plataformaIA);
        economico.getProyectos().add(gemeloDigital);

        return new EconomicoSeed(
                economico,
                List.of(
                        new AsignacionSeed("TNV-IA-24", "12345678A", 920d),
                        new AsignacionSeed("TNV-IA-24", "87654321B", 610d),
                        new AsignacionSeed("TNV-IOT-24", "12345678A", 420d),
                        new AsignacionSeed("TNV-IOT-24", "11223344C", 540d)
                )
        );
    }

    private EconomicoSeed crearBiocore2025() {
        Economico economico = crearEconomico(
                "Biocore Research, S.A.",
                "A23456781",
                "Parque Cientifico 8, Barcelona",
                "930000222",
                "Javier Mena",
                "javier.mena@biocore.es",
                1700L,
                7219L,
                2025L,
                false,
                true,
                "Compania de investigacion aplicada en ciencias de la vida, analitica avanzada y producto regulado.",
                "Durante 2025 conviven proyectos con certificacion y autoliquidacion, altas parciales en ejercicio y varias bonificaciones sobre personal investigador."
        );

        Personal daniel = crearPersonal(
                economico,
                "Daniel",
                "Castro Molina",
                "22334455D",
                "Investigador senior",
                "Biotecnologia",
                "Doctorado en Bioinformatica",
                "Master en estadistica",
                null,
                null,
                true,
                false,
                null,
                retribucion(47000, 1000, 1200, 0, 0),
                bases(0, 0, 3400, 3400, 3400, 3400, 3400, 3400, 3400, 3400, 3400, 3400),
                horas(2025, 1700, LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 31)),
                List.of(baja(LocalDate.of(2025, 6, 3), LocalDate.of(2025, 6, 14))),
                List.of(bonificacionInvestigador(2025, "50.00", LocalDate.of(2025, 3, 1), LocalDate.of(2025, 12, 31)))
        );

        Personal nuria = crearPersonal(
                economico,
                "Nuria",
                "Gomez Prieto",
                "33445566E",
                "Tecnica de laboratorio",
                "Operaciones I+D",
                "Grado en Biologia",
                "Master en calidad",
                null,
                null,
                false,
                true,
                null,
                retribucion(31500, 600, 450, 0, 0),
                bases(2550, 2550, 2550, 2550, 2550, 2550, 2550, 2550, 2550, 2550, 2550, 2550),
                horas(2025, 1700, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)),
                List.of(),
                List.of(otraBonificacion(
                        2025,
                        "12.50",
                        "Bonificacion por transformacion digital de perfiles tecnicos",
                        LocalDate.of(2025, 7, 1),
                        LocalDate.of(2025, 12, 31)
                ))
        );

        Personal pablo = crearPersonal(
                economico,
                "Pablo",
                "Herrero Solis",
                "44556677F",
                "Director de programa",
                "PMO I+D",
                "Ingenieria Industrial",
                "MBA",
                null,
                null,
                false,
                true,
                "a",
                retribucion(52000, 1500, 800, 300, 0),
                bases(3900, 3900, 3900, 3900, 3900, 3900, 3900, 3900, 3900, 3900, 3900, 3900),
                horas(2025, 1700, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31)),
                List.of(),
                List.of()
        );

        economico.getPersonal().addAll(List.of(daniel, nuria, pablo));

        Proyecto biosensores = crearProyecto(
                economico,
                "BIO-SENS-25",
                "Biosensores inteligentes para deteccion temprana",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                Estrategia.CERTIFICACION,
                Calificacion.I_MAS_D
        );
        Proyecto genai = crearProyecto(
                economico,
                "BIO-ML-25",
                "Motor predictivo para validacion de ensayos biologicos",
                LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 11, 30),
                Estrategia.AUTOLIQUIDATIVO,
                Calificacion.I_MAS_D_MAS_I
        );
        economico.getProyectos().add(biosensores);
        economico.getProyectos().add(genai);

        return new EconomicoSeed(
                economico,
                List.of(
                        new AsignacionSeed("BIO-SENS-25", "22334455D", 780d),
                        new AsignacionSeed("BIO-SENS-25", "33445566E", 640d),
                        new AsignacionSeed("BIO-ML-25", "22334455D", 420d),
                        new AsignacionSeed("BIO-ML-25", "44556677F", 360d),
                        new AsignacionSeed("BIO-ML-25", "33445566E", 180d)
                )
        );
    }

    private EconomicoSeed crearRetailInsight2026() {
        Economico economico = crearEconomico(
                "Retail Insight Labs, S.L.",
                "B34567892",
                "Avenida Datos 21, Valencia",
                "960000333",
                "Patricia Navas",
                "patricia.navas@retailinsight.es",
                1680L,
                4711L,
                2026L,
                true,
                false,
                "Empresa de analitica para retail con despliegues en tienda, vision artificial y automatizacion de procesos operativos.",
                "El ejercicio 2026 sirve para probar CNAE comercial, claves de ocupacion de oficina y personal con altas y bajas parciales."
        );

        Personal alicia = crearPersonal(
                economico,
                "Alicia",
                "Torres Alba",
                "55667788G",
                "Responsable administrativa",
                "Administracion",
                "Grado en ADE",
                null,
                null,
                null,
                false,
                true,
                "a",
                retribucion(28000, 500, 300, 0, 0),
                bases(2300, 2300, 2300, 2300, 2300, 2300, 2300, 2300, 2300, 2300, 2300, 2300),
                horas(2026, 1680, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)),
                List.of(),
                List.of()
        );

        Personal jorge = crearPersonal(
                economico,
                "Jorge",
                "Vidal Cano",
                "66778899H",
                "Investigador vision artificial",
                "IA aplicada",
                "Ingenieria de Telecomunicacion",
                "Master en vision por computador",
                null,
                null,
                true,
                true,
                null,
                retribucion(39500, 900, 900, 200, 0),
                bases(3100, 3100, 3100, 3100, 3100, 3100, 3100, 3100, 3100, 3100, 3100, 3100),
                horas(2026, 1680, LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)),
                List.of(),
                List.of(bonificacionInvestigador(2026, "45.00", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31)))
        );

        Personal raul = crearPersonal(
                economico,
                "Raul",
                "Medina Pardo",
                "77889900J",
                "Tecnico de despliegue",
                "Operaciones",
                "FP Sistemas microinformaticos",
                null,
                null,
                null,
                false,
                false,
                null,
                retribucion(25500, 400, 250, 100, 0),
                bases(1900, 1900, 1900, 1900, 1900, 1900, 1900, 1900, 1900, 1900, 0, 0),
                horas(2026, 1680, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 10, 31)),
                List.of(
                        baja(LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 18)),
                        baja(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 5))
                ),
                List.of()
        );

        economico.getPersonal().addAll(List.of(alicia, jorge, raul));

        Proyecto tiendaVision = crearProyecto(
                economico,
                "RTL-VISION-26",
                "Vision artificial para seguimiento de lineales en tienda",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                Estrategia.AUTOLIQUIDATIVO,
                Calificacion.I_MAS_D
        );
        Proyecto robotica = crearProyecto(
                economico,
                "RTL-OPS-26",
                "Robotizacion de inventario y reposicion asistida",
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 10, 31),
                Estrategia.IMV,
                Calificacion.IT
        );
        economico.getProyectos().add(tiendaVision);
        economico.getProyectos().add(robotica);

        return new EconomicoSeed(
                economico,
                List.of(
                        new AsignacionSeed("RTL-VISION-26", "66778899H", 860d),
                        new AsignacionSeed("RTL-VISION-26", "55667788G", 140d),
                        new AsignacionSeed("RTL-OPS-26", "77889900J", 720d),
                        new AsignacionSeed("RTL-OPS-26", "66778899H", 280d)
                )
        );
    }

    private int crearAsignaciones(Economico economico,
                                  List<AsignacionSeed> asignaciones,
                                  ProyectoPersonalRepository proyectoPersonalRepository) {
        if (asignaciones.isEmpty()) {
            return 0;
        }

        Map<String, Proyecto> proyectosPorAcronimo = economico.getProyectos().stream()
                .collect(Collectors.toMap(Proyecto::getAcronimo, proyecto -> proyecto));
        Map<String, Personal> personalPorDni = economico.getPersonal().stream()
                .collect(Collectors.toMap(Personal::getDni, persona -> persona));

        List<ProyectoPersonal> entidades = new ArrayList<>();
        for (AsignacionSeed asignacion : asignaciones) {
            Proyecto proyecto = proyectosPorAcronimo.get(asignacion.acronimoProyecto());
            Personal personal = personalPorDni.get(asignacion.dniPersonal());

            if (proyecto == null || personal == null) {
                throw new IllegalStateException("No se ha podido resolver una asignacion demo para "
                        + asignacion.acronimoProyecto() + " / " + asignacion.dniPersonal());
            }

            ProyectoPersonal proyectoPersonal = ProyectoPersonal.builder()
                    .proyecto(proyecto)
                    .personal(personal)
                    .horasAsignadas(asignacion.horasAsignadas())
                    .build();

            proyecto.getProyectoPersonales().add(proyectoPersonal);
            personal.getProyectoPersonales().add(proyectoPersonal);
            entidades.add(proyectoPersonal);
        }

        proyectoPersonalRepository.saveAll(entidades);
        return entidades.size();
    }

    private void prepararHoras(Economico economico) {
        for (Personal persona : economico.getPersonal()) {
            HorasPersonal horas = persona.getHorasPersonal();
            List<BajaLaboral> bajas = persona.getBajasLaborales() == null
                    ? new ArrayList<>()
                    : new ArrayList<>(persona.getBajasLaborales());

            for (BajaLaboral baja : bajas) {
                baja.setPersonal(persona);
                baja.setHorasPersonal(horas);
                baja.recalcularHorasDeBaja();
            }

            horas.setBajas(bajas);
            horas.actualizarHorasMaximasAnuales();
        }
    }

    private Economico crearEconomico(String nombre,
                                     String cif,
                                     String direccion,
                                     String telefono,
                                     String nombreContacto,
                                     String emailContacto,
                                     long horasConvenio,
                                     long cnae,
                                     long anualidad,
                                     boolean esPyme,
                                     boolean selloPymeInnovadora,
                                     String presentacionEmpresa,
                                     String descripcionIdi) {
        return Economico.builder()
                .nombre(nombre)
                .cif(cif)
                .direccion(direccion)
                .telefono(telefono)
                .nombreContacto(nombreContacto)
                .emailContacto(emailContacto)
                .horasConvenio(horasConvenio)
                .urllogo("https://dummyimage.com/240x120/0f172a/ffffff&text=" + nombre.replace(" ", "+"))
                .urlWeb("https://demo." + nombre.toLowerCase().replace(",", "").replace(".", "").replace(" ", "") + ".com")
                .CNAE(cnae)
                .anualidad(anualidad)
                .esPyme(esPyme)
                .selloPymeInnovadora(selloPymeInnovadora)
                .activo(true)
                .presentacionEmpresa(presentacionEmpresa)
                .descripcionIDI(descripcionIdi)
                .build();
    }

    private Personal crearPersonal(Economico economico,
                                   String nombre,
                                   String apellidos,
                                   String dni,
                                   String puesto,
                                   String departamento,
                                   String titulacion1,
                                   String titulacion2,
                                   String titulacion3,
                                   String titulacion4,
                                   boolean esInvestigador,
                                   boolean esContratoIndefinido,
                                   String claveOcupacion,
                                   Retribucion retribucion,
                                   BasesCotizacion basesCotizacion,
                                   HorasPersonal horasPersonal,
                                   List<BajaLaboral> bajas,
                                   List<BonificacionesTrabajador> bonificaciones) {
        CosteHoraPersonal costeHoraPersonal = ValoresDefecto.getCosteHoraPersonalDefault();

        Personal personal = Personal.builder()
                .nombre(nombre)
                .apellidos(apellidos)
                .dni(dni)
                .puesto(puesto)
                .departamento(departamento)
                .titulacion1(titulacion1)
                .titulacion2(titulacion2)
                .titulacion3(titulacion3)
                .titulacion4(titulacion4)
                .esPersonalInvestigador(esInvestigador)
                .esContratoIndefinido(esContratoIndefinido)
                .claveOcupacion(claveOcupacion)
                .economico(economico)
                .retribucion(retribucion)
                .basesCotizacion(basesCotizacion)
                .horasPersonal(horasPersonal)
                .bajasLaborales(new ArrayList<>(bajas))
                .bonificaciones(new ArrayList<>(bonificaciones))
                .costeHoraPersonal(costeHoraPersonal)
                .build();

        retribucion.setPersonal(personal);
        basesCotizacion.setPersona(personal);
        horasPersonal.setPersonal(personal);
        costeHoraPersonal.setPersonal(personal);

        for (BajaLaboral baja : personal.getBajasLaborales()) {
            baja.setPersonal(personal);
            baja.setHorasPersonal(horasPersonal);
        }
        horasPersonal.setBajas(new ArrayList<>(personal.getBajasLaborales()));

        for (BonificacionesTrabajador bonificacion : personal.getBonificaciones()) {
            bonificacion.setPersonal(personal);
        }

        return personal;
    }

    private Proyecto crearProyecto(Economico economico,
                                   String acronimo,
                                   String titulo,
                                   LocalDate fechaInicio,
                                   LocalDate fechaFin,
                                   Estrategia estrategia,
                                   Calificacion calificacion) {
        return Proyecto.builder()
                .acronimo(acronimo)
                .titulo(titulo)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .estrategia(estrategia)
                .calificacion(calificacion)
                .activo(true)
                .economico(economico)
                .build();
    }

    private Retribucion retribucion(long noIt, long especie, long aportaciones, long dietas, long exentas) {
        return Retribucion.builder()
                .importeRetribucionNoIT(noIt)
                .importeRetribucionExpecie(especie)
                .aportaciones_prevencion_social(aportaciones)
                .dietas_viaje_exentas(dietas)
                .rentas_exentas_190(exentas)
                .build();
    }

    private BasesCotizacion bases(long enero, long febrero, long marzo, long abril, long mayo, long junio,
                                  long julio, long agosto, long septiembre, long octubre, long noviembre, long diciembre) {
        return BasesCotizacion.builder()
                .basesCotizacionContingenciasComunesEnero(enero)
                .basesCotizacionContingenciasComunesFebrero(febrero)
                .basesCotizacionContingenciasComunesMarzo(marzo)
                .basesCotizacionContingenciasComunesAbril(abril)
                .basesCotizacionContingenciasComunesMayo(mayo)
                .basesCotizacionContingenciasComunesJunio(junio)
                .basesCotizacionContingenciasComunesJulio(julio)
                .basesCotizacionContingenciasComunesAgosto(agosto)
                .basesCotizacionContingenciasComunesSeptiembre(septiembre)
                .basesCotizacionContingenciasComunesOctubre(octubre)
                .basesCotizacionContingenciasComunesNoviembre(noviembre)
                .basesCotizacionContingenciasComunesDiciembre(diciembre)
                .build();
    }

    private HorasPersonal horas(int ejercicio, long horasConvenio, LocalDate fechaAlta, LocalDate fechaBaja) {
        return HorasPersonal.builder()
                .ejercicio(ejercicio)
                .fechaAltaEjercicio(fechaAlta)
                .fechaBajaEjercicio(fechaBaja)
                .horasConvenioAnual(horasConvenio)
                .bajas(new ArrayList<>())
                .build();
    }

    private BajaLaboral baja(LocalDate fechaInicio, LocalDate fechaFin) {
        return BajaLaboral.builder()
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .build();
    }

    private BonificacionesTrabajador bonificacionInvestigador(int anioFiscal,
                                                              String porcentaje,
                                                              LocalDate fechaInicio,
                                                              LocalDate fechaFin) {
        return BonificacionesTrabajador.builder()
                .tipoBonificacion(TiposBonificacion.BONIFICACION_PERSONAL_INVESTIGADOR)
                .porcentajeBonificacion(new BigDecimal(porcentaje))
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .anioFiscal(anioFiscal)
                .build();
    }

    private BonificacionesTrabajador otraBonificacion(int anioFiscal,
                                                      String porcentaje,
                                                      String descripcion,
                                                      LocalDate fechaInicio,
                                                      LocalDate fechaFin) {
        return BonificacionesTrabajador.builder()
                .tipoBonificacion(TiposBonificacion.OTRA_BONIFICACION)
                .porcentajeBonificacion(new BigDecimal(porcentaje))
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .anioFiscal(anioFiscal)
                .descripcion(descripcion)
                .build();
    }

    private void crearPeriodosDemo(Economico economico, ClaveContratoRepository claveContratoRepository) {
        int anio = Math.toIntExact(economico.getAnualidad());
        Map<String, ClaveContrato> claves = claveContratoRepository.findAll().stream()
                .collect(Collectors.toMap(ClaveContrato::getClave, c -> c));

        for (Personal personal : economico.getPersonal()) {
            HorasPersonal hp = personal.getHorasPersonal();
            LocalDate alta = hp.getFechaAltaEjercicio();
            LocalDate baja = hp.getFechaBajaEjercicio();
            boolean indefinido = personal.isEsContratoIndefinido();

            String claveStr = indefinido ? "100" : "402";
            ClaveContrato clave = claves.get(claveStr);

            if (clave == null) {
                log.warn("Clave de contrato '{}' no encontrada, omitiendo períodos para {}", claveStr, personal.getDni());
                continue;
            }

            // Base CC mensual ≈ media mensual de las bases de cotización existentes
            long baseCcAnual = personal.getBasesCotizacion().getBasesCotizacionContingenciasComunesAnual();
            BigDecimal baseCcMensual = BigDecimal.valueOf(baseCcAnual).divide(BigDecimal.valueOf(12), 2, java.math.RoundingMode.HALF_UP);

            PeriodoContrato periodo = PeriodoContrato.builder()
                    .personal(personal)
                    .claveContrato(clave)
                    .fechaAlta(alta)
                    .fechaBaja(baja.getMonthValue() == 12 && baja.getDayOfMonth() == 31 ? null : baja)
                    .anioFiscal(anio)
                    .porcentajeJornada(new BigDecimal("100.00"))
                    .baseCcMensual(baseCcMensual)
                    .baseCpMensual(baseCcMensual)
                    .build();

            personal.getPeriodosContrato().add(periodo);
        }
    }

    private record EconomicoSeed(Economico economico, List<AsignacionSeed> asignaciones) {
    }

    private record AsignacionSeed(String acronimoProyecto, String dniPersonal, Double horasAsignadas) {
    }
}
