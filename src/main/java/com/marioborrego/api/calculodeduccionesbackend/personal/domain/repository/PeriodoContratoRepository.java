package com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.PeriodoContrato;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PeriodoContratoRepository extends JpaRepository<PeriodoContrato, Long> {

    List<PeriodoContrato> findByPersonalIdPersonaAndAnioFiscalOrderByFechaAltaAsc(Long idPersonal, Integer anioFiscal);

    @Query("SELECT p FROM PeriodoContrato p WHERE p.personal.economico.idEconomico = :idEconomico AND p.anioFiscal = :anioFiscal ORDER BY p.personal.apellidos, p.fechaAlta")
    Page<PeriodoContrato> findByEconomicoAndAnio(@Param("idEconomico") Long idEconomico, @Param("anioFiscal") Integer anioFiscal, Pageable pageable);

    @Query("SELECT p FROM PeriodoContrato p WHERE p.personal.economico.idEconomico = :idEconomico ORDER BY p.personal.apellidos, p.fechaAlta")
    Page<PeriodoContrato> findByEconomico(@Param("idEconomico") Long idEconomico, Pageable pageable);

    List<PeriodoContrato> findByPersonalIdPersona(Long idPersonal);

    void deleteByPersonalIdPersona(Long idPersonal);
}
