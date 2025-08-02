package com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BajaLaboral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BajaLaboralRepository extends JpaRepository<BajaLaboral, Long> {
}