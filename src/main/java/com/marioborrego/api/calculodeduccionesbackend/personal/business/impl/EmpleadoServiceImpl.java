package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.personal.business.interfaces.EmpleadoService;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Empleado;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoServiceImpl implements EmpleadoService {
    private final EmpleadoRepository empleadoRepository;

    public EmpleadoServiceImpl(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public void darDeAltaEmpleado(Empleado empleado) {
        if (empleadoRepository.getEmpleadosByDNI(empleado.getDNI())) {
            throw new IllegalArgumentException("Ya existe un empleado con el DNI proporcionado.");
        }
        empleadoRepository.save(empleado);
    }

    @Override
    public void darDeBajaEmpleado(Integer idEmpleado) {
        Empleado empleado = empleadoRepository.findById(idEmpleado).orElseThrow(() -> new IllegalArgumentException("Empleado no encontrado con ID: " + idEmpleado));
        empleadoRepository.delete(empleado);
    }

    @Override
    public void actualizarEmpleado(Empleado empleado) {
        if (!empleadoRepository.existsById(empleado.getIdEmpleado())) {
            throw new IllegalArgumentException("Empleado no encontrado con ID: " + empleado.getIdEmpleado());
        }
        if (empleadoRepository.getEmpleadosByDNI(empleado.getDNI())) {
            throw new IllegalArgumentException("Ya existe un empleado con el DNI proporcionado.");
        }
        empleadoRepository.save(empleado);
    }

    @Override
    public Empleado obtenerEmpleadoPorId(Integer idEmpleado) {
        return null;
    }

    @Override
    public List<Empleado> obtenerTodosLosEmpleados() {
        return List.of();
    }

    @Override
    public List<Empleado> obtenerTodosLosEmpleadosEmpresa(String nombre) {
        return List.of();
    }
}
