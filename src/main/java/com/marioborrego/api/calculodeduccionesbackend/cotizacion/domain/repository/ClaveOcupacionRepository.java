package com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveOcupacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClaveOcupacionRepository extends JpaRepository<ClaveOcupacion, String> {

    Optional<ClaveOcupacion> findByClaveAndActivaTrue(String clave);

    List<ClaveOcupacion> findByActivaTrue();
}
