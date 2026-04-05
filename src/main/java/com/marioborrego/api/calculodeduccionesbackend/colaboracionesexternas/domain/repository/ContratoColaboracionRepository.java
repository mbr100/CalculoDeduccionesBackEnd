package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.ContratoColaboracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratoColaboracionRepository extends JpaRepository<ContratoColaboracion, Long> {
    List<ContratoColaboracion> findByColaboradoraIdColaboradora(Long idColaboradora);
    List<ContratoColaboracion> findByColaboradoraEconomicoIdEconomico(Long idEconomico);
}
