package com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EconomicoRepository extends JpaRepository<Economico, Long> {
    @Query("SELECT e FROM empresa_economico e WHERE e.activo = true")
    List<Economico> findAllActivos();

    @Query("SELECT e FROM empresa_economico e WHERE e.activo = true")
    Page<Economico> findAllActivosPaginado(Pageable pageable);

    @Query("SELECT e FROM empresa_economico e WHERE e.cif = ?1 AND e.anualidad = ?2 AND e.activo = true")
    List<Economico> comprobarExistencia(String cif, Integer anualidad);

    List<Economico> findByCifAndAnualidadAndActivoTrue(String cif, Long anualidad);
}