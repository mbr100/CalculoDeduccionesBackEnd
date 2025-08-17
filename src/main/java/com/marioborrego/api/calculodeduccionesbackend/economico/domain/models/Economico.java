package com.marioborrego.api.calculodeduccionesbackend.economico.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Empleado;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "empresa_economico")
public class Economico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_economico", nullable = false, unique = true)
    private int idEconomico;
    private String nombre;
    private String cif;
    private String direccion;
    private String telefono;
    private String nombreContacto;
    private String emailContacto;
    private Long horasConvenio;
    private String urllogo;
    private String urlWeb;
    private int CNAE;
    private int anualidad;
    private boolean esPyme;
    private boolean activo;

    @Column(columnDefinition = "TEXT")
    private String presentacionEmpresa;

    @Column(columnDefinition = "TEXT")
    private String descripcionIDI;

    @OneToMany(mappedBy = "economico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Empleado> empleados = new ArrayList<>();
}
