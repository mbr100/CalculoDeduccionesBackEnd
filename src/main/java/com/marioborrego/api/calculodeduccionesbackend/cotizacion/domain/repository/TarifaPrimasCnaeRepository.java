package com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TarifaPrimasCnae;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TarifaPrimasCnaeRepository extends JpaRepository<TarifaPrimasCnae, Long> {

    Optional<TarifaPrimasCnae> findByCnaeAndAnio(String cnae, Integer anio);

    List<TarifaPrimasCnae> findByAnio(Integer anio);

    List<TarifaPrimasCnae> findByCnae(String cnae);

    boolean existsByCnaeAndAnio(String cnae, Integer anio);
}
