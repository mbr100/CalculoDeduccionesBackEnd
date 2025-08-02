package com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Retribucion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetribucionRepository extends JpaRepository<Retribucion, Integer> {
}