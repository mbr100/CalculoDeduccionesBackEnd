package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.Colaboradora;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColaboradoraRepository extends JpaRepository<Colaboradora, Long> {
    List<Colaboradora> findByEconomicoIdEconomico(Long idEconomico);
    Optional<Colaboradora> findByCifAndEconomicoIdEconomico(String cif, Long idEconomico);
}
