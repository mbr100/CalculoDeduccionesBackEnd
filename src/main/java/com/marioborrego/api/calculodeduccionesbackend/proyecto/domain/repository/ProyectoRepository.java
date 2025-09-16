package com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.Proyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    @Query("SELECT p FROM Proyecto p WHERE p.economico.idEconomico = :idEconomico")
    Page<Proyecto> findByEconomico(@Param("idEconomico") Long idEconomico, Pageable pageable);

    @Query("SELECT p FROM Proyecto p WHERE p.economico.idEconomico = :idEconomico")
    List<Proyecto> findAllByIdEconomico(Long idEconomico);
}