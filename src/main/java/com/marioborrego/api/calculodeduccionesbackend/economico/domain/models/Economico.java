package com.marioborrego.api.calculodeduccionesbackend.economico.domain.models;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.Proyecto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "empresa_economico")
public class Economico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_economico", nullable = false, unique = true)
    private Long idEconomico;
    private String nombre;
    private String cif;
    private String direccion;
    private String telefono;
    private String nombreContacto;
    private String emailContacto;
    private Long horasConvenio;
    private String urllogo;
    private String urlWeb;
    private Long CNAE;
    private Long anualidad;
    private boolean esPyme;
    private boolean activo;

    @Column(columnDefinition = "TEXT")
    private String presentacionEmpresa;

    @Column(columnDefinition = "TEXT")
    private String descripcionIDI;

    @OneToMany(mappedBy = "economico", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Personal> personal = new ArrayList<>();


    @OneToMany(mappedBy = "economico", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Proyecto> proyectos = new HashSet<>();
}
