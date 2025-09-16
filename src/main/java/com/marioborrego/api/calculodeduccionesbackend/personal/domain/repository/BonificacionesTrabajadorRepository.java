package com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BonificacionesTrabajador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BonificacionesTrabajadorRepository extends JpaRepository<BonificacionesTrabajador,Long> {

    @Query("SELECT b FROM BonificacionesTrabajador b WHERE b.personal.economico.idEconomico = :idEconomico")
    Page<BonificacionesTrabajador> findBonificacionesTrabajadorsByIdEconomico(@Param("idEconomico") Long idEconomico, Pageable pageable);
}
