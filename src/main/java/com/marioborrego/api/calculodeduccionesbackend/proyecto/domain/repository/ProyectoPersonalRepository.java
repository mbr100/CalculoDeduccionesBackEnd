package com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.repository;

import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.ProyectoPersonal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProyectoPersonalRepository extends JpaRepository<ProyectoPersonal, Long> {

}