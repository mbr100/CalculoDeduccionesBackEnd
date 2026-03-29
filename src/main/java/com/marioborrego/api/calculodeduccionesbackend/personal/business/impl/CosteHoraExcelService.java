package com.marioborrego.api.calculodeduccionesbackend.personal.business.impl;

import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveContrato;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ClaveOcupacion;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.ConfiguracionAnualSS;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.models.TarifaPrimasCnae;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ClaveOcupacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.ConfiguracionAnualSSRepository;
import com.marioborrego.api.calculodeduccionesbackend.cotizacion.domain.repository.TarifaPrimasCnaeRepository;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.models.Economico;
import com.marioborrego.api.calculodeduccionesbackend.economico.domain.repository.EconomicoRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.*;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.NaturalezaContrato;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.models.enums.TiposBonificacion;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.BasesCotizacionRepository;
import com.marioborrego.api.calculodeduccionesbackend.personal.domain.repository.PeriodoContratoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CosteHoraExcelService {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final int SCALE = 6;
    private static final String[] MESES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final EconomicoRepository economicoRepository;
    private final PeriodoContratoRepository periodoContratoRepository;
    private final BasesCotizacionRepository basesCotizacionRepository;
    private final ConfiguracionAnualSSRepository configuracionAnualSSRepository;
    private final ClaveOcupacionRepository claveOcupacionRepository;
    private final TarifaPrimasCnaeRepository tarifaPrimasCnaeRepository;

    public CosteHoraExcelService(EconomicoRepository economicoRepository,
                                  PeriodoContratoRepository periodoContratoRepository,
                                  BasesCotizacionRepository basesCotizacionRepository,
                                  ConfiguracionAnualSSRepository configuracionAnualSSRepository,
                                  ClaveOcupacionRepository claveOcupacionRepository,
                                  TarifaPrimasCnaeRepository tarifaPrimasCnaeRepository) {
        this.economicoRepository = economicoRepository;
        this.periodoContratoRepository = periodoContratoRepository;
        this.basesCotizacionRepository = basesCotizacionRepository;
        this.configuracionAnualSSRepository = configuracionAnualSSRepository;
        this.claveOcupacionRepository = claveOcupacionRepository;
        this.tarifaPrimasCnaeRepository = tarifaPrimasCnaeRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generarExcel(Long idEconomico) throws IOException {
        Economico economico = economicoRepository.findById(idEconomico)
                .orElseThrow(() -> new RuntimeException("No se encontró el económico con ID: " + idEconomico));

        int anualidad = Math.toIntExact(economico.getAnualidad());
        int diasDelAnio = Year.of(anualidad).length();
        String cnae = String.valueOf(economico.getCNAE());

        ConfiguracionAnualSS config = configuracionAnualSSRepository.findByAnio(anualidad)
                .orElseThrow(() -> new RuntimeException("No se encontró configuración SS para el año " + anualidad));

        List<Personal> personalList = economico.getPersonal();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            // Estilos
            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle moneyStyle = crearEstiloMoney(workbook);
            CellStyle percentStyle = crearEstiloPercent(workbook);
            CellStyle dateStyle = crearEstiloDate(workbook);
            CellStyle numberStyle = crearEstiloNumber(workbook);
            CellStyle resultStyle = crearEstiloResult(workbook);
            CellStyle subHeaderStyle = crearEstiloSubHeader(workbook);

            crearHojaDetalle(workbook, personalList, anualidad, diasDelAnio, cnae, config,
                    headerStyle, subHeaderStyle, moneyStyle, dateStyle, numberStyle, resultStyle);
            crearHojaBajas(workbook, personalList, anualidad, diasDelAnio,
                    headerStyle, dateStyle, numberStyle);
            crearHojaSS(workbook, personalList, anualidad, diasDelAnio, cnae, config,
                    headerStyle, subHeaderStyle, moneyStyle, percentStyle, numberStyle);
            crearHojaAuditoriaBonificaciones(workbook, personalList, anualidad,
                    headerStyle, subHeaderStyle, moneyStyle, percentStyle, dateStyle);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    // ==================== HOJA 1: DETALLE ====================

    private void crearHojaDetalle(XSSFWorkbook workbook, List<Personal> personalList,
                                   int anualidad, int diasDelAnio, String cnae,
                                   ConfiguracionAnualSS config,
                                   CellStyle headerStyle, CellStyle subHeaderStyle,
                                   CellStyle moneyStyle, CellStyle dateStyle,
                                   CellStyle numberStyle, CellStyle resultStyle) {
        Sheet sheet = workbook.createSheet("Detalle Coste Hora");

        // Cabeceras
        String[] headers = {
                // Datos personales (0-1)
                "Nombre", "DNI",
                // Retribuciones (2-6)
                "Retrib. No IT", "Retrib. Especie", "Aportaciones Prev.", "Dietas Viaje", "Rentas Exentas",
                // Contrato (7-12)
                "Contrato", "Naturaleza", "F. Alta", "F. Baja", "% Jornada", "H. Convenio",
                // BCC mensual (13-24)
                "BCC Ene", "BCC Feb", "BCC Mar", "BCC Abr", "BCC May", "BCC Jun",
                "BCC Jul", "BCC Ago", "BCC Sep", "BCC Oct", "BCC Nov", "BCC Dic",
                // Horas periodo (25-26)
                "H. Periodo", "H. Baja Periodo",
                // Resultados (27-38)
                "Retrib. Total", "BCC Total", "Coef. SS", "H. Alta", "H. Baja Total",
                "Tipos Bonificación", "Ahorro Bonif. Total", "Ahorro Bonif. Investigador",
                "Ahorro Otras Bonif.", "SS Bruta", "SS Neta", "Coste/Hora"
        };

        // Fila de grupo
        Row groupRow = sheet.createRow(0);
        crearCeldaGrupo(groupRow, 0, "Datos", headerStyle);
        crearCeldaGrupo(groupRow, 2, "Retribuciones", headerStyle);
        crearCeldaGrupo(groupRow, 7, "Contrato", headerStyle);
        crearCeldaGrupo(groupRow, 13, "Bases Cotización CC", headerStyle);
        crearCeldaGrupo(groupRow, 25, "Horas Periodo", headerStyle);
        crearCeldaGrupo(groupRow, 27, "Resultados", headerStyle);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 6));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, 12));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 13, 24));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 25, 26));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 27, 38));

        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(subHeaderStyle);
        }

        int rowIdx = 2;
        for (Personal personal : personalList) {
            List<PeriodoContrato> periodos = periodoContratoRepository
                    .findByPersonalIdPersonaAndAnioFiscalOrderByFechaAltaAsc(personal.getIdPersona(), anualidad);

            Retribucion retrib = personal.getRetribucion();
            CosteHoraPersonal ch = personal.getCosteHoraPersonal();

            int numPeriodos = Math.max(periodos.size(), 1);
            int firstRow = rowIdx;

            for (int p = 0; p < numPeriodos; p++) {
                Row row = sheet.createRow(rowIdx);

                if (p == 0) {
                    // Datos personales - solo en la primera fila
                    row.createCell(0).setCellValue(personal.getNombre() + " " +
                            (personal.getApellidos() != null ? personal.getApellidos() : ""));
                    row.createCell(1).setCellValue(personal.getDni() != null ? personal.getDni() : "");

                    // Retribuciones - solo en la primera fila
                    setCellMoney(row, 2, retrib != null ? retrib.getImporteRetribucionNoIT() : null, moneyStyle);
                    setCellMoney(row, 3, retrib != null ? retrib.getImporteRetribucionExpecie() : null, moneyStyle);
                    setCellMoney(row, 4, retrib != null ? retrib.getAportaciones_prevencion_social() : null, moneyStyle);
                    setCellMoney(row, 5, retrib != null ? retrib.getDietas_viaje_exentas() : null, moneyStyle);
                    setCellMoney(row, 6, retrib != null ? retrib.getRentas_exentas_190() : null, moneyStyle);
                }

                // Contrato y BCC - por cada periodo
                if (!periodos.isEmpty() && p < periodos.size()) {
                    PeriodoContrato periodo = periodos.get(p);
                    ClaveContrato clave = periodo.getClaveContrato();

                    row.createCell(7).setCellValue(clave.getClave() + " - " + clave.getDescripcion());
                    row.createCell(8).setCellValue(clave.getNaturaleza().name());
                    setCellDate(row, 9, periodo.getFechaAlta(), dateStyle);
                    setCellDate(row, 10, periodo.getFechaBaja(), dateStyle);
                    setCellNumber(row, 11, periodo.getPorcentajeJornada() != null ?
                            periodo.getPorcentajeJornada().doubleValue() : null, numberStyle);
                    setCellNumber(row, 12, periodo.getHorasConvenio() != null ?
                            periodo.getHorasConvenio().doubleValue() : null, numberStyle);

                    // BCC de este periodo
                    BasesCotizacion bases = basesCotizacionRepository.findByPeriodoContratoId(periodo.getId())
                            .orElse(null);
                    for (int m = 1; m <= 12; m++) {
                        Long valorMes = bases != null ? bases.getBaseCotizacionContingenciasComunesMes(m) : 0L;
                        setCellMoney(row, 12 + m, valorMes, moneyStyle);
                    }

                    // Horas de este periodo
                    LocalDate inicioAnio = LocalDate.of(anualidad, 1, 1);
                    LocalDate finAnio = LocalDate.of(anualidad, 12, 31);
                    LocalDate fechaInicio = periodo.getFechaAlta().isBefore(inicioAnio) ? inicioAnio : periodo.getFechaAlta();
                    LocalDate fechaFin = periodo.getFechaBaja() != null
                            ? (periodo.getFechaBaja().isAfter(finAnio) ? finAnio : periodo.getFechaBaja())
                            : finAnio;

                    if (!fechaFin.isBefore(fechaInicio)) {
                        long diasPeriodo = ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
                        BigDecimal horasPeriodo = BigDecimal.valueOf(periodo.getHorasConvenio())
                                .multiply(BigDecimal.valueOf(diasPeriodo))
                                .divide(BigDecimal.valueOf(diasDelAnio), SCALE, RoundingMode.HALF_UP)
                                .multiply(periodo.getPorcentajeJornada())
                                .divide(CIEN, SCALE, RoundingMode.HALF_UP);
                        setCellNumber(row, 25, horasPeriodo.doubleValue(), numberStyle);

                        // Horas de baja para este periodo
                        BigDecimal horasBajaPeriodo = BigDecimal.ZERO;
                        for (BajaLaboral baja : personal.getBajasLaborales()) {
                            LocalDate fib = baja.getFechaInicio().isBefore(inicioAnio) ? inicioAnio : baja.getFechaInicio();
                            LocalDate ffb = baja.getFechaFin() == null || baja.getFechaFin().isAfter(finAnio) ? finAnio : baja.getFechaFin();
                            if (!fib.isAfter(ffb)) {
                                LocalDate overlapInicio = fechaInicio.isAfter(fib) ? fechaInicio : fib;
                                LocalDate overlapFin = fechaFin.isBefore(ffb) ? fechaFin : ffb;
                                if (!overlapInicio.isAfter(overlapFin)) {
                                    long diasSolapados = ChronoUnit.DAYS.between(overlapInicio, overlapFin) + 1;
                                    BigDecimal horasDia = BigDecimal.valueOf(periodo.getHorasConvenio())
                                            .divide(BigDecimal.valueOf(diasDelAnio), SCALE, RoundingMode.HALF_UP);
                                    BigDecimal hbp = horasDia.multiply(BigDecimal.valueOf(diasSolapados))
                                            .multiply(periodo.getPorcentajeJornada())
                                            .divide(CIEN, SCALE, RoundingMode.HALF_UP);
                                    horasBajaPeriodo = horasBajaPeriodo.add(hbp);
                                }
                            }
                        }
                        setCellNumber(row, 26, horasBajaPeriodo.doubleValue(), numberStyle);
                    }
                }

                // Resultados - solo en la primera fila
                if (p == 0 && ch != null) {
                    List<BonificacionesTrabajador> bonificacionesVigentes = obtenerBonificacionesVigentes(personal, anualidad);
                    setCellMoney(row, 27, ch.getRetribucionTotal(), resultStyle);

                    // BCC Total: suma de todas las bases de todos los periodos
                    long bccTotal = 0;
                    for (PeriodoContrato per : periodos) {
                        BasesCotizacion bases = basesCotizacionRepository.findByPeriodoContratoId(per.getId()).orElse(null);
                        if (bases != null) {
                            bccTotal += bases.getBasesCotizacionContingenciasComunesAnual();
                        }
                    }
                    setCellMoney(row, 28, bccTotal, resultStyle);

                    // Coeficiente SS = SS Bruta / BCC Total
                    if (bccTotal > 0 && ch.getSsEmpresaBruta() != null) {
                        BigDecimal coef = ch.getSsEmpresaBruta()
                                .divide(BigDecimal.valueOf(bccTotal), 6, RoundingMode.HALF_UP)
                                .multiply(CIEN);
                        setCellPercent(row, 29, coef.doubleValue(), crearEstiloPercent(workbook));
                    } else {
                        row.createCell(29).setCellValue("N/A");
                    }

                    setCellNumber(row, 30, ch.getHorasMaximas(), resultStyle);
                    setCellNumber(row, 31, ch.getHorasBaja(), resultStyle);
                    row.createCell(32).setCellValue(formatearTiposBonificacion(bonificacionesVigentes));
                    setCellMoney(row, 33, ch.getAhorroBonificaciones(), resultStyle);
                    setCellMoney(row, 34, ch.getAhorroInvestigador(), resultStyle);
                    setCellMoney(row, 35, ch.getAhorroOtrasBonificaciones(), resultStyle);
                    setCellMoney(row, 36, ch.getSsEmpresaBruta(), resultStyle);
                    setCellMoney(row, 37, ch.getCosteSS(), resultStyle);
                    setCellMoney(row, 38, ch.getCosteHora(), resultStyle);
                }

                rowIdx++;
            }
        }

        // Autoajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==================== HOJA 2: CÁLCULO BAJAS ====================

    private void crearHojaBajas(XSSFWorkbook workbook, List<Personal> personalList,
                                 int anualidad, int diasDelAnio,
                                 CellStyle headerStyle, CellStyle dateStyle, CellStyle numberStyle) {
        Sheet sheet = workbook.createSheet("Cálculo Bajas");

        String[] headers = {
                "Nombre", "DNI", "F. Inicio Baja", "F. Fin Baja", "Días Baja",
                "Periodo (Contrato)", "F. Alta Periodo", "F. Baja Periodo",
                "Días Solapados", "H. Convenio", "% Jornada", "Días Año",
                "Horas Baja (parcial)"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        LocalDate inicioAnio = LocalDate.of(anualidad, 1, 1);
        LocalDate finAnio = LocalDate.of(anualidad, 12, 31);
        int rowIdx = 1;

        for (Personal personal : personalList) {
            List<PeriodoContrato> periodos = periodoContratoRepository
                    .findByPersonalIdPersonaAndAnioFiscalOrderByFechaAltaAsc(personal.getIdPersona(), anualidad);
            String nombreCompleto = personal.getNombre() + " " +
                    (personal.getApellidos() != null ? personal.getApellidos() : "");

            for (BajaLaboral baja : personal.getBajasLaborales()) {
                LocalDate fib = baja.getFechaInicio().isBefore(inicioAnio) ? inicioAnio : baja.getFechaInicio();
                LocalDate ffb = baja.getFechaFin() == null || baja.getFechaFin().isAfter(finAnio) ? finAnio : baja.getFechaFin();
                if (fib.isAfter(ffb)) continue;

                long diasBajaTotal = ChronoUnit.DAYS.between(fib, ffb) + 1;

                for (PeriodoContrato pc : periodos) {
                    LocalDate pcInicio = pc.getFechaAlta().isBefore(inicioAnio) ? inicioAnio : pc.getFechaAlta();
                    LocalDate pcFin = pc.getFechaBaja() == null || pc.getFechaBaja().isAfter(finAnio) ? finAnio : pc.getFechaBaja();
                    LocalDate overlapInicio = pcInicio.isAfter(fib) ? pcInicio : fib;
                    LocalDate overlapFin = pcFin.isBefore(ffb) ? pcFin : ffb;

                    if (overlapInicio.isAfter(overlapFin)) continue;

                    long diasSolapados = ChronoUnit.DAYS.between(overlapInicio, overlapFin) + 1;
                    BigDecimal horasDia = BigDecimal.valueOf(pc.getHorasConvenio())
                            .divide(BigDecimal.valueOf(diasDelAnio), SCALE, RoundingMode.HALF_UP);
                    BigDecimal horasBaja = horasDia.multiply(BigDecimal.valueOf(diasSolapados))
                            .multiply(pc.getPorcentajeJornada())
                            .divide(CIEN, SCALE, RoundingMode.HALF_UP);

                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(nombreCompleto);
                    row.createCell(1).setCellValue(personal.getDni() != null ? personal.getDni() : "");
                    setCellDate(row, 2, baja.getFechaInicio(), dateStyle);
                    setCellDate(row, 3, baja.getFechaFin(), dateStyle);
                    setCellNumber(row, 4, (double) diasBajaTotal, numberStyle);
                    row.createCell(5).setCellValue(pc.getClaveContrato().getClave() + " - " + pc.getClaveContrato().getDescripcion());
                    setCellDate(row, 6, pc.getFechaAlta(), dateStyle);
                    setCellDate(row, 7, pc.getFechaBaja(), dateStyle);
                    setCellNumber(row, 8, (double) diasSolapados, numberStyle);
                    setCellNumber(row, 9, pc.getHorasConvenio().doubleValue(), numberStyle);
                    setCellNumber(row, 10, pc.getPorcentajeJornada().doubleValue(), numberStyle);
                    setCellNumber(row, 11, (double) diasDelAnio, numberStyle);
                    setCellNumber(row, 12, horasBaja.setScale(2, RoundingMode.HALF_UP).doubleValue(), numberStyle);
                }
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==================== HOJA 3: CÁLCULO SS ====================

    private void crearHojaSS(XSSFWorkbook workbook, List<Personal> personalList,
                              int anualidad, int diasDelAnio, String cnae,
                              ConfiguracionAnualSS config,
                              CellStyle headerStyle, CellStyle subHeaderStyle,
                              CellStyle moneyStyle, CellStyle percentStyle,
                              CellStyle numberStyle) {
        Sheet sheet = workbook.createSheet("Cálculo SS");

        String[] headers = {
                "Nombre", "DNI",
                "Contrato", "Naturaleza", "F. Alta", "F. Baja",
                "Tipo ATEP", "Origen ATEP",
                "Mes",
                "Base CC", "% CC", "Cuota CC",
                "% ATEP", "Cuota ATEP",
                "% Desempleo", "Cuota Desempleo",
                "% FOGASA", "Cuota FOGASA",
                "% FP", "Cuota FP",
                "% MEI", "Cuota MEI",
                "Total Mes"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        LocalDate inicioAnio = LocalDate.of(anualidad, 1, 1);
        LocalDate finAnio = LocalDate.of(anualidad, 12, 31);
        int rowIdx = 1;

        CellStyle boldStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);

        for (Personal personal : personalList) {
            List<PeriodoContrato> periodos = periodoContratoRepository
                    .findByPersonalIdPersonaAndAnioFiscalOrderByFechaAltaAsc(personal.getIdPersona(), anualidad);
            String nombreCompleto = personal.getNombre() + " " +
                    (personal.getApellidos() != null ? personal.getApellidos() : "");

            // Resolver ATEP
            BigDecimal tipoAtep;
            String origenATEP;
            String claveOcupacion = personal.getClaveOcupacion();
            if (claveOcupacion != null && !claveOcupacion.isBlank()) {
                ClaveOcupacion ocupacion = claveOcupacionRepository.findByClaveAndActivaTrue(claveOcupacion.toLowerCase())
                        .orElse(null);
                tipoAtep = ocupacion != null ? ocupacion.getTipoTotal() : BigDecimal.ZERO;
                origenATEP = "CUADRO_II_CLAVE_" + claveOcupacion.toUpperCase();
            } else {
                TarifaPrimasCnae tarifa = tarifaPrimasCnaeRepository.findByCnaeAndAnio(cnae, anualidad)
                        .orElse(null);
                tipoAtep = tarifa != null ? tarifa.getTipoTotal() : BigDecimal.ZERO;
                origenATEP = "CUADRO_I_CNAE_" + cnae;
            }

            for (PeriodoContrato periodo : periodos) {
                ClaveContrato clave = periodo.getClaveContrato();
                NaturalezaContrato naturaleza = clave.getNaturaleza();

                LocalDate fechaInicio = periodo.getFechaAlta().isBefore(inicioAnio) ? inicioAnio : periodo.getFechaAlta();
                LocalDate fechaFin = periodo.getFechaBaja() != null
                        ? (periodo.getFechaBaja().isAfter(finAnio) ? finAnio : periodo.getFechaBaja())
                        : finAnio;

                if (fechaFin.isBefore(fechaInicio)) continue;

                BasesCotizacion bases = basesCotizacionRepository.findByPeriodoContratoId(periodo.getId()).orElse(null);

                // Para contratos especiales (becarios, formación), mostrar cuotas fijas
                boolean esCuotaFija = naturaleza == NaturalezaContrato.BECARIO_NO_REMUNERADO
                        || naturaleza == NaturalezaContrato.BECARIO_REMUNERADO
                        || naturaleza == NaturalezaContrato.FORMACION;

                LocalDate cursor = fechaInicio.withDayOfMonth(1);
                while (!cursor.isAfter(fechaFin)) {
                    LocalDate inicioTramo = fechaInicio.isAfter(cursor) ? fechaInicio : cursor;
                    LocalDate finMes = cursor.withDayOfMonth(cursor.lengthOfMonth());
                    LocalDate finTramo = fechaFin.isBefore(finMes) ? fechaFin : finMes;

                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(nombreCompleto);
                    row.createCell(1).setCellValue(personal.getDni() != null ? personal.getDni() : "");
                    row.createCell(2).setCellValue(clave.getClave());
                    row.createCell(3).setCellValue(naturaleza.name());
                    setCellDate(row, 4, periodo.getFechaAlta(), moneyStyle);
                    setCellDate(row, 5, periodo.getFechaBaja(), moneyStyle);
                    setCellNumber(row, 6, tipoAtep.doubleValue(), percentStyle);
                    row.createCell(7).setCellValue(origenATEP);
                    row.createCell(8).setCellValue(MESES[cursor.getMonthValue() - 1]);

                    if (esCuotaFija) {
                        BigDecimal diasTramo = BigDecimal.valueOf(ChronoUnit.DAYS.between(inicioTramo, finTramo) + 1);
                        BigDecimal cuotaCc, cuotaAtep;

                        if (naturaleza == NaturalezaContrato.BECARIO_NO_REMUNERADO) {
                            cuotaCc = new BigDecimal("2.67").multiply(diasTramo).multiply(new BigDecimal("0.05"));
                            cuotaAtep = new BigDecimal("0.33").multiply(diasTramo);
                        } else {
                            BigDecimal mesesEq = diasTramo.divide(new BigDecimal("30.42"), SCALE, RoundingMode.HALF_UP);
                            cuotaCc = new BigDecimal("60.76").multiply(mesesEq).multiply(new BigDecimal("0.05"));
                            cuotaAtep = new BigDecimal("7.38").multiply(mesesEq);
                        }

                        row.createCell(9).setCellValue("Cuota fija");
                        row.createCell(10).setCellValue("Fija");
                        setCellMoney(row, 11, cuotaCc, moneyStyle);
                        row.createCell(12).setCellValue("Fija");
                        setCellMoney(row, 13, cuotaAtep, moneyStyle);
                        // Sin desempleo, fogasa, fp, mei para estos tipos
                        for (int c = 14; c <= 21; c++) {
                            setCellMoney(row, c, BigDecimal.ZERO, moneyStyle);
                        }
                        BigDecimal totalMes = cuotaCc.add(cuotaAtep);
                        setCellMoney(row, 22, totalMes, moneyStyle);
                    } else {
                        BigDecimal baseCcMes = bases != null
                                ? BigDecimal.valueOf(bases.getBaseCotizacionContingenciasComunesMes(cursor.getMonthValue()))
                                : BigDecimal.ZERO;

                        BigDecimal pccEmpresa = config.getCcEmpresa();
                        BigDecimal cuotaCc = baseCcMes.multiply(pccEmpresa).divide(CIEN, SCALE, RoundingMode.HALF_UP);
                        BigDecimal cuotaAtep = baseCcMes.multiply(tipoAtep).divide(CIEN, SCALE, RoundingMode.HALF_UP);

                        BigDecimal pDesempleo = BigDecimal.ZERO;
                        BigDecimal cuotaDesempleo = BigDecimal.ZERO;
                        if (clave.getCotizaDesempleo()) {
                            pDesempleo = naturaleza == NaturalezaContrato.TEMPORAL
                                    ? config.getDesempleoEmpresaTemporal()
                                    : config.getDesempleoEmpresaIndefinido();
                            cuotaDesempleo = baseCcMes.multiply(pDesempleo).divide(CIEN, SCALE, RoundingMode.HALF_UP);
                        }

                        BigDecimal pFogasa = BigDecimal.ZERO;
                        BigDecimal cuotaFogasa = BigDecimal.ZERO;
                        if (clave.getCotizaFogasa()) {
                            pFogasa = config.getFogasa();
                            cuotaFogasa = baseCcMes.multiply(pFogasa).divide(CIEN, SCALE, RoundingMode.HALF_UP);
                        }

                        BigDecimal pFp = BigDecimal.ZERO;
                        BigDecimal cuotaFp = BigDecimal.ZERO;
                        if (clave.getCotizaFp()) {
                            pFp = config.getFpEmpresa();
                            cuotaFp = baseCcMes.multiply(pFp).divide(CIEN, SCALE, RoundingMode.HALF_UP);
                        }

                        BigDecimal pMei = BigDecimal.ZERO;
                        BigDecimal cuotaMei = BigDecimal.ZERO;
                        if (clave.getCotizaMei()) {
                            pMei = config.getMeiEmpresa();
                            cuotaMei = baseCcMes.multiply(pMei).divide(CIEN, SCALE, RoundingMode.HALF_UP);
                        }

                        setCellMoney(row, 9, baseCcMes, moneyStyle);
                        setCellPercent(row, 10, pccEmpresa.doubleValue(), percentStyle);
                        setCellMoney(row, 11, cuotaCc, moneyStyle);
                        setCellPercent(row, 12, tipoAtep.doubleValue(), percentStyle);
                        setCellMoney(row, 13, cuotaAtep, moneyStyle);
                        setCellPercent(row, 14, pDesempleo.doubleValue(), percentStyle);
                        setCellMoney(row, 15, cuotaDesempleo, moneyStyle);
                        setCellPercent(row, 16, pFogasa.doubleValue(), percentStyle);
                        setCellMoney(row, 17, cuotaFogasa, moneyStyle);
                        setCellPercent(row, 18, pFp.doubleValue(), percentStyle);
                        setCellMoney(row, 19, cuotaFp, moneyStyle);
                        setCellPercent(row, 20, pMei.doubleValue(), percentStyle);
                        setCellMoney(row, 21, cuotaMei, moneyStyle);

                        BigDecimal totalMes = cuotaCc.add(cuotaAtep).add(cuotaDesempleo)
                                .add(cuotaFogasa).add(cuotaFp).add(cuotaMei);
                        setCellMoney(row, 22, totalMes, moneyStyle);
                    }

                    cursor = cursor.plusMonths(1);
                }

                // Fila resumen del periodo
                Row resumenRow = sheet.createRow(rowIdx++);
                resumenRow.createCell(0).setCellValue(nombreCompleto);
                resumenRow.createCell(1).setCellValue(personal.getDni() != null ? personal.getDni() : "");
                resumenRow.createCell(2).setCellValue(clave.getClave());
                resumenRow.createCell(8).setCellValue("TOTAL PERIODO");
                resumenRow.getCell(8).setCellStyle(boldStyle);

                if (!esCuotaFija && bases != null) {
                    long basesAnuales = bases.getBasesCotizacionContingenciasComunesAnual();
                    setCellMoney(resumenRow, 9, basesAnuales, moneyStyle);
                }

                // Totales del periodo desde CosteHoraPersonal no es posible por periodo,
                // se deja la suma mensual para verificación
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ==================== UTILIDADES ====================

    // ==================== HOJA 4: AUDITORIA BONIFICACIONES ====================

    private void crearHojaAuditoriaBonificaciones(XSSFWorkbook workbook,
                                                  List<Personal> personalList,
                                                  int anualidad,
                                                  CellStyle headerStyle,
                                                  CellStyle subHeaderStyle,
                                                  CellStyle moneyStyle,
                                                  CellStyle percentStyle,
                                                  CellStyle dateStyle) {
        Sheet sheet = workbook.createSheet("Auditoria Bonificaciones");

        String[] headers = {
                "Nombre", "DNI", "Tipo Bonificación", "% Bonificación",
                "F. Inicio", "F. Fin", "Descripción",
                "Ahorro Bonif. (estimado)", "Ahorro Tipo (total)", "Ahorro Total Personal"
        };

        Row groupRow = sheet.createRow(0);
        crearCeldaGrupo(groupRow, 0, "Bonificaciones aplicadas", headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));

        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(subHeaderStyle);
        }

        int rowIdx = 2;
        for (Personal personal : personalList) {
            CosteHoraPersonal ch = personal.getCosteHoraPersonal();
            if (ch == null) {
                continue;
            }

            List<BonificacionesTrabajador> bonificacionesVigentes = obtenerBonificacionesVigentes(personal, anualidad);
            if (bonificacionesVigentes.isEmpty()) {
                continue;
            }

            Map<TiposBonificacion, BigDecimal> ahorroPorTipo = new EnumMap<>(TiposBonificacion.class);
            ahorroPorTipo.put(TiposBonificacion.BONIFICACION_PERSONAL_INVESTIGADOR,
                    ch.getAhorroInvestigador() != null ? ch.getAhorroInvestigador() : BigDecimal.ZERO);
            ahorroPorTipo.put(TiposBonificacion.OTRA_BONIFICACION,
                    ch.getAhorroOtrasBonificaciones() != null ? ch.getAhorroOtrasBonificaciones() : BigDecimal.ZERO);

            Map<TiposBonificacion, BigDecimal> pesoPorTipo = new EnumMap<>(TiposBonificacion.class);
            for (BonificacionesTrabajador bonificacion : bonificacionesVigentes) {
                TiposBonificacion tipo = bonificacion.getTipoBonificacion();
                BigDecimal pesoActual = pesoPorTipo.getOrDefault(tipo, BigDecimal.ZERO);
                BigDecimal pesoBonificacion = calcularPesoBonificacionEnAnio(bonificacion, anualidad);
                pesoPorTipo.put(tipo, pesoActual.add(pesoBonificacion));
            }

            for (BonificacionesTrabajador bonificacion : bonificacionesVigentes) {
                Row row = sheet.createRow(rowIdx++);
                TiposBonificacion tipo = bonificacion.getTipoBonificacion();

                BigDecimal ahorroTipo = ahorroPorTipo.getOrDefault(tipo, BigDecimal.ZERO);
                BigDecimal pesoTipo = pesoPorTipo.getOrDefault(tipo, BigDecimal.ZERO);
                BigDecimal pesoBonificacion = calcularPesoBonificacionEnAnio(bonificacion, anualidad);

                BigDecimal ahorroBonificacion = BigDecimal.ZERO;
                if (pesoTipo.compareTo(BigDecimal.ZERO) > 0) {
                    ahorroBonificacion = ahorroTipo
                            .multiply(pesoBonificacion)
                            .divide(pesoTipo, 2, RoundingMode.HALF_UP);
                }

                row.createCell(0).setCellValue(personal.getNombre() + " " +
                        (personal.getApellidos() != null ? personal.getApellidos() : ""));
                row.createCell(1).setCellValue(personal.getDni() != null ? personal.getDni() : "");
                row.createCell(2).setCellValue(traducirTipoBonificacion(tipo));

                setCellPercent(row, 3,
                        bonificacion.getPorcentajeBonificacion() != null
                                ? bonificacion.getPorcentajeBonificacion().doubleValue()
                                : null,
                        percentStyle);
                setCellDate(row, 4, bonificacion.getFechaInicio(), dateStyle);
                setCellDate(row, 5, bonificacion.getFechaFin(), dateStyle);
                row.createCell(6).setCellValue(bonificacion.getDescripcion() != null ? bonificacion.getDescripcion() : "");
                setCellMoney(row, 7, ahorroBonificacion, moneyStyle);
                setCellMoney(row, 8, ahorroTipo, moneyStyle);
                setCellMoney(row, 9, ch.getAhorroBonificaciones(), moneyStyle);
            }
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private BigDecimal calcularPesoBonificacionEnAnio(BonificacionesTrabajador bonificacion, int anualidad) {
        if (bonificacion == null || bonificacion.getPorcentajeBonificacion() == null
                || bonificacion.getFechaInicio() == null || bonificacion.getFechaFin() == null) {
            return BigDecimal.ZERO;
        }

        LocalDate inicioAnio = LocalDate.of(anualidad, 1, 1);
        LocalDate finAnio = LocalDate.of(anualidad, 12, 31);
        LocalDate inicio = bonificacion.getFechaInicio().isBefore(inicioAnio) ? inicioAnio : bonificacion.getFechaInicio();
        LocalDate fin = bonificacion.getFechaFin().isAfter(finAnio) ? finAnio : bonificacion.getFechaFin();

        if (fin.isBefore(inicio)) {
            return BigDecimal.ZERO;
        }

        long diasVigencia = ChronoUnit.DAYS.between(inicio, fin) + 1;
        return BigDecimal.valueOf(diasVigencia)
                .multiply(bonificacion.getPorcentajeBonificacion())
                .divide(CIEN, SCALE, RoundingMode.HALF_UP);
    }

    private List<BonificacionesTrabajador> obtenerBonificacionesVigentes(Personal personal, int anualidad) {
        if (personal.getBonificaciones() == null || personal.getBonificaciones().isEmpty()) {
            return List.of();
        }
        LocalDate inicioAnio = LocalDate.of(anualidad, 1, 1);
        LocalDate finAnio = LocalDate.of(anualidad, 12, 31);
        return personal.getBonificaciones().stream()
                .filter(b -> b != null && b.getAnioFiscal() != null && b.getAnioFiscal() == anualidad)
                .filter(b -> b.getFechaInicio() != null && b.getFechaFin() != null)
                .filter(b -> !b.getFechaFin().isBefore(inicioAnio) && !b.getFechaInicio().isAfter(finAnio))
                .toList();
    }

    private String formatearTiposBonificacion(List<BonificacionesTrabajador> bonificaciones) {
        if (bonificaciones == null || bonificaciones.isEmpty()) {
            return "Sin bonificaciones";
        }
        return bonificaciones.stream()
                .map(BonificacionesTrabajador::getTipoBonificacion)
                .distinct()
                .map(this::traducirTipoBonificacion)
                .collect(Collectors.joining(", "));
    }

    private String traducirTipoBonificacion(TiposBonificacion tipoBonificacion) {
        if (tipoBonificacion == null) {
            return "N/D";
        }
        return switch (tipoBonificacion) {
            case BONIFICACION_PERSONAL_INVESTIGADOR -> "Personal investigador";
            case OTRA_BONIFICACION -> "Otra bonificación";
        };
    }

    private void crearCeldaGrupo(Row row, int col, String valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(style);
    }

    private void setCellMoney(Row row, int col, Long valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor : 0);
        cell.setCellStyle(style);
    }

    private void setCellMoney(Row row, int col, BigDecimal valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor.setScale(2, RoundingMode.HALF_UP).doubleValue() : 0);
        cell.setCellStyle(style);
    }

    private void setCellMoney(Row row, int col, long valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(style);
    }

    private void setCellNumber(Row row, int col, Double valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor : 0);
        cell.setCellStyle(style);
    }

    private void setCellNumber(Row row, int col, BigDecimal valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor.setScale(2, RoundingMode.HALF_UP).doubleValue() : 0);
        cell.setCellStyle(style);
    }

    private void setCellPercent(Row row, int col, Double valor, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor != null ? valor : 0);
        cell.setCellStyle(style);
    }

    private void setCellDate(Row row, int col, LocalDate fecha, CellStyle style) {
        Cell cell = row.createCell(col);
        if (fecha != null) {
            cell.setCellValue(fecha.format(DATE_FMT));
        } else {
            cell.setCellValue("Vigente");
        }
    }

    private CellStyle crearEstiloHeader(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloSubHeader(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.PALE_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloMoney(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00 €"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle crearEstiloPercent(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("0.00\"%\""));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle crearEstiloDate(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloNumber(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        style.setAlignment(HorizontalAlignment.RIGHT);
        return style;
    }

    private CellStyle crearEstiloResult(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
