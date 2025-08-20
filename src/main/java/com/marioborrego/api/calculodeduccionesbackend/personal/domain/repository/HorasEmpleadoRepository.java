package com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.HorasPersonal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HorasEmpleadoRepository extends JpaRepository<HorasPersonal, Long> {
}