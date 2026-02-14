package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.LibroDiarioDTO;
import com.cristhian.SistemaInventario.Repositorio.MovimientoContableRepository;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio encargado de la gestión del Libro Diario.
 *
 * Permite:
 * - Consultar los movimientos contables en un rango de fechas
 * - Obtener resultados paginados
 * - Exportar el Libro Diario en formatos Excel y PDF
 */
@Service
@Transactional
public class LibroDiarioService {

    /**
     * Repositorio de movimientos contables.
     */
    private final MovimientoContableRepository movimientoRepo;

    /**
     * Inyección del repositorio por constructor.
     *
     * @param movimientoRepo repositorio de movimientos contables
     */
    public LibroDiarioService(MovimientoContableRepository movimientoRepo) {
        this.movimientoRepo = movimientoRepo;
    }

    /**
     * Obtiene el Libro Diario de forma paginada.
     *
     * @param desde fecha inicial (opcional)
     * @param hasta fecha final (opcional)
     * @param pageable configuración de paginación y orden
     * @return página de registros del libro diario
     */
    public Page<LibroDiarioDTO> obtenerLibroDiario(
            LocalDate desde,
            LocalDate hasta,
            Pageable pageable
    ) {

        // Conversión de fechas a LocalDateTime para consultas
        LocalDateTime desdeDT = desde != null
                ? desde.atStartOfDay()
                : null;

        LocalDateTime hastaDT = hasta != null
                ? hasta.atTime(23, 59, 59)
                : null;

        return movimientoRepo
                .obtenerLibroDiarioPaginado(desdeDT, hastaDT, pageable);
    }

    /**
     * Exporta el Libro Diario a formato Excel.
     *
     * @param desde fecha inicial
     * @param hasta fecha final
     * @return archivo Excel en bytes
     * @throws IOException error al generar el archivo
     */
    public byte[] exportarLibroDiarioExcel(LocalDate desde, LocalDate hasta)
            throws IOException {

        // Conversión de fechas
        LocalDateTime desdeDT = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime hastaDT = hasta != null ? hasta.atTime(23, 59, 59) : null;

        // Obtención de datos
        List<LibroDiarioDTO> datos =
                movimientoRepo.obtenerLibroDiario(desdeDT, hastaDT);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Libro Diario");

        // Encabezados del Excel
        Row header = sheet.createRow(0);
        String[] columnas = {
                "Fecha", "Asiento", "Cuenta",
                "Descripción", "Debe", "Haber"
        };

        for (int i = 0; i < columnas.length; i++) {
            header.createCell(i).setCellValue(columnas[i]);
        }

        // Carga de información
        int fila = 1;
        for (LibroDiarioDTO d : datos) {
            Row row = sheet.createRow(fila++);
            row.createCell(0).setCellValue(d.getFecha().toString());
            row.createCell(1).setCellValue(d.getIdAsiento());
            row.createCell(2)
                    .setCellValue(d.getCodigoCuenta() + " - " + d.getNombreCuenta());
            row.createCell(3).setCellValue(d.getDescripcionAsiento());
            row.createCell(4).setCellValue(d.getDebe().doubleValue());
            row.createCell(5).setCellValue(d.getHaber().doubleValue());
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }

    /**
     * Exporta el Libro Diario a formato PDF.
     *
     * @param desde fecha inicial
     * @param hasta fecha final
     * @return archivo PDF en bytes
     * @throws Exception error al generar el PDF
     */
    public byte[] exportarLibroDiarioPDF(LocalDate desde, LocalDate hasta)
            throws Exception {

        // Conversión de fechas
        LocalDateTime desdeDT = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime hastaDT = hasta != null ? hasta.atTime(23, 59, 59) : null;

        // Obtención de datos
        List<LibroDiarioDTO> datos =
                movimientoRepo.obtenerLibroDiario(desdeDT, hastaDT);

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Título del documento
        document.add(new Paragraph("LIBRO DIARIO"));
        document.add(new Paragraph(" "));

        // Tabla del libro diario
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);

        String[] headers = {
                "Fecha", "Asiento", "Cuenta",
                "Descripción", "Debe", "Haber"
        };

        for (String h : headers) {
            table.addCell(new PdfPCell(new Phrase(h)));
        }

        for (LibroDiarioDTO d : datos) {
            table.addCell(d.getFecha().toString());
            table.addCell(String.valueOf(d.getIdAsiento()));
            table.addCell(d.getCodigoCuenta());
            table.addCell(d.getDescripcionAsiento());
            table.addCell(d.getDebe().toString());
            table.addCell(d.getHaber().toString());
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }
}
