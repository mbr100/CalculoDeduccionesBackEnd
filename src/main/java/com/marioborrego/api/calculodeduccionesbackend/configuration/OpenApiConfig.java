package com.marioborrego.api.calculodeduccionesbackend.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("API de Proyectos")
                        .description("Documentación de la API de gestión de proyectos")
                        .version("1.0.0")
                        .license(new License().name("Licencia Apache 2.0").url("http://www.apache.org/licenses/")));

    }
}
