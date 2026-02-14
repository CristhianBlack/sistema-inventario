package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.LibroDiarioDTO;
import com.cristhian.SistemaInventario.ServicioImplement.LibroDiarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@PreAuthorize("hasRole('ADMIN_SISTEMA')")
@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class LibroDiarioController {

    private final LibroDiarioService libroDiarioService;

    public LibroDiarioController(LibroDiarioService libroDiarioService) {
        this.libroDiarioService = libroDiarioService;
    }

    @GetMapping("/contabilidad/libro-diario")
    public Page<LibroDiarioDTO> listarLibroDiario(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return libroDiarioService.obtenerLibroDiario(desde, hasta, pageable);
    }

    @GetMapping("/contabilidad/libro-diario/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) throws IOException {

        byte[] archivo = libroDiarioService.exportarLibroDiarioExcel(desde, hasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=libro_diario.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(archivo);
    }

    @GetMapping("/contabilidad/libro-diario/pdf")
    public ResponseEntity<byte[]> exportarPDF(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) throws Exception {

        byte[] archivo = libroDiarioService.exportarLibroDiarioPDF(desde, hasta);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=libro_diario.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
    }

}
