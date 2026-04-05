package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.ImputacionFacturaFase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImputacionFacturaFaseRepository extends JpaRepository<ImputacionFacturaFase, Long> {
    List<ImputacionFacturaFase> findByFacturaIdFactura(Long idFactura);
    List<ImputacionFacturaFase> findByFaseIdFase(Long idFase);
    Optional<ImputacionFacturaFase> findByFacturaIdFacturaAndFaseIdFase(Long idFactura, Long idFase);
    void deleteByFacturaIdFactura(Long idFactura);
}
