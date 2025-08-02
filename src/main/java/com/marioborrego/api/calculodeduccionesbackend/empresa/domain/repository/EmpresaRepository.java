package com.marioborrego.api.calculodeduccionesbackend.empresa.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.empresa.domain.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
}