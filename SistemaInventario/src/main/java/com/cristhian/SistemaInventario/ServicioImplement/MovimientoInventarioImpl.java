package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.KardexDTO;
import com.cristhian.SistemaInventario.DTO.MovimientoInventarioDTO;
import com.cristhian.SistemaInventario.Modelo.MovimientoInventario;
import com.cristhian.SistemaInventario.Modelo.TipoMovimiento;
import com.cristhian.SistemaInventario.Repositorio.MovimientoInventarioRepository;
import com.cristhian.SistemaInventario.Service.IMovimientoInventario;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MovimientoInventarioImpl implements IMovimientoInventario {

    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public MovimientoInventarioImpl(MovimientoInventarioRepository movimientoInventarioRepository) {
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Override
    public List<MovimientoInventario> listarMovimientosInventario() {
        return  movimientoInventarioRepository.findAll();
    }

    @Override
    public List<MovimientoInventarioDTO> generarMovimientoInventarioPorFechas(
            LocalDate desde,
            LocalDate hasta) {

        List<MovimientoInventario> movimientos =
                movimientoInventarioRepository.buscarPorFechas(desde, hasta);

        List<MovimientoInventarioDTO> movimiento = new ArrayList<>();


        for (MovimientoInventario m : movimientos) {
            MovimientoInventarioDTO dto = new MovimientoInventarioDTO();
            dto.setFechaMovimiento(m.getFechaMovimiento());
            dto.setOrigenMovimiento(m.getOrigenMovimiento());
            dto.setTipoMovimiento(m.getTipoMovimiento());
            dto.setCantidad(m.getCantidad());
            dto.setObservacion(m.getObservacion());
            dto.setIdProducto(m.getProducto().getIdProducto());
            dto.setIdProveedor(m.getProveedor().getIdProveedor());

            movimiento.add(dto);
            System.out.println(m);
        }

        return movimiento;
    }

    // Exportar a excel
    @Override
    public byte[] exportarMovimientoInventarioExcel(LocalDate desde, LocalDate hasta) {
        List<MovimientoInventarioDTO> movimiento = generarMovimientoInventarioPorFechas( desde, hasta);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("MOVIMIENTO DEL INVENTARIO");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Fecha");
        header.createCell(1).setCellValue("Origen");
        header.createCell(2).setCellValue("Tipo");
        header.createCell(3).setCellValue("cantidad");
        header.createCell(4).setCellValue("Observacion");
        header.createCell(5).setCellValue("Producto");

        int rowNum = 1;
        for (MovimientoInventarioDTO m : movimiento) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getFechaMovimiento().toString());
            row.createCell(1).setCellValue(m.getOrigenMovimiento().name());
            row.createCell(2).setCellValue(m.getTipoMovimiento().name());
            row.createCell(3).setCellValue(m.getCantidad());
            row.createCell(4).setCellValue(m.getObservacion());
            row.createCell(5).setCellValue(m.getIdProducto());
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }

    @Override
    public byte[] exportarMovimientoInventarioPdf( LocalDate desde, LocalDate hasta) {

        List<MovimientoInventarioDTO> movimiento = generarMovimientoInventarioPorFechas( desde, hasta);

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 🔹 Título
            Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Paragraph title = new Paragraph("MOVIMIENTO DEL INVENTARIO. ", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            // 🔹 Fechas
            document.add(new Paragraph(
                    "Desde: " + desde + "    Hasta: " + hasta
            ));
            document.add(new Paragraph(" "));

            // 🔹 Tabla
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2, 2, 2, 2, 4});

            addPdfHeaderCell(table, "Fecha");
            addPdfHeaderCell(table, "Origen");
            addPdfHeaderCell(table, "Tipo");
            addPdfHeaderCell(table, "Cantidad");
            addPdfHeaderCell(table, "producto");
            addPdfHeaderCell(table, "Observación");

            for (MovimientoInventarioDTO m : movimiento) {
                table.addCell(m.getFechaMovimiento().toString());
                table.addCell(m.getOrigenMovimiento().name());
                table.addCell(m.getTipoMovimiento().name());
                table.addCell(String.valueOf(m.getCantidad()));
                table.addCell(String.valueOf(m.getIdProducto()));
                table.addCell(m.getObservacion());
            }

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    //Método auxiliar para encabezados
    private void addPdfHeaderCell(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
    }
}
