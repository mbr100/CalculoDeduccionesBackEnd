package com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.resumenCostes;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenCostePersonalDTO {
    private Long idPersonal;
    private String nombre;
    private String dni;
    private String puesto;
    private String titulacion;
    private String departamento;
    private Long idCosteHoraPersonal;
    private BigDecimal retribucionTotal;
    private BigDecimal costeSS;
    private BigDecimal horasAlta;
    // Campo legado mantenido por compatibilidad con clientes actuales.
    private BigDecimal horasMaximas;
    private BigDecimal horasEfectivas;
    private BigDecimal horasBaja;
    private BigDecimal costeHora;

    // Desglose de cuotas SS empresa
    private BigDecimal cuotaCC;
    private BigDecimal cuotaATEP;
    private BigDecimal cuotaDesempleo;
    private BigDecimal cuotaFogasa;
    private BigDecimal cuotaFP;
    private BigDecimal cuotaMEI;

    // Trazabilidad AT/EP
    private BigDecimal tipoATEPAplicado;
    private String origenTipoATEP;

    // Bonificaciones SS
    private BigDecimal ssEmpresaBruta;
    private BigDecimal ahorroBonificaciones;
    private BigDecimal ahorroInvestigador;
    private BigDecimal ahorroOtrasBonificaciones;
}
