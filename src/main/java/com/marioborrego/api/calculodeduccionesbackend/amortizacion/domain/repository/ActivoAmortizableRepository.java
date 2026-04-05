package com.marioborrego.api.calculodeduccionesbackend.amortizacion.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.amortizacion.domain.models.ActivoAmortizable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivoAmortizableRepository extends JpaRepository<ActivoAmortizable, Long> {
    List<ActivoAmortizable> findByEconomicoIdEconomico(Long idEconomico);
    List<ActivoAmortizable> findByProyectoIdProyecto(Long idProyecto);
}
