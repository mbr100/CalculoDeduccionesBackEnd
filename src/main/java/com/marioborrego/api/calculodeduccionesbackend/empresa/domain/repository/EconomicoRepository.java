package com.marioborrego.api.calculodeduccionesbackend.empresa.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.empresa.domain.models.Economico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EconomicoRepository extends JpaRepository<Economico, Integer> {
    @Query("SELECT e FROM empresa_economico e WHERE e.activo = true")
    List<Economico> findAllActivos();

    @Query("SELECT e FROM empresa_economico e WHERE e.activo = true")
    Page<Economico> findAllActivosPaginado(Pageable pageable);
}