package com.marioborrego.api.calculodeduccionesbackend.personal.domain.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HorasPersonalTest {

    @Test
    void actualizarHorasMaximasAnuales_ignoraBajasSinHorasCalculadasTodavia() {
        HorasPersonal horasPersonal = HorasPersonal.builder()
                .ejercicio(2024)
                .fechaAltaEjercicio(LocalDate.of(2024, 1, 1))
                .fechaBajaEjercicio(LocalDate.of(2024, 12, 31))
                .horasConvenioAnual(1740L)
                .bajas(List.of(BajaLaboral.builder()
                        .fechaInicio(LocalDate.of(2024, 2, 1))
                        .fechaFin(LocalDate.of(2024, 2, 10))
                        .build()))
                .build();

        horasPersonal.actualizarHorasMaximasAnuales();

        assertThat(horasPersonal.getHorasMaximasAnuales()).isEqualTo(1740L);
    }
}
