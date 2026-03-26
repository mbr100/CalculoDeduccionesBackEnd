package com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BonificacionesTrabajador;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BonificacionesTrabajadorRepository extends JpaRepository<BonificacionesTrabajador, Long> {

    @Query("SELECT b FROM BonificacionesTrabajador b WHERE b.personal.economico.idEconomico = :idEconomico")
    Page<BonificacionesTrabajador> findBonificacionesTrabajadorsByIdEconomico(@Param("idEconomico") Long idEconomico, Pageable pageable);

    List<BonificacionesTrabajador> findByPersonalIdPersonaAndAnioFiscal(Long idPersonal, Integer anioFiscal);

    List<BonificacionesTrabajador> findByPersonalIdPersonaAndAnioFiscalAndTipoBonificacion(
            Long idPersonal, Integer anioFiscal, TiposBonificacion tipoBonificacion);

    @Query("""
        SELECT COUNT(b) > 0 FROM BonificacionesTrabajador b
        WHERE b.personal.idPersona = :idPersonal
        AND b.tipoBonificacion = 'BONIFICACION_PERSONAL_INVESTIGADOR'
        AND b.fechaInicio <= :fecha
        AND b.fechaFin >= :fecha
    """)
    boolean tieneBonificacionInvestigadorEnFecha(
            @Param("idPersonal") Long idPersonal,
            @Param("fecha") LocalDate fecha);

    @Query("""
        SELECT DISTINCT b.personal.idPersona FROM BonificacionesTrabajador b
        WHERE b.tipoBonificacion = 'BONIFICACION_PERSONAL_INVESTIGADOR'
        AND b.anioFiscal = :anio
    """)
    List<Long> findPersonalConBonificacionInvestigador(@Param("anio") Integer anio);

    List<BonificacionesTrabajador> findByPersonalIdPersona(Long idPersonal);
}
