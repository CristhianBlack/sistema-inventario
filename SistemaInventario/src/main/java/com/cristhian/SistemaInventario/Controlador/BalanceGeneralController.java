package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.BalanceGeneralDTO;
import com.cristhian.SistemaInventario.ServicioImplement.BalanceGeneralService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasRole('ADMIN_SISTEMA')")
@RestController
@RequestMapping("/Inventario")
@CrossOrigin(origins = "http://localhost:4200")
public class BalanceGeneralController {

    private final BalanceGeneralService balanceGeneralService;

    public BalanceGeneralController(BalanceGeneralService balanceGeneralService) {
        this.balanceGeneralService = balanceGeneralService;
    }

    @GetMapping("/contabilidad/balance-general")
    public BalanceGeneralDTO balanceGeneral() {
        return balanceGeneralService.obtenerBalance();
    }

    @GetMapping("/contabilidad/balance-general/excel")
    public ResponseEntity<byte[]> exportarBalanceExcel() throws Exception {

        byte[] excel = balanceGeneralService.generarExcelBalanceGeneral();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=balance_general.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }

    @GetMapping("/contabilidad/balance-general/pdf")
    public ResponseEntity<byte[]> exportarBalancePdf() throws Exception {

        byte[] pdf = balanceGeneralService.generarPdfBalanceGeneral();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=balance_general.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
