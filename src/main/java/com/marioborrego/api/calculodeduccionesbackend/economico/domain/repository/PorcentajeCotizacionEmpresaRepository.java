package com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.PorcentajeCotizacionEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PorcentajeCotizacionEmpresaRepository extends JpaRepository<PorcentajeCotizacionEmpresa, Long> {

    @Query("SELECT p FROM Porcentajes_cotizacion_empresa p WHERE p.CNAE = ?1")
    Optional<PorcentajeCotizacionEmpresa> findbyCNAE(int cnae);
}