package com.marioborrego.api.calculodeduccionesbackend.helper;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BasesCotizacion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.HorasPersonal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Retribucion;

import java.time.LocalDate;
import java.util.ArrayList;

public class ValoresDefecto {

    public static Retribucion getRetribucionDefault() {
        return Retribucion.builder()
                .importeRetribucionNoIT(0L)
                .importeRetribucionExpecie(0L)
                .aportaciones_prevencion_social(0L)
                .dietas_viaje_exentas(0L)
                .rentas_exentas_190(0L)
                .build();
    }

    public static BasesCotizacion getBasesCotizacionDefault() {
        return BasesCotizacion.builder()
                .basesCotizacionContingenciasComunesEnero(0L)
                .basesCotizacionContingenciasComunesFebrero(0L)
                .basesCotizacionContingenciasComunesMarzo(0L)
                .basesCotizacionContingenciasComunesAbril(0L)
                .basesCotizacionContingenciasComunesMayo(0L)
                .basesCotizacionContingenciasComunesJunio(0L)
                .basesCotizacionContingenciasComunesJulio(0L)
                .basesCotizacionContingenciasComunesAgosto(0L)
                .basesCotizacionContingenciasComunesSeptiembre(0L)
                .basesCotizacionContingenciasComunesOctubre(0L)
                .basesCotizacionContingenciasComunesNoviembre(0L)
                .basesCotizacionContingenciasComunesDiciembre(0L)
                .build();
    }

    public static HorasPersonal getHorasPersonalDefault(Economico economico) {
        return HorasPersonal.builder()
                .ejercicio(economico.getAnualidad()) // Año actual
                .fechaAltaEjercicio(LocalDate.of(economico.getAnualidad(), 1,1)) // 1 de enero del año actual
                .fechaBajaEjercicio(LocalDate.of(economico.getAnualidad(), 12,31)) // 31 de diciembre del año actual
                .horasConvenioAnual(economico.getHorasConvenio()) // Usar las horas del convenio de la empresa
                .bajas(new ArrayList<>())
                .build();
    }
}
