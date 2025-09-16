package com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BajaLaboral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BajaLaboralRepository extends JpaRepository<BajaLaboral, Long> {
    Page<BajaLaboral> findByPersonalEconomicoIdEconomico(Long idEconomico, Pageable pageable);
}