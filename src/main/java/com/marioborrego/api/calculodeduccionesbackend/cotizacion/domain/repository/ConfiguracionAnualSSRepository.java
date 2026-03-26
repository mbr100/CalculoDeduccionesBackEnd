package com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ConfiguracionAnualSS;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracionAnualSSRepository extends JpaRepository<ConfiguracionAnualSS, Long> {

    Optional<ConfiguracionAnualSS> findByAnio(Integer anio);

    boolean existsByAnio(Integer anio);
}
