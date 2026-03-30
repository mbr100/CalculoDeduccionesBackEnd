package com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.AsignacionFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionFaseRepository extends JpaRepository<AsignacionFase, Long> {
    List<AsignacionFase> findByProyectoPersonalProyectoIdProyecto(Long idProyecto);
    Optional<AsignacionFase> findByProyectoPersonalIdAndFaseProyectoIdFase(Long idProyectoPersonal, Long idFase);
    void deleteByFaseProyectoIdFase(Long idFase);
}
