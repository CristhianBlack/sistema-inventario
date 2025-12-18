package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.KardexDTO;
import com.cristhian.SistemaInventario.Service.IKardexService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
            @RequestParam String desde,
            @RequestParam String hasta) {

        LocalDate fechaDesde = LocalDate.parse(desde);
        LocalDate fechaHasta = LocalDate.parse(hasta);

        return ResponseEntity.ok(
                kardexService.generarKardexPorFechas(id, fechaDesde, fechaHasta)
        );
    }

    @GetMapping("/Kardex/{id}/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @PathVariable int id,
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {

        byte[] excel = kardexService.exportarKardexExcel(id, desde, hasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=kardex.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @GetMapping("/Kardex/{id}/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @PathVariable int id,
            @RequestParam LocalDate desde,
            @RequestParam LocalDate hasta) {

        byte[] pdf = kardexService.exportarKardexPdf(id, desde, hasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=kardex.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }


}
