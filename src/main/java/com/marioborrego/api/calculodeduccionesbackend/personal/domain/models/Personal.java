package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.proyecto.domain.models.ProyectoPersonal;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Personal")
public class Personal {

    @Id
    @Column(name = "id_personal", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPersona;

    private String nombre;
    private String apellidos;
    private String dni;
    private String puesto;
    private String departamento;
    private String titulacion1;
    private String titulacion2;
    private String titulacion3;
    private String titulacion4;
    private boolean esPersonalInvestigador;

    @ManyToOne
    @Builder.Default
    @JoinColumn(name = "id_economico", nullable = false)
    private Economico economico = new Economico();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_retribucion")
    @Builder.Default
    @JsonManagedReference
    private Retribucion retribucion = new Retribucion();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_bases_cotizacion")
    @Builder.Default
    private BasesCotizacion basesCotizacion = new BasesCotizacion();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_horas_empleado")
    @Builder.Default
    private HorasPersonal horasPersonal = new HorasPersonal();

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BajaLaboral> bajasLaborales = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_bonificaciones_trabajador")
    @Builder.Default
    private BonificacionesTrabajador bonificacionesTrabajador = new BonificacionesTrabajador();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_coste_hora_personal")
    @Builder.Default
    private CosteHoraPersonal costeHoraPersonal = new CosteHoraPersonal();

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<ProyectoPersonal> proyectoPersonales = new HashSet<>();
}
