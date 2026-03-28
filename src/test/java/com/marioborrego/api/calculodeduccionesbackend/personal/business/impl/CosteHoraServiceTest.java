package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveOcupacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ConfiguracionAnualSS;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TarifaPrimasCnae;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveOcupacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ConfiguracionAnualSSRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.TarifaPrimasCnaeRepository;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BasesCotizacion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.CosteHoraPersonal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.HorasPersonal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Personal;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.Retribucion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PeriodoContratoRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PersonalRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.presentation.dto.bonificaciones.BonificacionResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CosteHoraServiceTest {

    @Mock
    private PersonalRepository personalRepository;

    @Mock
    private ConfiguracionAnualSSRepository configuracionAnualSSRepository;

    @Mock
    private TarifaPrimasCnaeRepository tarifaPrimasCnaeRepository;

    @Mock
    private ClaveOcupacionRepository claveOcupacionRepository;

    @Mock
    private BonificacionService bonificacionService;

    private CosteHoraService costeHoraService;

    private PeriodoContratoRepository periodoContratoRepository;

    @BeforeEach
    void setUp() {
        costeHoraService = new CosteHoraService(
                personalRepository,
                configuracionAnualSSRepository,
                tarifaPrimasCnaeRepository,
                claveOcupacionRepository,
                bonificacionService,
                periodoContratoRepository
        );
        when(personalRepository.save(any(Personal.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void calcularCosteHoraEconomico_usaTarifaCnaeYDesempleoIndefinido() {
        Economico economico = economico(1L, 6201L, 2024L);
        Personal personal = personal(
                11L,
                economico,
                true,
                null,
                retribucion(40000L),
                bases(3000L),
                horas(1800L)
        );
        economico.setPersonal(List.of(personal));

        when(configuracionAnualSSRepository.findByAnio(2024)).thenReturn(Optional.of(configuracion("0.58")));
        when(tarifaPrimasCnaeRepository.findByCnaeAndAnio("6201", 2024)).thenReturn(Optional.of(tarifa("1.00")));
        when(bonificacionService.calcularAhorroBonificaciones(eq(11L), eq(new BigDecimal("8496.000000")), eq(2024)))
                .thenReturn(resultadoBonificacion("0.00", "0.00", "0.00"));

        costeHoraService.calcularCosteHoraEconomico(economico);

        CosteHoraPersonal costeHora = personal.getCosteHoraPersonal();
        assertThat(costeHora.getOrigenTipoATEP()).isEqualTo("CUADRO_I_CNAE_6201");
        assertThat(costeHora.getTipoATEPAplicado()).isEqualByComparingTo("1.00");
        assertThat(costeHora.getCuotaDesempleo()).isEqualByComparingTo("1980.000000");
        assertThat(costeHora.getSsEmpresaBruta()).isEqualByComparingTo("11332.800000");
        assertThat(costeHora.getCosteSS()).isEqualByComparingTo("11332.800000");
        assertThat(costeHora.getCosteHora()).isEqualByComparingTo("28.518222");
    }

    @Test
    void calcularCosteHoraEconomico_usaClaveOcupacionYBonificaciones() {
        Economico economico = economico(2L, 8610L, 2025L);
        Personal personal = personal(
                21L,
                economico,
                false,
                "h",
                retribucion(30000L),
                bases(2000L),
                horas(1500L)
        );
        economico.setPersonal(List.of(personal));

        when(configuracionAnualSSRepository.findByAnio(2025)).thenReturn(Optional.of(configuracion("0.67")));
        when(claveOcupacionRepository.findByClaveAndActivaTrue("h")).thenReturn(Optional.of(claveOcupacion("3.25")));
        when(bonificacionService.calcularAhorroBonificaciones(eq(21L), eq(new BigDecimal("5664.000000")), eq(2025)))
                .thenReturn(resultadoBonificacion("1000.00", "800.00", "200.00"));

        costeHoraService.calcularCosteHoraEconomico(economico);

        CosteHoraPersonal costeHora = personal.getCosteHoraPersonal();
        assertThat(costeHora.getOrigenTipoATEP()).isEqualTo("CUADRO_II_CLAVE_H");
        assertThat(costeHora.getTipoATEPAplicado()).isEqualByComparingTo("3.25");
        assertThat(costeHora.getCuotaCC()).isEqualByComparingTo("4664.000000");
        assertThat(costeHora.getCuotaDesempleo()).isEqualByComparingTo("1608.000000");
        assertThat(costeHora.getAhorroBonificaciones()).isEqualByComparingTo("1000.00");
        assertThat(costeHora.getCosteSS()).isEqualByComparingTo("7404.800000");
        assertThat(costeHora.getCosteHora()).isEqualByComparingTo("24.936533");
    }

    @Test
    void calcularCosteHoraEconomico_noPermiteSeguridadSocialNetaNegativa() {
        Economico economico = economico(3L, 6201L, 2024L);
        Personal personal = personal(
                31L,
                economico,
                true,
                null,
                retribucion(20000L),
                bases(1000L),
                horas(1000L)
        );
        economico.setPersonal(List.of(personal));

        when(configuracionAnualSSRepository.findByAnio(2024)).thenReturn(Optional.of(configuracion("0.58")));
        when(tarifaPrimasCnaeRepository.findByCnaeAndAnio("6201", 2024)).thenReturn(Optional.of(tarifa("1.00")));
        when(bonificacionService.calcularAhorroBonificaciones(eq(31L), eq(new BigDecimal("2832.000000")), eq(2024)))
                .thenReturn(resultadoBonificacion("99999.99", "99999.99", "0.00"));

        costeHoraService.calcularCosteHoraEconomico(economico);

        CosteHoraPersonal costeHora = personal.getCosteHoraPersonal();
        assertThat(costeHora.getCuotaCC()).isEqualByComparingTo("0");
        assertThat(costeHora.getCosteSS()).isEqualByComparingTo("0");
        assertThat(costeHora.getCosteHora()).isEqualByComparingTo("20.000000");
    }

    @Test
    void calcularCosteHoraEconomico_devuelveCeroSiNoHayHorasEfectivas() {
        Economico economico = economico(4L, 6201L, 2026L);
        Personal personal = personal(
                41L,
                economico,
                true,
                null,
                retribucion(18000L),
                bases(1200L),
                horas(0L)
        );
        economico.setPersonal(List.of(personal));

        when(configuracionAnualSSRepository.findByAnio(2026)).thenReturn(Optional.of(configuracion("0.75")));
        when(tarifaPrimasCnaeRepository.findByCnaeAndAnio("6201", 2026)).thenReturn(Optional.of(tarifa("1.00")));
        when(bonificacionService.calcularAhorroBonificaciones(eq(41L), eq(new BigDecimal("3398.400000")), eq(2026)))
                .thenReturn(resultadoBonificacion("0.00", "0.00", "0.00"));

        costeHoraService.calcularCosteHoraEconomico(economico);

        assertThat(personal.getCosteHoraPersonal().getCosteHora()).isEqualByComparingTo("0");
        assertThat(personal.getCosteHoraPersonal().getHorasMaximas()).isEqualByComparingTo("0");
    }

    private Economico economico(Long id, Long cnae, Long anualidad) {
        Economico economico = new Economico();
        economico.setIdEconomico(id);
        economico.setCNAE(cnae);
        economico.setAnualidad(anualidad);
        economico.setHorasConvenio(1700L);
        return economico;
    }

    private Personal personal(Long idPersona,
                              Economico economico,
                              boolean esContratoIndefinido,
                              String claveOcupacion,
                              Retribucion retribucion,
                              BasesCotizacion basesCotizacion,
                              HorasPersonal horasPersonal) {
        Personal personal = new Personal();
        personal.setIdPersona(idPersona);
        personal.setEconomico(economico);
        personal.setEsContratoIndefinido(esContratoIndefinido);
        personal.setClaveOcupacion(claveOcupacion);
        personal.setRetribucion(retribucion);
        personal.setBasesCotizacion(basesCotizacion);
        personal.setHorasPersonal(horasPersonal);
        personal.setCosteHoraPersonal(new CosteHoraPersonal());

        retribucion.setPersonal(personal);
        basesCotizacion.setPersona(personal);
        horasPersonal.setPersonal(personal);
        personal.getCosteHoraPersonal().setPersonal(personal);
        return personal;
    }

    private Retribucion retribucion(Long importeNoIt) {
        Retribucion retribucion = new Retribucion();
        retribucion.setImporteRetribucionNoIT(importeNoIt);
        retribucion.setImporteRetribucionExpecie(0L);
        retribucion.setAportaciones_prevencion_social(0L);
        retribucion.setDietas_viaje_exentas(0L);
        retribucion.setRentas_exentas_190(0L);
        return retribucion;
    }

    private BasesCotizacion bases(Long baseMensual) {
        BasesCotizacion basesCotizacion = new BasesCotizacion();
        basesCotizacion.setBasesCotizacionContingenciasComunesEnero(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesFebrero(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesMarzo(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesAbril(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesMayo(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesJunio(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesJulio(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesAgosto(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesSeptiembre(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesOctubre(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesNoviembre(baseMensual);
        basesCotizacion.setBasesCotizacionContingenciasComunesDiciembre(baseMensual);
        return basesCotizacion;
    }

    private HorasPersonal horas(Long horasMaximas) {
        HorasPersonal horasPersonal = new HorasPersonal();
        horasPersonal.setHorasMaximasAnuales(horasMaximas);
        return horasPersonal;
    }

    private ConfiguracionAnualSS configuracion(String meiEmpresa) {
        return ConfiguracionAnualSS.builder()
                .anio(2025)
                .ccEmpresa(new BigDecimal("23.60"))
                .ccTrabajador(new BigDecimal("4.70"))
                .desempleoEmpresaIndefinido(new BigDecimal("5.50"))
                .desempleoEmpresaTemporal(new BigDecimal("6.70"))
                .fogasa(new BigDecimal("0.20"))
                .fpEmpresa(new BigDecimal("0.60"))
                .meiEmpresa(new BigDecimal(meiEmpresa))
                .meiTrabajador(new BigDecimal("0.00"))
                .build();
    }

    private TarifaPrimasCnae tarifa(String tipoTotal) {
        return TarifaPrimasCnae.builder()
                .cnae("6201")
                .anio(2025)
                .descripcion("Demo")
                .tipoIt(new BigDecimal("0.65"))
                .tipoIms(new BigDecimal("0.35"))
                .tipoTotal(new BigDecimal(tipoTotal))
                .versionCnae("2009")
                .build();
    }

    private ClaveOcupacion claveOcupacion(String tipoTotal) {
        return ClaveOcupacion.builder()
                .clave("h")
                .descripcion("Seguridad")
                .tipoIt(new BigDecimal("1.95"))
                .tipoIms(new BigDecimal("1.30"))
                .tipoTotal(new BigDecimal(tipoTotal))
                .activa(true)
                .build();
    }

    private BonificacionResultDTO resultadoBonificacion(String total, String investigador, String otras) {
        return BonificacionResultDTO.builder()
                .ahorroTotalAnual(new BigDecimal(total))
                .ahorroInvestigador(new BigDecimal(investigador))
                .ahorroOtrasBonificaciones(new BigDecimal(otras))
                .detalles(List.of())
                .build();
    }
}
