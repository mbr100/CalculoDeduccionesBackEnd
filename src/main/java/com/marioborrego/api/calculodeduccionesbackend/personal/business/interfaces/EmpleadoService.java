package com.marioborrego.api.calculodeduccionesbackend.personal.business.interfaces;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Empleado;

import java.util.List;

public interface EmpleadoService {
    void darDeAltaEmpleado(Empleado empleado);
    void darDeBajaEmpleado(Integer idEmpleado);
    void actualizarEmpleado(Empleado empleado);
    Empleado obtenerEmpleadoPorId(Integer idEmpleado);
    List<Empleado> obtenerTodosLosEmpleados();
    List<Empleado> obtenerTodosLosEmpleadosEmpresa(String nombre);
}
