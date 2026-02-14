package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.KardexDTO;
import com.cristhian.SistemaInventario.Service.IKardexService;
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
public class KardexController {

    private final IKardexService kardexService;

    public KardexController(IKardexService kardexService) {
        this.kardexService = kardexService;
    }

    @GetMapping("/Kardex/{id}")
    public ResponseEntity<List<KardexDTO>> obtenerKardex(@PathVariable int id) {
        System.out.println("ENTRÉ AL CONTROLLER KARDEX");
        return ResponseEntity.ok(kardexService.generarKardex(id));
    }

    @GetMapping("/Kardex/{id}/filtro")
    public ResponseEntity<List<KardexDTO>> obtenerKardexPorFechas(
            @PathVariable int id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {

        LocalDateTime fechaDesde = desde.atStartOfDay();
        LocalDateTime fechaHasta = hasta.atTime(23, 59, 59);

        return ResponseEntity.ok(
                kardexService.generarKardexPorFechas(id, fechaDesde, fechaHasta)
        );
    }

    @GetMapping("/Kardex/{id}/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @PathVariable int id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        LocalDateTime fechaDesde = desde.atStartOfDay();
        LocalDateTime fechaHasta = hasta.atTime(23, 59, 59);

        byte[] excel = kardexService.exportarKardexExcel(id, fechaDesde, fechaHasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=kardex.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @GetMapping("/Kardex/{id}/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @PathVariable int id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        LocalDateTime fechaDesde = desde.atStartOfDay();
        LocalDateTime fechaHasta = hasta.atTime(23, 59, 59);

        byte[] pdf = kardexService.exportarKardexPdf(id, fechaDesde, fechaHasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=kardex.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


}
