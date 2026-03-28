package com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.NaturalezaContrato;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaveContratoRepository extends JpaRepository<ClaveContrato, String> {

    List<ClaveContrato> findByVigenteTrue();

    List<ClaveContrato> findByVigenteTrueOrderByClaveAsc();

    List<ClaveContrato> findByNaturalezaAndVigenteTrue(NaturalezaContrato naturaleza);
}
