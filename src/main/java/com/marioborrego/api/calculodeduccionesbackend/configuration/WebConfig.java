package com.marioborrego.api.calculodeduccionesbackend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")  // Aplica a todos los endpoints
                        .allowedOrigins("*")  // Permite todos los orígenes
                        .allowedMethods("*")  // Permite todos los métodos (GET, POST, etc.)
                        .allowedHeaders("*"); // Permite todos los headers
            }
        };
    }
}