package com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TipoCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TipoCotizacionRepository extends JpaRepository<TipoCotizacion, Long> {

    Optional<TipoCotizacion> findByCnaeAndAnualidad(String cnae, Integer anualidad);

    List<TipoCotizacion> findByAnualidad(Integer anualidad);

    List<TipoCotizacion> findByCnae(String cnae);

    boolean existsByCnaeAndAnualidad(String cnae, Integer anualidad);
}
