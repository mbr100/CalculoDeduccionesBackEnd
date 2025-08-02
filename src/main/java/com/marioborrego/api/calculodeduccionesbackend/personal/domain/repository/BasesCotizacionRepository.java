package com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BasesCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasesCotizacionRepository extends JpaRepository<BasesCotizacion, Integer> {
}