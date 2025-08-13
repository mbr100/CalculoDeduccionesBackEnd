package com.marioborrego.api.calculodeduccionesbackend.configuration;

import com.marioborrego.api.calculodeduccionesbackend.empresa.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.empresa.domain.repository.EconomicoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner loadData(EconomicoRepository economicoRepository) {
        return args -> {

            Economico eco1 = new Economico();
            eco1.setNombre("Empresa Alpha");
            eco1.setCif("B12345678");
            eco1.setDireccion("Calle Falsa 123, Madrid");
            eco1.setTelefono("911234567");
            eco1.setNombreContacto("Juan Pérez");
            eco1.setEmailContacto("juan.perez@alpha.com");
            eco1.setHorasConvenio(1600L);
            eco1.setUrllogo("https://example.com/logo1.png");
            eco1.setUrlWeb("https://empresa-alpha.com");
            eco1.setCNAE("6201");
            eco1.setAnualidad(2025);
            eco1.setEsPyme(true);
            eco1.setActivo(true);

            Economico eco2 = new Economico();
            eco2.setNombre("Beta Solutions");
            eco2.setCif("B87654321");
            eco2.setDireccion("Av. Innovación 45, Barcelona");
            eco2.setTelefono("931234567");
            eco2.setNombreContacto("María López");
            eco2.setEmailContacto("maria.lopez@beta.com");
            eco2.setHorasConvenio(1750L);
            eco2.setUrllogo("https://example.com/logo2.png");
            eco2.setUrlWeb("https://beta-solutions.com");
            eco2.setCNAE("7112");
            eco2.setAnualidad(2025);
            eco2.setEsPyme(false);
            eco2.setActivo(true);

            Economico eco5 = new Economico();
            eco5.setNombre("Epsilon Tech");
            eco5.setCif("B56789012");
            eco5.setDireccion("Paseo de la Ciencia 8, Sevilla");
            eco5.setTelefono("954123456");
            eco5.setNombreContacto("Carlos Ruiz");
            eco5.setEmailContacto("carlos.ruiz@epsilon.com");
            eco5.setHorasConvenio(1600L);
            eco5.setUrllogo("https://example.com/logo5.png");
            eco5.setUrlWeb("https://epsilon-tech.com");
            eco5.setCNAE("6203");
            eco5.setAnualidad(2025);
            eco5.setEsPyme(false);
            eco5.setActivo(true);

            Economico eco6 = new Economico();
            eco6.setNombre("Zeta Innovación");
            eco6.setCif("B67890123");
            eco6.setDireccion("Calle del Progreso 15, Zaragoza");
            eco6.setTelefono("976123456");
            eco6.setNombreContacto("Ana Martínez");
            eco6.setEmailContacto("ana.martinez@zeta.com");
            eco6.setHorasConvenio(1580L);
            eco6.setUrllogo("https://example.com/logo6.png");
            eco6.setUrlWeb("https://zeta-innovacion.com");
            eco6.setCNAE("7219");
            eco6.setAnualidad(2025);
            eco6.setEsPyme(true);
            eco6.setActivo(true);

            Economico eco7 = new Economico();
            eco7.setNombre("Omega Digital");
            eco7.setCif("B78901234");
            eco7.setDireccion("Calle de la Red 9, Málaga");
            eco7.setTelefono("952123456");
            eco7.setNombreContacto("Luis Torres");
            eco7.setEmailContacto("luis.torres@omega.com");
            eco7.setHorasConvenio(1720L);
            eco7.setUrllogo("https://example.com/logo7.png");
            eco7.setUrlWeb("https://omega-digital.com");
            eco7.setCNAE("6209");
            eco7.setAnualidad(2025);
            eco7.setEsPyme(false);
            eco7.setActivo(true);

            Economico eco8 = new Economico();
            eco8.setNombre("Sigma Cloud");
            eco8.setCif("B89012345");
            eco8.setDireccion("Av. de los Datos 4, Murcia");
            eco8.setTelefono("968123456");
            eco8.setNombreContacto("Isabel López");
            eco8.setEmailContacto("isabel.lopez@sigma.com");
            eco8.setHorasConvenio(1680L);
            eco8.setUrllogo("https://example.com/logo8.png");
            eco8.setUrlWeb("https://sigma-cloud.com");
            eco8.setCNAE("6312");
            eco8.setAnualidad(2025);
            eco8.setEsPyme(true);
            eco8.setActivo(true);

            Economico eco9 = new Economico();
            eco9.setNombre("Lambda Soft");
            eco9.setCif("B90123456");
            eco9.setDireccion("Plaza del Código 11, Valladolid");
            eco9.setTelefono("983123456");
            eco9.setNombreContacto("Miguel Ángel Romero");
            eco9.setEmailContacto("miguel.romero@lambda.com");
            eco9.setHorasConvenio(1650L);
            eco9.setUrllogo("https://example.com/logo9.png");
            eco9.setUrlWeb("https://lambda-soft.com");
            eco9.setCNAE("5829");
            eco9.setAnualidad(2025);
            eco9.setEsPyme(false);
            eco9.setActivo(true);

            Economico eco10 = new Economico();
            eco10.setNombre("Theta AI");
            eco10.setCif("B01234567");
            eco10.setDireccion("Calle de la Inteligencia 3, Salamanca");
            eco10.setTelefono("923123456");
            eco10.setNombreContacto("Paula Hernández");
            eco10.setEmailContacto("paula.hernandez@theta.com");
            eco10.setHorasConvenio(1700L);
            eco10.setUrllogo("https://example.com/logo10.png");
            eco10.setUrlWeb("https://theta-ai.com");
            eco10.setCNAE("6201");
            eco10.setAnualidad(2025);
            eco10.setEsPyme(true);
            eco10.setActivo(true);

            Economico eco11 = new Economico();
            eco11.setNombre("Kappa Robotics");
            eco11.setCif("B11223344");
            eco11.setDireccion("Calle del Futuro 22, San Sebastián");
            eco11.setTelefono("943123456");
            eco11.setNombreContacto("Javier Castro");
            eco11.setEmailContacto("javier.castro@kappa.com");
            eco11.setHorasConvenio(1750L);
            eco11.setUrllogo("https://example.com/logo11.png");
            eco11.setUrlWeb("https://kappa-robotics.com");
            eco11.setCNAE("2899");
            eco11.setAnualidad(2025);
            eco11.setEsPyme(false);
            eco11.setActivo(true);

            Economico eco12 = new Economico();
            eco12.setNombre("Nova Systems");
            eco12.setCif("B22334455");
            eco12.setDireccion("Avenida del Desarrollo 14, Alicante");
            eco12.setTelefono("965123456");
            eco12.setNombreContacto("Carmen Ortega");
            eco12.setEmailContacto("carmen.ortega@nova.com");
            eco12.setHorasConvenio(1600L);
            eco12.setUrllogo("https://example.com/logo12.png");
            eco12.setUrlWeb("https://nova-systems.com");
            eco12.setCNAE("6202");
            eco12.setAnualidad(2025);
            eco12.setEsPyme(true);
            eco12.setActivo(true);

            Economico eco13 = new Economico();
            eco13.setNombre("Orion Data");
            eco13.setCif("B33445566");
            eco13.setDireccion("Calle de la Información 6, Córdoba");
            eco13.setTelefono("957123456");
            eco13.setNombreContacto("Andrés Navarro");
            eco13.setEmailContacto("andres.navarro@orion.com");
            eco13.setHorasConvenio(1680L);
            eco13.setUrllogo("https://example.com/logo13.png");
            eco13.setUrlWeb("https://orion-data.com");
            eco13.setCNAE("6311");
            eco13.setAnualidad(2025);
            eco13.setEsPyme(false);
            eco13.setActivo(true);

            Economico eco14 = new Economico();
            eco14.setNombre("Ares Security");
            eco14.setCif("B44556677");
            eco14.setDireccion("Calle Segura 19, Santander");
            eco14.setTelefono("942123456");
            eco14.setNombreContacto("Patricia Morales");
            eco14.setEmailContacto("patricia.morales@ares.com");
            eco14.setHorasConvenio(1720L);
            eco14.setUrllogo("https://example.com/logo14.png");
            eco14.setUrlWeb("https://ares-security.com");
            eco14.setCNAE("6209");
            eco14.setAnualidad(2025);
            eco14.setEsPyme(true);
            eco14.setActivo(true);

            Economico eco15 = new Economico();
            eco15.setNombre("Atlas Cloud");
            eco15.setCif("B55667788");
            eco15.setDireccion("Plaza del Servidor 3, León");
            eco15.setTelefono("987123456");
            eco15.setNombreContacto("Roberto Sánchez");
            eco15.setEmailContacto("roberto.sanchez@atlas.com");
            eco15.setHorasConvenio(1650L);
            eco15.setUrllogo("https://example.com/logo15.png");
            eco15.setUrlWeb("https://atlas-cloud.com");
            eco15.setCNAE("6312");
            eco15.setAnualidad(2025);
            eco15.setEsPyme(false);
            eco15.setActivo(true);

            Economico eco16 = new Economico();
            eco16.setNombre("Helios AI");
            eco16.setCif("B66778899");
            eco16.setDireccion("Avenida del Sol 7, Palma de Mallorca");
            eco16.setTelefono("971123456");
            eco16.setNombreContacto("Eva Jiménez");
            eco16.setEmailContacto("eva.jimenez@helios.com");
            eco16.setHorasConvenio(1700L);
            eco16.setUrllogo("https://example.com/logo16.png");
            eco16.setUrlWeb("https://helios-ai.com");
            eco16.setCNAE("6201");
            eco16.setAnualidad(2025);
            eco16.setEsPyme(true);
            eco16.setActivo(true);

            Economico eco17 = new Economico();
            eco17.setNombre("Pegasus Robotics");
            eco17.setCif("B77889900");
            eco17.setDireccion("Calle del Robot 12, Gijón");
            eco17.setTelefono("985123456");
            eco17.setNombreContacto("Fernando López");
            eco17.setEmailContacto("fernando.lopez@pegasus.com");
            eco17.setHorasConvenio(1750L);
            eco17.setUrllogo("https://example.com/logo17.png");
            eco17.setUrlWeb("https://pegasus-robotics.com");
            eco17.setCNAE("2899");
            eco17.setAnualidad(2025);
            eco17.setEsPyme(false);
            eco17.setActivo(true);

            Economico eco18 = new Economico();
            eco18.setNombre("Aurora Labs");
            eco18.setCif("B88990011");
            eco18.setDireccion("Calle de la Ciencia 8, Burgos");
            eco18.setTelefono("947123456");
            eco18.setNombreContacto("Lorena Martín");
            eco18.setEmailContacto("lorena.martin@aurora.com");
            eco18.setHorasConvenio(1600L);
            eco18.setUrllogo("https://example.com/logo18.png");
            eco18.setUrlWeb("https://aurora-labs.com");
            eco18.setCNAE("7219");
            eco18.setAnualidad(2025);
            eco18.setEsPyme(true);
            eco18.setActivo(true);

            Economico eco19 = new Economico();
            eco19.setNombre("Titan Software");
            eco19.setCif("B99001122");
            eco19.setDireccion("Calle del Código 10, Granada");
            eco19.setTelefono("958123456");
            eco19.setNombreContacto("Jorge Pérez");
            eco19.setEmailContacto("jorge.perez@titan.com");
            eco19.setHorasConvenio(1680L);
            eco19.setUrllogo("https://example.com/logo19.png");
            eco19.setUrlWeb("https://titan-software.com");
            eco19.setCNAE("5829");
            eco19.setAnualidad(2025);
            eco19.setEsPyme(false);
            eco19.setActivo(true);

            Economico eco20 = new Economico();
            eco20.setNombre("Phoenix Networks");
            eco20.setCif("B10111213");
            eco20.setDireccion("Calle de la Red 4, Vigo");
            eco20.setTelefono("986123456");
            eco20.setNombreContacto("Sofía Rodríguez");
            eco20.setEmailContacto("sofia.rodriguez@phoenix.com");
            eco20.setHorasConvenio(1720L);
            eco20.setUrllogo("https://example.com/logo20.png");
            eco20.setUrlWeb("https://phoenix-networks.com");
            eco20.setCNAE("6203");
            eco20.setAnualidad(2025);
            eco20.setEsPyme(true);
            eco20.setActivo(true);

            Economico eco21 = new Economico();
            eco21.setNombre("Nebula Dev");
            eco21.setCif("B12131415");
            eco21.setDireccion("Plaza de la Tecnología 6, Oviedo");
            eco21.setTelefono("984123456");
            eco21.setNombreContacto("Pablo García");
            eco21.setEmailContacto("pablo.garcia@nebula.com");
            eco21.setHorasConvenio(1650L);
            eco21.setUrllogo("https://example.com/logo21.png");
            eco21.setUrlWeb("https://nebula-dev.com");
            eco21.setCNAE("6202");
            eco21.setAnualidad(2025);
            eco21.setEsPyme(true);
            eco21.setActivo(true);

            Economico eco22 = new Economico();
            eco22.setNombre("Cronos Tech");
            eco22.setCif("B14151617");
            eco22.setDireccion("Avenida del Tiempo 1, Lugo");
            eco22.setTelefono("982123456");
            eco22.setNombreContacto("Marta Herrera");
            eco22.setEmailContacto("marta.herrera@cronos.com");
            eco22.setHorasConvenio(1700L);
            eco22.setUrllogo("https://example.com/logo22.png");
            eco22.setUrlWeb("https://cronos-tech.com");
            eco22.setCNAE("6209");
            eco22.setAnualidad(2025);
            eco22.setEsPyme(false);
            eco22.setActivo(true);

            Economico eco23 = new Economico();
            eco23.setNombre("Hercules Cloud");
            eco23.setCif("B16171819");
            eco23.setDireccion("Calle de la Fuerza 2, Cáceres");
            eco23.setTelefono("927123456");
            eco23.setNombreContacto("Alberto Muñoz");
            eco23.setEmailContacto("alberto.munoz@hercules.com");
            eco23.setHorasConvenio(1600L);
            eco23.setUrllogo("https://example.com/logo23.png");
            eco23.setUrlWeb("https://hercules-cloud.com");
            eco23.setCNAE("6312");
            eco23.setAnualidad(2025);
            eco23.setEsPyme(true);
            eco23.setActivo(true);

            Economico eco24 = new Economico();
            eco24.setNombre("Vulcan Innovations");
            eco24.setCif("B18192021");
            eco24.setDireccion("Calle del Fuego 7, Almería");
            eco24.setTelefono("950123456");
            eco24.setNombreContacto("Beatriz Navarro");
            eco24.setEmailContacto("beatriz.navarro@vulcan.com");
            eco24.setHorasConvenio(1750L);
            eco24.setUrllogo("https://example.com/logo24.png");
            eco24.setUrlWeb("https://vulcan-innovations.com");
            eco24.setCNAE("7219");
            eco24.setAnualidad(2025);
            eco24.setEsPyme(false);
            eco24.setActivo(true);

            Economico eco25 = new Economico();
            eco25.setNombre("Zephyr Digital");
            eco25.setCif("B20212223");
            eco25.setDireccion("Calle del Viento 5, Huelva");
            eco25.setTelefono("959123456");
            eco25.setNombreContacto("Cristina Vega");
            eco25.setEmailContacto("cristina.vega@zephyr.com");
            eco25.setHorasConvenio(1680L);
            eco25.setUrllogo("https://example.com/logo25.png");
            eco25.setUrlWeb("https://zephyr-digital.com");
            eco25.setCNAE("6201");
            eco25.setAnualidad(2025);
            eco25.setEsPyme(true);
            eco25.setActivo(true);


            economicoRepository.saveAll(Arrays.asList(eco1, eco2, eco5, eco6, eco7, eco8, eco9, eco10, eco11, eco12, eco13, eco14, eco15, eco16, eco17, eco18, eco19, eco20, eco21, eco22, eco23, eco24, eco25));

            System.out.println("✅ Datos de prueba insertados en la tabla ECONOMICO");
        };
    }
}
