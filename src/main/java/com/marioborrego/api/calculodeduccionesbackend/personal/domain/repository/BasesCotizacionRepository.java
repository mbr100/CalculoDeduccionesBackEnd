package com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BasesCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BasesCotizacionRepository extends JpaRepository<BasesCotizacion, Long> {

    Optional<BasesCotizacion> findByPeriodoContratoId(Long idPeriodoContrato);

    @Query("SELECT b FROM BasesCotizacion b WHERE b.periodoContrato IS NOT NULL " +
            "AND b.persona.economico.idEconomico = :idEconomico " +
            "ORDER BY b.persona.nombre ASC, b.periodoContrato.fechaAlta ASC")
    List<BasesCotizacion> findAllPeriodoLinkedByEconomico(@Param("idEconomico") Long idEconomico);

    void deleteByPeriodoContratoId(Long idPeriodoContrato);
}