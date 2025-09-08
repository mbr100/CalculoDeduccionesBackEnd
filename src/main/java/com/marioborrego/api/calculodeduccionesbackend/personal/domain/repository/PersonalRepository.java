package com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalRepository extends JpaRepository<Personal, Long> {

    @Query("SELECT p FROM Personal p WHERE p.economico.idEconomico = ?1")
    Page<Personal> findPersonalByeconomicoId(Long idEconomico, Pageable pageable);

    @Query("SELECT p FROM Personal p WHERE p.economico.idEconomico = ?1")
    List<Personal> findByEconomicoId(Long idEconomico);
}
