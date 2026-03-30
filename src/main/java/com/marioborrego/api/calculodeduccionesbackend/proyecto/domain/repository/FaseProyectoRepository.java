package com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.FaseProyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaseProyectoRepository extends JpaRepository<FaseProyecto, Long> {
    List<FaseProyecto> findByProyectoIdProyecto(Long idProyecto);
}
