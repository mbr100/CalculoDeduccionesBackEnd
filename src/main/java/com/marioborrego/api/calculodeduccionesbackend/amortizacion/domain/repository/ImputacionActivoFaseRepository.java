package com.marioborrego.api.calculodeduccionesbackend.amortizacion.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.amortizacion.domain.models.ImputacionActivoFase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ImputacionActivoFaseRepository extends JpaRepository<ImputacionActivoFase, Long> {
    List<ImputacionActivoFase> findByActivoIdActivo(Long idActivo);
    List<ImputacionActivoFase> findByFaseIdFase(Long idFase);
    Optional<ImputacionActivoFase> findByActivoIdActivoAndFaseIdFase(Long idActivo, Long idFase);

    @Transactional
    void deleteByActivoIdActivo(Long idActivo);
}
