//package com.marioborrego.api.calculodeduccionesbackend.configuration;
//
//import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
//import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
//import com.marioborrego.api.calculodeduccionesbackend.personal.business.impl.CosteHoraService;
//import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.*;
//import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
//import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.BajaLaboralRepository;
//import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PersonalRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.ArrayList;
//
//@Configuration
//public class DataLoader {
//
//    private final PersonalRepository personalRepository;
//    private final CosteHoraService costeHoraService;
//
//    public DataLoader(PersonalRepository personalRepository, CosteHoraService costeHoraService) {
//        this.personalRepository = personalRepository;
//        this.costeHoraService = costeHoraService;
//    }
//
//    @Bean
//    CommandLineRunner loadData(EconomicoRepository economicoRepository, PersonalRepository personalRepository, BajaLaboralRepository bajaLaboralRepository, CosteHoraService costeHoraService) {
//        return args -> {
//            if (personalRepository.count() == 0) {
//
//                // 1. Crear el registro Economico único
//                Economico economico = new Economico();
//                economico.setNombre("Empresa Alpha");
//                economico.setCif("B12345678");
//                economico.setDireccion("Calle Falsa 123, Madrid");
//                economico.setTelefono("911234567");
//                economico.setNombreContacto("Juan Pérez");
//                economico.setEmailContacto("juan.perez@alpha.com");
//                economico.setHorasConvenio(1600L);
//                economico.setUrllogo("https://example.com/logo1.png");
//                economico.setUrlWeb("https://empresa-alpha.com");
//                economico.setCNAE(6201L);
//                economico.setAnualidad(2024L);
//                economico.setEsPyme(true);
//                economico.setActivo(true);
//
//                Economico savedEconomico = economicoRepository.save(economico);
//
//                // 2. Crear empleados con todos sus datos relacionados
//                crearEmpleado(personalRepository, bajaLaboralRepository,
//                        "Juan Carlos", "García López", "12345678A",
//                        "Desarrollador Senior", "Desarrollo de Software",
//                        "Ingeniería Informática", "Master en Desarrollo Web",
//                        null, null, true, savedEconomico);
//
//                crearEmpleado(personalRepository, bajaLaboralRepository,
//                        "María Isabel", "Rodríguez Pérez", "87654321B",
//                        "Investigadora Principal", "I+D+i",
//                        "Doctorado en Ciencias", "Licenciatura en Física",
//                        "Master en Investigación", null, true, savedEconomico);
//
//                crearEmpleado(personalRepository, bajaLaboralRepository,
//                        "Carlos Antonio", "Martínez Sánchez", "11223344C",
//                        "Técnico de RRHH", "Recursos Humanos",
//                        "Licenciatura en Psicología", "Master en Gestión de RRHH",
//                        null, null, false, savedEconomico);
//
//                crearEmpleado(personalRepository, bajaLaboralRepository,
//                        "Ana Belén", "Fernández Jiménez", "99887766D",
//                        "Analista Junior", "Desarrollo de Software",
//                        "Grado en Informática", null, null, null, false, savedEconomico);
//
//                crearEmpleado(personalRepository, bajaLaboralRepository,
//                        "Pedro Luis", "González Moreno", "55443322E",
//                        "Investigador Senior", "I+D+i",
//                        "Doctorado en Ingeniería", "Ingeniería Industrial",
//                        "Master en Innovación", "Curso de Especialización",
//                        true, savedEconomico);
//                System.out.println("✅ Datos de ejemplo cargados exitosamente");
//            }
//        };
//    }
//
//    private void crearEmpleado(PersonalRepository personalRepository,
//                               BajaLaboralRepository bajaLaboralRepository,
//                               String nombre, String apellidos, String dni, String puesto,
//                               String departamento, String titulacion1, String titulacion2,
//                               String titulacion3, String titulacion4, boolean esInvestigador,
//                               Economico economico) {
//
//        // 1. Crear Retribución
//        Retribucion retribucion = Retribucion.builder()
//                .importeRetribucionNoIT(35000L + (long)(Math.random() * 25000)) // Entre 35k y 60k
//                .importeRetribucionExpecie(500L + (long)(Math.random() * 1000)) // Entre 500 y 1500
//                .aportaciones_prevencion_social(200L + (long)(Math.random() * 300)) // Entre 200 y 500
//                .dietas_viaje_exentas(100L + (long)(Math.random() * 400)) // Entre 100 y 500
//                .rentas_exentas_190(0L)
//                .build();
//
//        // 2. Crear Bases de Cotización
//        BasesCotizacion basesCotizacion = crearBasesCotizacion();
//
//        // 3. Crear Horas Personal (usando las horas convenio de la empresa)
//        HorasPersonal horasPersonal = HorasPersonal.builder()
//                .ejercicio(2024)
//                .fechaAltaEjercicio(LocalDate.of(2024, 1, 1))
//                .fechaBajaEjercicio(LocalDate.of(2024, 12, 31))
//                .horasConvenioAnual(economico.getHorasConvenio()) // 1600h
//                .bajas(new ArrayList<>())
//                .build();
//
//
//        // 4. Crear Bonificaciones
//        BonificacionesTrabajador bonificaciones = BonificacionesTrabajador.builder()
//                .tipoBonificacion(TiposBonificacion.BONIFICACION_PERSONAL_INVESTIGADOR)
//                .porcentajeBonificacion(new BigDecimal("45.00"))
//                .build();
//
//        // 6. Crear Personal
//        Personal personal = Personal.builder()
//                .nombre(nombre)
//                .apellidos(apellidos)
//                .dni(dni)
//                .puesto(puesto)
//                .departamento(departamento)
//                .titulacion1(titulacion1)
//                .titulacion2(titulacion2)
//                .titulacion3(titulacion3)
//                .titulacion4(titulacion4)
//                .esPersonalInvestigador(esInvestigador)
//                .economico(economico)
//                .retribucion(retribucion)
//                .basesCotizacion(basesCotizacion)
//                .horasPersonal(horasPersonal)
//                .bajasLaborales(new ArrayList<>())
//                .bonificacionesTrabajador(bonificaciones)
//                .build();
//
//        // Establecer relaciones bidireccionales
//        retribucion.setPersonal(personal);
//        basesCotizacion.setPersona(personal);
//        horasPersonal.setPersonal(personal);
//
//        // Guardar el empleado (cascade salvará las entidades relacionadas)
//        Personal savedPersonal = personalRepository.save(personal);
//
//        // 6. Crear algunas bajas laborales aleatorias
//        crearBajasLaborales(savedPersonal, bajaLaboralRepository);
//
//        CosteHoraPersonal costeHoraPersonal = CosteHoraPersonal.builder()
//                .costeHora(BigDecimal.ZERO)
//                .costeSS(BigDecimal.ZERO)
//                .horasMaximas(BigDecimal.valueOf(horasPersonal.getHorasMaximasAnuales()))
//                .retribucionTotal(BigDecimal.valueOf(retribucion.getPercepcionesSalariales()))
//                .personal(savedPersonal)
//                .build();
//
//        costeHoraService.calcularCosteHoraEconomico(savedPersonal.getEconomico());
//
//        savedPersonal.setCosteHoraPersonal(costeHoraPersonal);
//        personalRepository.save(savedPersonal);
//
//    }
//
//    private BasesCotizacion crearBasesCotizacion() {
//        // Generar bases de cotización realistas (entre 2500 y 4500 por mes)
//        long baseMinima = 2500L;
//        long variacion = 2000L;
//
//        return BasesCotizacion.builder()
//                .basesCotizacionContingenciasComunesEnero(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesFebrero(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesMarzo(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesAbril(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesMayo(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesJunio(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesJulio(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesAgosto(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesSeptiembre(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesOctubre(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesNoviembre(baseMinima + (long)(Math.random() * variacion))
//                .basesCotizacionContingenciasComunesDiciembre(baseMinima + (long)(Math.random() * variacion))
//                .build();
//    }
//
//    private void crearBajasLaborales(Personal personal, BajaLaboralRepository bajaLaboralRepository) {
//        // Crear 1-2 bajas laborales aleatorias para algunos empleados
//        if (Math.random() > 0.6) { // 40% de probabilidad de tener bajas
//            int numeroBajas = (int)(Math.random() * 2) + 1; // 1 o 2 bajas
//
//            for (int i = 0; i < numeroBajas; i++) {
//                // Generar fechas aleatorias en 2024
//                int mesInicio = (int)(Math.random() * 12) + 1;
//                int diaInicio = (int)(Math.random() * 28) + 1;
//                LocalDate fechaInicio = LocalDate.of(2024, mesInicio, diaInicio);
//
//                // La baja dura entre 3 y 15 días
//                int duracion = (int)(Math.random() * 12) + 3;
//                LocalDate fechaFin = fechaInicio.plusDays(duracion);
//
//
//                BajaLaboral baja = BajaLaboral.builder()
//                        .fechaInicio(fechaInicio)
//                        .fechaFin(fechaFin)
//                        .horasPersonal(personal.getHorasPersonal())
//                        .personal(personal)
//                        .build();
//
//                bajaLaboralRepository.save(baja);
//                personal.getBajasLaborales().add(baja);
//                personal.getHorasPersonal().getBajas().add(baja);
//                personal.getBajasLaborales().forEach(BajaLaboral::recalcularHorasDeBaja);
//                personal.getHorasPersonal().actualizarHorasMaximasAnuales();
//                personalRepository.save(personal);
//            }
//        }
//    }
//}