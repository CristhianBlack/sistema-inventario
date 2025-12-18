package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.KardexDTO;
import com.cristhian.SistemaInventario.DTO.MovimientoInventarioDTO;
import com.cristhian.SistemaInventario.Service.IMovimientoInventario;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<List<MovimientoInventarioDTO>> listarMoviminetosInvetario(){
        List<MovimientoInventarioDTO> response = movimientoInventarioService.listarMovimientosInventario().stream()
                .map(MovimientoInventarioDTO :: new).toList();// mapeo entidad → DTO
        return  ResponseEntity.ok(response);
    }

    @GetMapping("/MovimientoInventario/filtro")
    public ResponseEntity<List<MovimientoInventarioDTO>> obtenerKardexPorFechas(
            @RequestParam String desde,
            @RequestParam String hasta) {

        LocalDate fechaDesde = LocalDate.parse(desde);
        LocalDate fechaHasta = LocalDate.parse(hasta);

        return ResponseEntity.ok(
                movimientoInventarioService.generarMovimientoInventarioPorFechas( fechaDesde, fechaHasta)
        );
    }

    @GetMapping("/MovimientoInventario/excel")
    public ResponseEntity<byte[]> exportarExcel(

            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {

        byte[] excel = movimientoInventarioService.exportarMovimientoInventarioExcel(desde, hasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=MovimientoInventario.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @GetMapping("/MovimientoInventario/pdf")
    public ResponseEntity<byte[]> exportarPdf(

            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {

        byte[] pdf = movimientoInventarioService.exportarMovimientoInventarioPdf(desde, hasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=MovimientoInventario.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
