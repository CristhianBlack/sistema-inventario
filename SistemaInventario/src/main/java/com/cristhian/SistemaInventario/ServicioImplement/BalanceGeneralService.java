package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.BalanceGeneralDTO;
import com.cristhian.SistemaInventario.DTO.BalanceLineaDTO;
import com.cristhian.SistemaInventario.Repositorio.MovimientoContableRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class BalanceGeneralService {

    // Repositorio para consultas de movimientos contables agregados
    private final MovimientoContableRepository repo;

    public BalanceGeneralService(MovimientoContableRepository repo) {
        this.repo = repo;
    }

    /**
     * Obtiene el Balance General del sistema.
     *
     * Calcula los totales de:
     * - Activos
     * - Pasivos
     * - Patrimonio
     *
     * Reglas contables aplicadas:
     * - INGRESOS incrementan el patrimonio (utilidad)
     * - GASTOS disminuyen el patrimonio
     *
     * @return DTO con totales y detalle de líneas del balance
     */
    public BalanceGeneralDTO obtenerBalance() {

        // Obtiene las líneas del balance desde la base de datos
        List<BalanceLineaDTO> lineas = repo.obtenerBalance();

        // Salida de depuración para validación de datos contables
        System.out.println("===== BALANCE RAW DESDE BD =====");
        for (BalanceLineaDTO l : lineas) {
            System.out.println(
                    l.getTipo() + " | " +
                            l.getCodigo() + " | " +
                            l.getNombre() + " | " +
                            l.getSaldo()
            );
        }
        System.out.println("TOTAL LINEAS = " + lineas.size());

        // Acumuladores por tipo de cuenta
        BigDecimal activos = BigDecimal.ZERO;
        BigDecimal pasivos = BigDecimal.ZERO;
        BigDecimal patrimonio = BigDecimal.ZERO;

        // Cálculo del balance según tipo de cuenta
        for (BalanceLineaDTO l : lineas) {

            switch (l.getTipo()) {
                case ACTIVO -> activos = activos.add(l.getSaldo());
                case PASIVO -> pasivos = pasivos.add(l.getSaldo());
                case PATRIMONIO -> patrimonio = patrimonio.add(l.getSaldo());
                case INGRESO -> patrimonio = patrimonio.add(l.getSaldo());    // utilidad
                case GASTO -> patrimonio = patrimonio.subtract(l.getSaldo()); // gasto reduce patrimonio
            }

        }

        // Salida de depuración de totales calculados
        System.out.println("ACTIVOS = " + activos);
        System.out.println("PASIVOS = " + pasivos);
        System.out.println("PATRIMONIO = " + patrimonio);

        // Retorna el balance general consolidado
        return new BalanceGeneralDTO(activos, pasivos, patrimonio, lineas);
    }

    /**
     * Genera un archivo Excel (.xlsx) con el Balance General.
     *
     * @return arreglo de bytes representando el archivo Excel
     * @throws Exception en caso de error durante la generación del archivo
     */
    public byte[] generarExcelBalanceGeneral() throws Exception {

        // Obtiene las líneas del balance
        List<BalanceLineaDTO> lineas = repo.obtenerBalance();

        // Creación del workbook y la hoja de Excel
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Balance General");

        // Encabezados de la hoja
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Tipo");
        header.createCell(1).setCellValue("Código");
        header.createCell(2).setCellValue("Cuenta");
        header.createCell(3).setCellValue("Saldo");

        // Llenado de filas con los datos del balance
        int rowNum = 1;
        for (BalanceLineaDTO b : lineas) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(b.getTipo().name());
            row.createCell(1).setCellValue(b.getCodigo());
            row.createCell(2).setCellValue(b.getNombre());
            row.createCell(3).setCellValue(b.getSaldo().doubleValue());
        }

        // Ajuste automático de columnas
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);

        // Escritura del archivo en memoria
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();

        return out.toByteArray();
    }

    /**
     * Genera un archivo PDF con el Balance General.
     *
     * @return arreglo de bytes representando el archivo PDF
     * @throws Exception en caso de error durante la generación del documento
     */
    public byte[] generarPdfBalanceGeneral() throws Exception {

        // Obtiene las líneas del balance
        List<BalanceLineaDTO> lineas = repo.obtenerBalance();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Configuración del documento PDF
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, out);
        document.open();

        // Título del documento
        Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph titulo = new Paragraph("Balance General", tituloFont);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);

        // Tabla principal del balance
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{3, 3, 6, 3});

        // Encabezados de la tabla
        agregarHeader(table, "Tipo");
        agregarHeader(table, "Código");
        agregarHeader(table, "Cuenta");
        agregarHeader(table, "Saldo");

        // Llenado de la tabla con los datos del balance
        for (BalanceLineaDTO b : lineas) {
            table.addCell(b.getTipo().name());
            table.addCell(b.getCodigo());
            table.addCell(b.getNombre());
            table.addCell(b.getSaldo().toString());
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    /**
     * Agrega una celda de encabezado a la tabla PDF.
     *
     * @param table tabla PDF
     * @param texto texto del encabezado
     */
    private void agregarHeader(PdfPTable table, String texto) {
        Font font = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(texto, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        table.addCell(cell);
    }


}
