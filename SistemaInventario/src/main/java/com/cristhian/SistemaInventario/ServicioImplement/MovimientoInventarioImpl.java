package com.cristhian.SistemaInventario.ServicioImplement;

import com.cristhian.SistemaInventario.DTO.KardexDTO;
import com.cristhian.SistemaInventario.DTO.MovimientoInventarioDTO;
import com.cristhian.SistemaInventario.Modelo.MovimientoInventario;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar los movimientos de inventario:
 * - Listado general
 * - Filtrado por rango de fechas
 * - Exportación a Excel
 * - Exportación a PDF
 */
@Service
@Transactional
public class MovimientoInventarioImpl implements IMovimientoInventario {

    /*private final MovimientoInventarioRepository movimientoInventarioRepository;

    public MovimientoInventarioImpl(MovimientoInventarioRepository movimientoInventarioRepository) {
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    /*Metodo que permite listar todos los movimientos de inventario hacia la vista la logica se
    * el metodo mapToDTO contiene el resto de la logica que realiza el proceso de llamar los datos*/
   /* @Override
    public List<MovimientoInventarioDTO> listarMovimientosInventario() {

        List<MovimientoInventario> movimientos =
                movimientoInventarioRepository.findAll();

        List<MovimientoInventarioDTO> lista = new ArrayList<>();

        for (MovimientoInventario m : movimientos) {
            lista.add(mapToDTO(m));
        }

        return lista;
    }


    /*Metodo que permite filtrar los movimientos de inventario por un rango de fecha ingresado por el usuario
     y que se muestra en la vista, la logica se  maneja desde el metodo mapToDTO contiene el resto de la logica que
     realiza el proceso de llamar los datos*/
    /*@Override
    public List<MovimientoInventarioDTO> generarMovimientoInventarioPorFechas(
            LocalDateTime desde,
            LocalDateTime hasta) {

        List<MovimientoInventario> movimientos =
                movimientoInventarioRepository.buscarPorFechas(desde, hasta);

        List<MovimientoInventarioDTO> movimiento = new ArrayList<>();


        for (MovimientoInventario m : movimientos) {
            movimiento.add(mapToDTO(m));
        }

        return movimiento;
    }

    //Metodo que permite exportar a excel ya sea por un rango de fechas o sin ninguna
    @Override
    public byte[] exportarMovimientoInventarioExcel(LocalDateTime desde, LocalDateTime hasta) {

        if ((desde == null && hasta != null) || (desde != null && hasta == null)) {
            throw new IllegalArgumentException("Debe enviar ambas fechas o ninguna");
        }

        List<MovimientoInventarioDTO> movimiento;

        //Validamos si las fechas vienen vacias para poder exportar el excel sin rango de fechas.
        if (desde != null && hasta != null) {
            movimiento = generarMovimientoInventarioPorFechas( desde, hasta);
        } else {
            movimiento = listarMovimientosInventario(); // TODOS
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("MOVIMIENTO DEL INVENTARIO");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Fecha");
        header.createCell(1).setCellValue("Producto");
        header.createCell(2).setCellValue("Origen");
        header.createCell(3).setCellValue("Tipo");
        header.createCell(4).setCellValue("cantidad");
        header.createCell(5).setCellValue("Observacion");
        header.createCell(6).setCellValue("Proveedor");

        int rowNum = 1;
        for (MovimientoInventarioDTO m : movimiento) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getFechaMovimiento().toString());
            row.createCell(1).setCellValue(m.getNombreProducto());
            row.createCell(2).setCellValue(m.getOrigenMovimiento().name());
            row.createCell(3).setCellValue(m.getTipoMovimiento().name());
            row.createCell(4).setCellValue(m.getCantidad());
            row.createCell(5).setCellValue(m.getObservacion());
            row.createCell(6).setCellValue(m.getNombreProveedor());
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }

    //Metodo que permite exportar a pdf ya sea por un rango de fechas o sin ninguna
    @Override
    public byte[] exportarMovimientoInventarioPdf( LocalDateTime desde, LocalDateTime hasta) {

        if ((desde == null && hasta != null) || (desde != null && hasta == null)) {
            throw new IllegalArgumentException("Debe enviar ambas fechas o ninguna");
        }

        List<MovimientoInventarioDTO> movimiento;

        //Validamos si las fechas vienen vacias para poder exportar el excel sin rango de fechas.
        if (desde != null && hasta != null) {
            movimiento = generarMovimientoInventarioPorFechas( desde, hasta);
        } else {
            movimiento = listarMovimientosInventario(); // TODOS
        }

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            //  Título
            Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Paragraph title = new Paragraph("MOVIMIENTO DEL INVENTARIO. ", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            //  Fechas
            document.add(new Paragraph(
                    (desde != null ? "Desde: " + desde : "Desde: —") +
                            "    " +
                            (hasta != null ? "Hasta: " + hasta : "Hasta: —")
            ));

            //  Tabla
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2, 2, 2, 2, 4, 2});

            addPdfHeaderCell(table, "Fecha");
            addPdfHeaderCell(table, "Producto");
            addPdfHeaderCell(table, "Origen");
            addPdfHeaderCell(table, "Tipo");
            addPdfHeaderCell(table, "Cantidad");
            //addPdfHeaderCell(table, "producto");
            addPdfHeaderCell(table, "Observación");
            addPdfHeaderCell(table, "Proveedor");

            for (MovimientoInventarioDTO m : movimiento) {
                table.addCell(m.getFechaMovimiento().toString());
                table.addCell(m.getNombreProducto());
                table.addCell(m.getOrigenMovimiento().name());
                table.addCell(m.getTipoMovimiento().name());
                table.addCell(String.valueOf(m.getCantidad()));
                //table.addCell(String.valueOf(m.getIdProducto()));
                table.addCell(m.getObservacion());
                table.addCell(m.getNombreProveedor());
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

    /* metodo que nos permite mapear el movimineto de inventario para se utilizado en los metodos
    generarMovimientoInventarioPorFechas y listarMovimientosInventario() ya que manejan la misma logica
    de ngeocio.*/
    /*private MovimientoInventarioDTO mapToDTO(MovimientoInventario m) {

        MovimientoInventarioDTO dto = new MovimientoInventarioDTO();

        dto.setFechaMovimiento(m.getFechaMovimiento());
        dto.setOrigenMovimiento(m.getOrigenMovimiento());
        dto.setTipoMovimiento(m.getTipoMovimiento());
        dto.setCantidad(m.getCantidad());
        dto.setObservacion(m.getObservacion());

        // Producto
        if (m.getProducto() != null) {
            dto.setIdProducto(m.getProducto().getIdProducto());
            dto.setNombreProducto(m.getProducto().getNombreProducto());
        }

        // Proveedor / Persona
        if (m.getProveedor() != null && m.getProveedor().getPersona() != null) {

            var p = m.getProveedor().getPersona();
            dto.setIdProveedor(m.getProveedor().getIdProveedor());

            if (p.getRazonSocial() != null && !p.getRazonSocial().isBlank()) {
                dto.setNombreProveedor(p.getRazonSocial());
            } else {
                dto.setNombreProveedor(
                        (p.getNombre() != null ? p.getNombre() : "") + " " +
                                (p.getApellido() != null ? p.getApellido() : "") + " " +
                                (p.getSegundoApellido() != null ? p.getSegundoApellido() : "")
                );
            }

        } else {
            dto.setNombreProveedor("—");
        }

        return dto;
    }*/

    // Repositorio para acceso a datos de movimientos de inventario
    private final MovimientoInventarioRepository movimientoInventarioRepository;

    // Inyección por constructor
    public MovimientoInventarioImpl(MovimientoInventarioRepository movimientoInventarioRepository) {
        this.movimientoInventarioRepository = movimientoInventarioRepository;
    }

    /**
     * Lista todos los movimientos de inventario.
     * La conversión de entidad a DTO se realiza en el método mapToDTO.
     */
    @Override
    public List<MovimientoInventarioDTO> listarMovimientosInventario() {

        List<MovimientoInventario> movimientos =
                movimientoInventarioRepository.findAll();

        List<MovimientoInventarioDTO> lista = new ArrayList<>();

        // Convertimos cada entidad a DTO
        for (MovimientoInventario m : movimientos) {
            lista.add(mapToDTO(m));
        }

        return lista;
    }

    /**
     * Obtiene los movimientos de inventario filtrados por un rango de fechas.
     * La lógica de mapeo se delega al método mapToDTO.
     */
    @Override
    public List<MovimientoInventarioDTO> generarMovimientoInventarioPorFechas(
            LocalDateTime desde,
            LocalDateTime hasta) {

        List<MovimientoInventario> movimientos =
                movimientoInventarioRepository.buscarPorFechas(desde, hasta);

        List<MovimientoInventarioDTO> movimiento = new ArrayList<>();

        // Conversión a DTO
        for (MovimientoInventario m : movimientos) {
            movimiento.add(mapToDTO(m));
        }

        return movimiento;
    }

    /**
     * Exporta los movimientos de inventario a Excel.
     * Permite exportar con rango de fechas o todos los registros.
     */
    @Override
    public byte[] exportarMovimientoInventarioExcel(LocalDateTime desde, LocalDateTime hasta) {

        // Validación: ambas fechas deben venir o ninguna
        if ((desde == null && hasta != null) || (desde != null && hasta == null)) {
            throw new IllegalArgumentException("Debe enviar ambas fechas o ninguna");
        }

        List<MovimientoInventarioDTO> movimiento;

        // Si hay fechas, se filtra; si no, se exportan todos
        if (desde != null && hasta != null) {
            movimiento = generarMovimientoInventarioPorFechas(desde, hasta);
        } else {
            movimiento = listarMovimientosInventario();
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("MOVIMIENTO DEL INVENTARIO");

        // Encabezados del Excel
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Fecha");
        header.createCell(1).setCellValue("Producto");
        header.createCell(2).setCellValue("Origen");
        header.createCell(3).setCellValue("Tipo");
        header.createCell(4).setCellValue("Cantidad");
        header.createCell(5).setCellValue("Observación");
        header.createCell(6).setCellValue("Proveedor");

        // Contenido
        int rowNum = 1;
        for (MovimientoInventarioDTO m : movimiento) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(m.getFechaMovimiento().toString());
            row.createCell(1).setCellValue(m.getNombreProducto());
            row.createCell(2).setCellValue(m.getOrigenMovimiento().name());
            row.createCell(3).setCellValue(m.getTipoMovimiento().name());
            row.createCell(4).setCellValue(m.getCantidad());
            row.createCell(5).setCellValue(m.getObservacion());
            row.createCell(6).setCellValue(m.getNombreProveedor());
        }

        // Conversión a byte[]
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            workbook.write(out);
            workbook.close();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }

    /**
     * Exporta los movimientos de inventario a PDF.
     * Permite exportar con rango de fechas o todos los registros.
     */
    @Override
    public byte[] exportarMovimientoInventarioPdf(LocalDateTime desde, LocalDateTime hasta) {

        // Validación de fechas
        if ((desde == null && hasta != null) || (desde != null && hasta == null)) {
            throw new IllegalArgumentException("Debe enviar ambas fechas o ninguna");
        }

        List<MovimientoInventarioDTO> movimiento;

        // Determinar origen de los datos
        if (desde != null && hasta != null) {
            movimiento = generarMovimientoInventarioPorFechas(desde, hasta);
        } else {
            movimiento = listarMovimientosInventario();
        }

        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            Font titleFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Paragraph title = new Paragraph("MOVIMIENTO DEL INVENTARIO", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            // Rango de fechas
            document.add(new Paragraph(
                    (desde != null ? "Desde: " + desde : "Desde: —") +
                            "    " +
                            (hasta != null ? "Hasta: " + hasta : "Hasta: —")
            ));

            // Tabla
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2, 2, 2, 2, 2, 4, 2});

            addPdfHeaderCell(table, "Fecha");
            addPdfHeaderCell(table, "Producto");
            addPdfHeaderCell(table, "Origen");
            addPdfHeaderCell(table, "Tipo");
            addPdfHeaderCell(table, "Cantidad");
            addPdfHeaderCell(table, "Observación");
            addPdfHeaderCell(table, "Proveedor");

            // Filas
            for (MovimientoInventarioDTO m : movimiento) {
                table.addCell(m.getFechaMovimiento().toString());
                table.addCell(m.getNombreProducto());
                table.addCell(m.getOrigenMovimiento().name());
                table.addCell(m.getTipoMovimiento().name());
                table.addCell(String.valueOf(m.getCantidad()));
                table.addCell(m.getObservacion());
                table.addCell(m.getNombreProveedor());
            }

            document.add(table);
            document.close();

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    /**
     * Método auxiliar para crear celdas de encabezado en el PDF.
     */
    private void addPdfHeaderCell(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 10, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        table.addCell(cell);
    }

    /**
     * Mapea la entidad MovimientoInventario a MovimientoInventarioDTO.
     * Este método centraliza la lógica de transformación para reutilización.
     */
    private MovimientoInventarioDTO mapToDTO(MovimientoInventario m) {

        MovimientoInventarioDTO dto = new MovimientoInventarioDTO();

        dto.setFechaMovimiento(m.getFechaMovimiento());
        dto.setOrigenMovimiento(m.getOrigenMovimiento());
        dto.setTipoMovimiento(m.getTipoMovimiento());
        dto.setCantidad(m.getCantidad());
        dto.setObservacion(m.getObservacion());

        // Información del producto
        if (m.getProducto() != null) {
            dto.setIdProducto(m.getProducto().getIdProducto());
            dto.setNombreProducto(m.getProducto().getNombreProducto());
        }

        // Información del proveedor / persona
        if (m.getProveedor() != null && m.getProveedor().getPersona() != null) {

            var p = m.getProveedor().getPersona();
            dto.setIdProveedor(m.getProveedor().getIdProveedor());

            if (p.getRazonSocial() != null && !p.getRazonSocial().isBlank()) {
                dto.setNombreProveedor(p.getRazonSocial());
            } else {
                dto.setNombreProveedor(
                        (p.getNombre() != null ? p.getNombre() : "") + " " +
                                (p.getApellido() != null ? p.getApellido() : "") + " " +
                                (p.getSegundoApellido() != null ? p.getSegundoApellido() : "")
                );
            }

        } else {
            dto.setNombreProveedor("—");
        }

        return dto;
    }
}
