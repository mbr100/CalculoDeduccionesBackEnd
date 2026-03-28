package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BonificacionesTrabajador;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.BonificacionesTrabajadorRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.BonificacionResultDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BonificacionServiceTest {

    @Mock
    private BonificacionesTrabajadorRepository bonificacionRepository;

    @InjectMocks
    private BonificacionService bonificacionService;

    @Test
    void calcularAhorroBonificaciones_prorrateaYSeparaTipos() {
        Long idPersonal = 7L;
        when(bonificacionRepository.findByPersonalIdPersonaAndAnioFiscal(idPersonal, 2025))
                .thenReturn(List.of(
                        bonificacionInvestigador("40.00", LocalDate.of(2025, 1, 1), LocalDate.of(2025, 6, 30), 2025),
                        otraBonificacion("10.00", "Programa de conciliacion", LocalDate.of(2025, 7, 1), LocalDate.of(2025, 12, 31), 2025)
                ));

        BonificacionResultDTO result = bonificacionService.calcularAhorroBonificaciones(
                idPersonal,
                new BigDecimal("1000.00"),
                2025
        );

        assertThat(result.getAhorroInvestigador()).isEqualByComparingTo("198.36");
        assertThat(result.getAhorroOtrasBonificaciones()).isEqualByComparingTo("50.41");
        assertThat(result.getAhorroTotalAnual()).isEqualByComparingTo("248.77");
        assertThat(result.getDetalles()).hasSize(2);
    }

    @Test
    void validarBonificacion_rechazaPorcentajeNoPermitidoEnInvestigador() {
        BonificacionesTrabajador bonificacion = bonificacionInvestigador(
                "35.00",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                2025
        );

        assertThatThrownBy(() -> bonificacionService.validarBonificacion(bonificacion))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("40%, 45% o 50%");
    }

    @Test
    void validarBonificacion_rechazaOtraSinDescripcion() {
        BonificacionesTrabajador bonificacion = otraBonificacion(
                "15.00",
                "",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 12, 31),
                2025
        );

        assertThatThrownBy(() -> bonificacionService.validarBonificacion(bonificacion))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("descripción es obligatoria");
    }

    @Test
    void validarBonificacion_rechazaPeriodoFueraDelEjercicioFiscal() {
        BonificacionesTrabajador bonificacion = otraBonificacion(
                "8.50",
                "Bonificacion local",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 31),
                2025
        );

        assertThatThrownBy(() -> bonificacionService.validarBonificacion(bonificacion))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("año fiscal 2025");
    }

    private BonificacionesTrabajador bonificacionInvestigador(String porcentaje,
                                                              LocalDate fechaInicio,
                                                              LocalDate fechaFin,
                                                              int anioFiscal) {
        return BonificacionesTrabajador.builder()
                .tipoBonificacion(TiposBonificacion.BONIFICACION_PERSONAL_INVESTIGADOR)
                .porcentajeBonificacion(new BigDecimal(porcentaje))
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .anioFiscal(anioFiscal)
                .build();
    }

    private BonificacionesTrabajador otraBonificacion(String porcentaje,
                                                      String descripcion,
                                                      LocalDate fechaInicio,
                                                      LocalDate fechaFin,
                                                      int anioFiscal) {
        return BonificacionesTrabajador.builder()
                .tipoBonificacion(TiposBonificacion.OTRA_BONIFICACION)
                .porcentajeBonificacion(new BigDecimal(porcentaje))
                .descripcion(descripcion)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .anioFiscal(anioFiscal)
                .build();
    }
}
