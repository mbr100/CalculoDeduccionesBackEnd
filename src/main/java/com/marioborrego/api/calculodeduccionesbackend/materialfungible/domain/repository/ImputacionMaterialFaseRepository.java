package com.marioborrego.api.calculodeduccionesbackend.materialfungible.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.materialfungible.domain.models.ImputacionMaterialFase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImputacionMaterialFaseRepository extends JpaRepository<ImputacionMaterialFase, Long> {
    List<ImputacionMaterialFase> findByFacturaIdFactura(Long idFactura);
    List<ImputacionMaterialFase> findByFaseIdFase(Long idFase);
    Optional<ImputacionMaterialFase> findByFacturaIdFacturaAndFaseIdFase(Long idFactura, Long idFase);
    void deleteByFacturaIdFactura(Long idFactura);
}
