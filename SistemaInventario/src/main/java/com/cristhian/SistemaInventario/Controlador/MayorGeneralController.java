package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.MayorGeneralDTO;
import com.cristhian.SistemaInventario.DTO.MayorGeneralPageDTO;
import com.cristhian.SistemaInventario.ServicioImplement.MayorGeneralService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@PreAuthorize("hasRole('ADMIN_SISTEMA')")
@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class MayorGeneralController {

    private final MayorGeneralService mayorService;

    public MayorGeneralController(MayorGeneralService mayorService) {
        this.mayorService = mayorService;
    }

    @GetMapping("/contabilidad/mayor-general")
    public MayorGeneralPageDTO obtenerMayorPaginado(
            @RequestParam Long idCuenta,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return mayorService.obtenerMayorPaginado(idCuenta, desde, hasta, page, size);
    }

    @GetMapping("/contabilidad/mayor-general/excel")
    public ResponseEntity<byte[]> exportarMayorGeneralExcel(
            @RequestParam Long idCuenta,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) throws Exception {

        byte[] archivo =mayorService.generarExcelMayorGeneral(idCuenta, desde, hasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=mayor_general.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(archivo);
    }

    @GetMapping("/contabilidad/mayor-general/pdf")
    public ResponseEntity<byte[]> exportarMayorGeneralPdf(
            @RequestParam Long idCuenta,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) throws Exception {

        byte[] pdf = mayorService.generarPdfMayorGeneral(idCuenta, desde, hasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mayor_general.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
