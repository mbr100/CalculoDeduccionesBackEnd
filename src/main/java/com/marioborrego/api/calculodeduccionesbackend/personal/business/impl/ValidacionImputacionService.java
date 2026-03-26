package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.BonificacionesTrabajador;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.BonificacionesTrabajadorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidacionImputacionService {

    private final BonificacionesTrabajadorRepository bonificacionRepository;
    private final EconomicoRepository economicoRepository;

    public ValidacionImputacionService(BonificacionesTrabajadorRepository bonificacionRepository,
                                       EconomicoRepository economicoRepository) {
        this.bonificacionRepository = bonificacionRepository;
        this.economicoRepository = economicoRepository;
    }

    /**
     * Valida si se pueden imputar horas I+D+i a un trabajador.
     *
     * REGLA: Si el trabajador tiene bonificación de personal investigador,
     * la empresa DEBE tener el sello de PYME innovadora para poder
     * imputar horas del mismo trabajador a deducciones I+D+i.
     *
     * @return null si es válido, o mensaje de error si está bloqueado
     */
    public String validarImputacionHoras(Long idPersonal, Integer anioFiscal, Long idEconomico) {
        // 1. Tiene bonificación de personal investigador este año?
        List<BonificacionesTrabajador> bonifInvestigador = bonificacionRepository
                .findByPersonalIdPersonaAndAnioFiscalAndTipoBonificacion(
                        idPersonal, anioFiscal, TiposBonificacion.BONIFICACION_PERSONAL_INVESTIGADOR);

        if (bonifInvestigador.isEmpty()) {
            return null; // No tiene bonificación de investigador → puede imputar sin restricción
        }

        // 2. Tiene bonificación de investigador → la empresa tiene sello PYME?
        Economico economico = economicoRepository.findById(idEconomico)
                .orElseThrow(() -> new RuntimeException("No se encontró económico con ID: " + idEconomico));

        if (economico.isSelloPymeInnovadora()) {
            return null; // Tiene sello → compatibilidad plena → puede imputar
        }

        // 3. No tiene sello → BLOQUEO
        return "No se pueden imputar horas I+D+i a este trabajador. "
                + "Tiene bonificación de personal investigador activa en " + anioFiscal
                + " y la empresa NO tiene el sello de PYME innovadora. "
                + "Sin el sello, no es posible aplicar simultáneamente la bonificación SS "
                + "y la deducción fiscal I+D+i sobre el mismo investigador. "
                + "Opciones: (1) Obtener el sello PYME innovadora, "
                + "(2) Eliminar la bonificación de investigador de este trabajador, "
                + "o (3) No imputar horas I+D+i a este trabajador.";
    }
}
