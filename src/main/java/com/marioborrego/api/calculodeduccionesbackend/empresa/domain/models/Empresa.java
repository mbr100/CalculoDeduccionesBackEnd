package com.marioborrego.api.calculodeduccionesbackend.empresa.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Empleado;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Empresa")
public class Empresa {
    @Id
    @Column(name = "id_empresa")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private int idEmpresa;
    private String nombre;
    private String cif;
    private String direccion;
    private String telefono;
    private String nombreContacto;
    private String emailContacto;
    private Long horasConvenio;
    private String urllogo;
    private String urlWeb;
    private String CNAE; // Código Nacional de Actividad Económica

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Empleado> empleados = new ArrayList<>();
}
