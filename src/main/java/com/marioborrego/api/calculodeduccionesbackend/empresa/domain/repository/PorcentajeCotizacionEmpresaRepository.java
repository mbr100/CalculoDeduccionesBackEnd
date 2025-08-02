package com.marioborrego.api.calculodeduccionesbackend.empresa.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.empresa.domain.models.PorcentajeCotizacionEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PorcentajeCotizacionEmpresaRepository extends JpaRepository<PorcentajeCotizacionEmpresa, Long> {
}