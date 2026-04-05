package com.marioborrego.api.calculodeduccionesbackend.materialfungible.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.materialfungible.domain.models.FacturaMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacturaMaterialRepository extends JpaRepository<FacturaMaterial, Long> {
    List<FacturaMaterial> findByEconomicoIdEconomico(Long idEconomico);
    List<FacturaMaterial> findByProyectoIdProyecto(Long idProyecto);
}
