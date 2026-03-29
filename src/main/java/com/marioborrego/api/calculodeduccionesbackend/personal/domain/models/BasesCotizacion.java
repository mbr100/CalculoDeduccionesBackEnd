package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "BasesCotizacion")
public class BasesCotizacion {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id_baseCotizacion;

    private Long basesCotizacionContingenciasComunesEnero;
    private Long basesCotizacionContingenciasComunesFebrero;
    private Long basesCotizacionContingenciasComunesMarzo;
    private Long basesCotizacionContingenciasComunesAbril;
    private Long basesCotizacionContingenciasComunesMayo;
    private Long basesCotizacionContingenciasComunesJunio;
    private Long basesCotizacionContingenciasComunesJulio;
    private Long basesCotizacionContingenciasComunesAgosto;
    private Long basesCotizacionContingenciasComunesSeptiembre;
    private Long basesCotizacionContingenciasComunesOctubre;
    private Long basesCotizacionContingenciasComunesNoviembre;
    private Long basesCotizacionContingenciasComunesDiciembre;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_periodo_contrato")
    private PeriodoContrato periodoContrato;

    @Transient
    public Long getBasesCotizacionContingenciasComunesAnual() {
        long total = 0L;
        for (int mes = 1; mes <= 12; mes++) {
            total += getBaseCotizacionContingenciasComunesMes(mes);
        }
        return total;
    }

    @Transient
    public Long getBaseCotizacionContingenciasComunesMes(int mes) {
        return switch (mes) {
            case 1 -> valorOrZero(basesCotizacionContingenciasComunesEnero);
            case 2 -> valorOrZero(basesCotizacionContingenciasComunesFebrero);
            case 3 -> valorOrZero(basesCotizacionContingenciasComunesMarzo);
            case 4 -> valorOrZero(basesCotizacionContingenciasComunesAbril);
            case 5 -> valorOrZero(basesCotizacionContingenciasComunesMayo);
            case 6 -> valorOrZero(basesCotizacionContingenciasComunesJunio);
            case 7 -> valorOrZero(basesCotizacionContingenciasComunesJulio);
            case 8 -> valorOrZero(basesCotizacionContingenciasComunesAgosto);
            case 9 -> valorOrZero(basesCotizacionContingenciasComunesSeptiembre);
            case 10 -> valorOrZero(basesCotizacionContingenciasComunesOctubre);
            case 11 -> valorOrZero(basesCotizacionContingenciasComunesNoviembre);
            case 12 -> valorOrZero(basesCotizacionContingenciasComunesDiciembre);
            default -> throw new IllegalArgumentException("Mes no válido: " + mes);
        };
    }

    /**
     * Suma las bases de los meses que caen dentro del rango de fechas indicado (inclusive).
     * Los meses se consideran completos: un mes se incluye si el periodo lo cubre al menos parcialmente.
     */
    @Transient
    public Long getBasesCotizacionEnRango(LocalDate desde, LocalDate hasta, int anioFiscal) {
        long total = 0;
        for (int m = 1; m <= 12; m++) {
            LocalDate inicioMes = LocalDate.of(anioFiscal, m, 1);
            LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
            // El mes se incluye si hay solapamiento con el rango del periodo
            if (!inicioMes.isAfter(hasta) && !finMes.isBefore(desde)) {
                total += getBaseCotizacionContingenciasComunesMes(m);
            }
        }
        return total;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id_personal")
    private Personal persona;

    /**
     * Pone a 0 las bases de los meses que quedan fuera del rango [desde, hasta] dentro del año fiscal.
     * Un mes se considera fuera si NO hay solapamiento entre [inicioMes, finMes] y [desde, hasta].
     */
    public void anularBasesForaDeRango(LocalDate desde, LocalDate hasta, int anioFiscal) {
        for (int m = 1; m <= 12; m++) {
            LocalDate inicioMes = LocalDate.of(anioFiscal, m, 1);
            LocalDate finMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());
            if (inicioMes.isAfter(hasta) || finMes.isBefore(desde)) {
                setBaseCotizacionContingenciasComunesMes(m, 0L);
            }
        }
    }

    public void setBaseCotizacionContingenciasComunesMes(int mes, Long valor) {
        switch (mes) {
            case 1 -> basesCotizacionContingenciasComunesEnero = valor;
            case 2 -> basesCotizacionContingenciasComunesFebrero = valor;
            case 3 -> basesCotizacionContingenciasComunesMarzo = valor;
            case 4 -> basesCotizacionContingenciasComunesAbril = valor;
            case 5 -> basesCotizacionContingenciasComunesMayo = valor;
            case 6 -> basesCotizacionContingenciasComunesJunio = valor;
            case 7 -> basesCotizacionContingenciasComunesJulio = valor;
            case 8 -> basesCotizacionContingenciasComunesAgosto = valor;
            case 9 -> basesCotizacionContingenciasComunesSeptiembre = valor;
            case 10 -> basesCotizacionContingenciasComunesOctubre = valor;
            case 11 -> basesCotizacionContingenciasComunesNoviembre = valor;
            case 12 -> basesCotizacionContingenciasComunesDiciembre = valor;
            default -> throw new IllegalArgumentException("Mes no válido: " + mes);
        }
    }

    private long valorOrZero(Long valor) {
        return valor != null ? valor : 0L;
    }
}
