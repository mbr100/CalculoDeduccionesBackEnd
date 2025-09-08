package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Retribucion")
public class Retribucion {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long idRetribucion;

    private Long importeRetribucionNoIT;
    private Long importeRetribucionExpecie;
    private Long aportaciones_prevencion_social;
    private Long dietas_viaje_exentas;
    private Long rentas_exentas_190;

    @Transient
    public Long getPercepcionesSalariales() {
        return (importeRetribucionNoIT != null ? importeRetribucionNoIT : 0L)
                + (importeRetribucionExpecie != null ? importeRetribucionExpecie : 0L)
                + (aportaciones_prevencion_social != null ? aportaciones_prevencion_social : 0L)
                + (dietas_viaje_exentas != null ? dietas_viaje_exentas : 0L)
                + (rentas_exentas_190 != null ? rentas_exentas_190 : 0L);
    }
    @OneToOne(mappedBy = "retribucion")
    @JsonBackReference
    private Personal personal;
}
