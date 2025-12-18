package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.KardexDTO;
import com.cristhian.SistemaInventario.Modelo.MovimientoInventario;
import com.cristhian.SistemaInventario.Modelo.TipoMovimiento;
import com.cristhian.SistemaInventario.Repositorio.MovimientoInventarioRepository;
import com.cristhian.SistemaInventario.Service.IKardexService;
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


@Service
@Transactional

public class KardexServiceImpl implements IKardexService {

    private final MovimientoInventarioRepository movimientoInventarioRepository;

    public KardexServiceImpl(MovimientoInventarioRepository movimientoInventarioRepository) {
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    @Override
    public List<KardexDTO> generarKardex(int idProducto) {

        List<MovimientoInventario> movimientos =
                movimientoInventarioRepository.listarPorProductoOrdenado(idProducto);

        if (movimientos == null || movimientos.isEmpty()) {
            return new ArrayList<>(); //NUNCA lanzar error
        }

        List<KardexDTO> kardex = new ArrayList<>();
        int saldo = 0;

        for (MovimientoInventario m : movimientos) {
            System.out.println("MOVIMIENTOS: " + movimientos.size());

            KardexDTO dto = new KardexDTO();
            dto.setFecha(m.getFechaMovimiento());
            dto.setOrigen(m.getOrigenMovimiento().name());
            dto.setObservacion(m.getObservacion());

            if (m.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
                dto.setEntrada(m.getCantidad());
                dto.setSalida(0);
                saldo += m.getCantidad();
            } else {
                dto.setEntrada(0);
                dto.setSalida(m.getCantidad());
                saldo -= m.getCantidad();
            }

            dto.setSaldo(saldo);
            kardex.add(dto);
            System.out.println(m);
        }


        return kardex;
    }

    @Override
    public List<KardexDTO> generarKardexPorFechas(
            int idProducto,
            LocalDate desde,
            LocalDate hasta) {

        List<MovimientoInventario> movimientos =
                movimientoInventarioRepository.buscarPorProductoYFechas(idProducto, desde, hasta);

        List<KardexDTO> kardex = new ArrayList<>();
        int saldo = 0;

        for (MovimientoInventario m : movimientos) {
            KardexDTO dto = new KardexDTO();
            dto.setFecha(m.getFechaMovimiento());
            dto.setOrigen(m.getOrigenMovimiento().name());
            dto.setObservacion(m.getObservacion());

            if (m.getTipoMovimiento() == TipoMovimiento.ENTRADA) {
                dto.setEntrada(m.getCantidad());
                dto.setSalida(0);
                saldo += m.getCantidad();
            } else {
                dto.setEntrada(0);
                dto.setSalida(m.getCantidad());
                saldo -= m.getCantidad();
            }

            dto.setSaldo(saldo);
            kardex.add(dto);
            System.out.println(m);
        }

        return kardex;
    }

    // Exportar a excel
    @Override
    public byte[] exportarKardexExcel(int idProducto, LocalDate desde, LocalDate hasta) {
        List<KardexDTO> kardex = generarKardexPorFechas(idProducto, desde, hasta);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Kardex");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Fecha");
        header.createCell(1).setCellValue("Origen");
        header.createCell(2).setCellValue("Entrada");
        header.createCell(3).setCellValue("Salida");
        header.createCell(4).setCellValue("Saldo");
        header.createCell(5).setCellValue("Observación");

        int rowNum = 1;
        for (KardexDTO k : kardex) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(k.getFecha().toString());
            row.createCell(1).setCellValue(k.getOrigen());
            row.createCell(2).setCellValue(k.getEntrada());
            row.createCell(3).setCellValue(k.getSalida());
            row.createCell(4).setCellValue(k.getSaldo());
            row.createCell(5).setCellValue(k.getObservacion());
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
    public byte[] exportarKardexPdf(int idProducto, LocalDate desde, LocalDate hasta) {

        List<KardexDTO> kardex = generarKardexPorFechas(idProducto, desde, hasta);

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // 🔹 Título
            Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Paragraph title = new Paragraph("KARDEX DEL PRODUCTO", titleFont);
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
            addPdfHeaderCell(table, "Entrada");
            addPdfHeaderCell(table, "Salida");
            addPdfHeaderCell(table, "Saldo");
            addPdfHeaderCell(table, "Observación");

            for (KardexDTO k : kardex) {
                table.addCell(k.getFecha().toString());
                table.addCell(k.getOrigen());
                table.addCell(String.valueOf(k.getEntrada()));
                table.addCell(String.valueOf(k.getSalida()));
                table.addCell(String.valueOf(k.getSaldo()));
                table.addCell(k.getObservacion());
            }

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF Kardex", e);
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
