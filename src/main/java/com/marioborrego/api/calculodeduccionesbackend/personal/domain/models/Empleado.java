package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.empresa.domain.models.Empresa;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Empleado")
public class Empleado {
    @Id
    @Column(name = "id_empleado")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEmpleado;
    private String nombre;
    private String apellidos;
    private String DNI;
    private String direccion;
    private String telefono;
    private String email;
    private String urlFoto;

    @ManyToOne()
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_retribucion")
    private Retribucion retribucion;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_bases_cotizacion")
    private BasesCotizacion basesCotizacion;

    @OneToMany(mappedBy = "empleado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BajaLaboral> bajasLaborales = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_horas_empleado")
    private HorasEmpleado horasEmpleado;
}
