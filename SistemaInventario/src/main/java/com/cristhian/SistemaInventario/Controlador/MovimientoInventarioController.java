package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.KardexDTO;
import com.cristhian.SistemaInventario.DTO.MovimientoInventarioDTO;
import com.cristhian.SistemaInventario.Service.IMovimientoInventario;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class MovimientoInventarioController {

    private final IMovimientoInventario movimientoInventarioService;

    public MovimientoInventarioController(IMovimientoInventario movimientoInventarioService) {
        this.movimientoInventarioService = movimientoInventarioService;
    }

    @GetMapping("MovimientoInventario")
    public List<MovimientoInventarioDTO> listar() {
        return movimientoInventarioService.listarMovimientosInventario();
    }

    @GetMapping("/MovimientoInventario/filtro")
    public ResponseEntity<List<MovimientoInventarioDTO>> obtenerMovimientoInventarioPorFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {

        LocalDateTime fechaDesde = desde.atStartOfDay();
        LocalDateTime fechaHasta = hasta.atTime(23, 59, 59);

        return ResponseEntity.ok(
                movimientoInventarioService.generarMovimientoInventarioPorFechas( fechaDesde, fechaHasta)
        );
    }

    @GetMapping("/MovimientoInventario/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {

        LocalDateTime fechaDesde = null;
        LocalDateTime fechaHasta = null;

        if (desde != null && hasta != null) {
            fechaDesde = desde.atStartOfDay();
            fechaHasta = hasta.atTime(23, 59, 59);
        }

        byte[] excel = movimientoInventarioService.exportarMovimientoInventarioExcel(fechaDesde, fechaHasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=MovimientoInventario.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @GetMapping("/MovimientoInventario/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {

        LocalDateTime fechaDesde = null;
        LocalDateTime fechaHasta = null;

        if (desde != null && hasta != null) {
            fechaDesde = desde.atStartOfDay();
            fechaHasta = hasta.atTime(23, 59, 59);
        }

        byte[] pdf = movimientoInventarioService.exportarMovimientoInventarioPdf(fechaDesde, fechaHasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=MovimientoInventario.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
