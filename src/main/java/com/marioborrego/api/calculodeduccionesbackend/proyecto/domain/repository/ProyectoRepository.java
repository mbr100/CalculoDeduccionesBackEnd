package com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    Page<Proyecto> findProyectosByEconomico(Pageable pageable, Long idEconomico);
}