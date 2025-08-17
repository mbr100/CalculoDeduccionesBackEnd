package com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.PorcentajeCotizacionEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PorcentajeCotizacionEmpresaRepository extends JpaRepository<PorcentajeCotizacionEmpresa, Long> {
}