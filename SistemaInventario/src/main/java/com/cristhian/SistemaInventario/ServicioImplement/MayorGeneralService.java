package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.MayorGeneralDTO;
import com.cristhian.SistemaInventario.DTO.MayorGeneralPageDTO;
import com.cristhian.SistemaInventario.Repositorio.MovimientoContableRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;


import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio encargado de gestionar el Mayor General:
 * - Consulta paginada
 * - Consulta completa
 * - Exportación a Excel
 * - Exportación a PDF
 */
@Service
@Transactional
public class MayorGeneralService {

    // Repositorio que consulta los movimientos contables
    private final MovimientoContableRepository movimientoRepo;

    // Inyección por constructor
    public MayorGeneralService(MovimientoContableRepository movimientoRepo) {
        this.movimientoRepo = movimientoRepo;
    }

    /**
     * Obtiene el Mayor General de forma paginada.
     * Calcula el saldo acumulado por cada movimiento.
     */
    public MayorGeneralPageDTO obtenerMayorPaginado(
            Long idCuenta,
            LocalDate desde,
            LocalDate hasta,
            int page,
            int size
    ) {

        // Convertir fechas a LocalDateTime
        LocalDateTime desdeDT = (desde != null) ? desde.atStartOfDay() : null;
        LocalDateTime hastaDT = (hasta != null) ? hasta.atTime(23, 59, 59) : null;

        // Configuración de paginación y orden
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("asiento.fecha").and(Sort.by("idMovimiento"))
        );

        // Consulta paginada al repositorio
        Page<MayorGeneralDTO> pageData =
                movimientoRepo.obtenerMayorPaginado(idCuenta, desdeDT, hastaDT, pageable);

        // El saldo SIEMPRE inicia en cero
        BigDecimal saldo = BigDecimal.ZERO;

        // Calcular saldo acumulado
        for (MayorGeneralDTO m : pageData.getContent()) {
            BigDecimal debe = m.getDebe() != null ? m.getDebe() : BigDecimal.ZERO;
            BigDecimal haber = m.getHaber() != null ? m.getHaber() : BigDecimal.ZERO;

            saldo = saldo.add(debe).subtract(haber);
            m.setSaldo(saldo);
        }

        // Construcción del DTO de respuesta
        MayorGeneralPageDTO dto = new MayorGeneralPageDTO();
        dto.setSaldoInicial(BigDecimal.ZERO);
        dto.setMovimientos(pageData.getContent());
        dto.setTotalRegistros(pageData.getTotalElements());
        dto.setTotalPages(pageData.getTotalPages());
        dto.setPage(pageData.getNumber());

        return dto;
    }

    /**
     * Construye el Mayor General a partir de una página
     * y un saldo inicial dado.
     */
    private MayorGeneralPageDTO construirMayor(
            Page<MayorGeneralDTO> pageData,
            BigDecimal saldoInicial
    ) {

        BigDecimal saldo = saldoInicial;

        // Recorre los movimientos y calcula el saldo acumulado
        for (MayorGeneralDTO m : pageData.getContent()) {
            BigDecimal debe = m.getDebe() != null ? m.getDebe() : BigDecimal.ZERO;
            BigDecimal haber = m.getHaber() != null ? m.getHaber() : BigDecimal.ZERO;

            saldo = saldo.add(debe).subtract(haber);
            m.setSaldo(saldo);
        }

        // Construcción del DTO
        MayorGeneralPageDTO dto = new MayorGeneralPageDTO();
        dto.setMovimientos(pageData.getContent());
        dto.setTotalRegistros(pageData.getTotalElements());
        dto.setTotalPages(pageData.getTotalPages());
        dto.setPage(pageData.getNumber());
        dto.setSaldoInicial(saldoInicial);

        return dto;
    }

    /**
     * Obtiene el Mayor General completo (sin paginación).
     */
    public List<MayorGeneralDTO> obtenerMayorCompleto(
            Long idCuenta,
            LocalDate desde,
            LocalDate hasta
    ) {

        // Conversión de fechas
        LocalDateTime desdeDT = (desde != null) ? desde.atStartOfDay() : null;
        LocalDateTime hastaDT = (hasta != null) ? hasta.atTime(23, 59, 59) : null;

        // Consulta sin paginación
        Page<MayorGeneralDTO> page =
                movimientoRepo.obtenerMayorPaginado(
                        idCuenta,
                        desdeDT,
                        hastaDT,
                        Pageable.unpaged()
                );

        BigDecimal saldo = BigDecimal.ZERO;

        // Calcular saldo acumulado
        for (MayorGeneralDTO m : page.getContent()) {
            BigDecimal debe = m.getDebe() != null ? m.getDebe() : BigDecimal.ZERO;
            BigDecimal haber = m.getHaber() != null ? m.getHaber() : BigDecimal.ZERO;

            saldo = saldo.add(debe).subtract(haber);
            m.setSaldo(saldo);
        }

        return page.getContent();
    }

    /**
     * Genera el archivo Excel del Mayor General.
     */
    public byte[] generarExcelMayorGeneral(
            Long idCuenta,
            LocalDate desde,
            LocalDate hasta
    ) throws Exception {

        // Conversión de fechas
        LocalDateTime desdeDT = (desde != null) ? desde.atStartOfDay() : null;
        LocalDateTime hastaDT = (hasta != null) ? hasta.atTime(23, 59, 59) : null;

        // Obtener todos los movimientos
        List<MayorGeneralDTO> movimientos =
                movimientoRepo.obtenerMayorPaginado(
                        idCuenta,
                        desdeDT,
                        hastaDT,
                        Pageable.unpaged()
                ).getContent();

        // Calcular saldo acumulado
        BigDecimal saldo = BigDecimal.ZERO;
        for (MayorGeneralDTO m : movimientos) {
            BigDecimal debe = m.getDebe() != null ? m.getDebe() : BigDecimal.ZERO;
            BigDecimal haber = m.getHaber() != null ? m.getHaber() : BigDecimal.ZERO;
            saldo = saldo.add(debe).subtract(haber);
            m.setSaldo(saldo);
        }

        // Crear libro y hoja de Excel
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Mayor General");

        // Encabezados
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Fecha");
        header.createCell(1).setCellValue("Descripción");
        header.createCell(2).setCellValue("Debe");
        header.createCell(3).setCellValue("Haber");
        header.createCell(4).setCellValue("Saldo");

        // Contenido
        int rowNum = 1;
        for (MayorGeneralDTO m : movimientos) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getFecha().toString());
            row.createCell(1).setCellValue(m.getDescripcion());
            row.createCell(2).setCellValue(m.getDebe().doubleValue());
            row.createCell(3).setCellValue(m.getHaber().doubleValue());
            row.createCell(4).setCellValue(m.getSaldo().doubleValue());
        }

        // Convertir a byte[]
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();

        return out.toByteArray();
    }

    /**
     * Genera el archivo PDF del Mayor General.
     */
    public byte[] generarPdfMayorGeneral(
            Long idCuenta,
            LocalDate desde,
            LocalDate hasta
    ) throws Exception {

        // Conversión de fechas
        LocalDateTime desdeDT = (desde != null) ? desde.atStartOfDay() : null;
        LocalDateTime hastaDT = (hasta != null) ? hasta.atTime(23, 59, 59) : null;

        // Obtener movimientos
        List<MayorGeneralDTO> movimientos =
                movimientoRepo.obtenerMayorPaginado(
                        idCuenta,
                        desdeDT,
                        hastaDT,
                        Pageable.unpaged()
                ).getContent();

        // Calcular saldo acumulado
        BigDecimal saldo = BigDecimal.ZERO;
        for (MayorGeneralDTO m : movimientos) {
            BigDecimal debe = m.getDebe() != null ? m.getDebe() : BigDecimal.ZERO;
            BigDecimal haber = m.getHaber() != null ? m.getHaber() : BigDecimal.ZERO;
            saldo = saldo.add(debe).subtract(haber);
            m.setSaldo(saldo);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Crear documento PDF
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, out);
        document.open();

        // Título del documento
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph titulo = new Paragraph("Mayor General", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Tabla
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2, 4, 2, 2, 2});

        agregarHeader(table, "Fecha");
        agregarHeader(table, "Descripción");
        agregarHeader(table, "Debe");
        agregarHeader(table, "Haber");
        agregarHeader(table, "Saldo");

        // Filas
        for (MayorGeneralDTO m : movimientos) {
            table.addCell(m.getFecha().toString());
            table.addCell(m.getDescripcion());
            table.addCell(m.getDebe().toString());
            table.addCell(m.getHaber().toString());
            table.addCell(m.getSaldo().toString());
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    /**
     * Agrega una celda de encabezado a la tabla del PDF.
     */
    private void agregarHeader(PdfPTable table, String texto) {
        Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }

}
