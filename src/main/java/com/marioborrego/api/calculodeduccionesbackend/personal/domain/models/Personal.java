package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private int idPersona;
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

    @ManyToOne()
    @JoinColumn(name = "id_economico", nullable = false)
    private Economico economico;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_retribucion")
    @JsonManagedReference
    private Retribucion retribucion;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_bases_cotizacion")
    private BasesCotizacion basesCotizacion;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_horas_empleado")
    private HorasPersonal horasPersonal;

    @OneToMany(mappedBy = "personal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BajaLaboral> bajasLaborales = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_bonificaciones_trabajador")
    private BonificacionesTrabajador bonificacionesTrabajador;
}
