package com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.colaboracionesexternas.domain.models.FacturaColaboracion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FacturaColaboracionRepository extends JpaRepository<FacturaColaboracion, Long> {
    List<FacturaColaboracion> findByColaboradoraIdColaboradora(Long idColaboradora);
    List<FacturaColaboracion> findByContratoIdContrato(Long idContrato);
    List<FacturaColaboracion> findByProyectoIdProyecto(Long idProyecto);
    List<FacturaColaboracion> findByColaboradoraEconomicoIdEconomico(Long idEconomico);
}
