package com.cristhian.SistemaInventario.Controlador;

import com.cristhian.SistemaInventario.DTO.DashboardTotalesDTO;
import com.cristhian.SistemaInventario.ServicioImplement.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Inventario")
@CrossOrigin("http://localhost:4200")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/totales")
    public DashboardTotalesDTO obtenerTotales() {
        return dashboardService.obtenerTotales();
    }
}
