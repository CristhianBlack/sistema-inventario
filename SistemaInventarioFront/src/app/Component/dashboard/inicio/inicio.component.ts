import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { DashboardTotales } from 'src/app/Modelos/Dashboard-Totales';
import { AutenticationService } from 'src/app/Servicios/autentication.service';
import { DashboardService } from 'src/app/Servicios/dashboard.service';

@Component({
  selector: 'app-inicio',
  templateUrl: './inicio.component.html',
  styleUrls: ['./inicio.component.css']
})
export class InicioComponent implements OnInit{

  totales?: DashboardTotales;
  cargando = true;

  rol: string | null = null;
  nombreUsuario = '';
  rolUsuario: string | null = null;


   constructor(private authService : AutenticationService,
       private toastr : ToastrService,
       private router: Router,
       private dashboardService : DashboardService
     ){}
   

   ngOnInit(): void {
    this.nombreUsuario = this.authService.getNombreCompleto();
    this.rolUsuario = this.authService.getRol();
     this.dashboardService.obtenerTotales().subscribe({
    next: (data) => {
      console.log("Datos totales: ", data)
      this.totales = data;
      this.cargando = false;
    },
    error: () => this.cargando = false
  });
  }

   get esAdmin(): boolean {
    return this.authService.getRol() === 'ADMIN_SISTEMA';
  }

  formatearRolSistema(rolSistema: any): string {
  if (!rolSistema) return '';

  return String(rolSistema)
    .toLowerCase()
    .replace(/_/g, ' ')
    .replace(/\b\w/g, letra => letra.toUpperCase());
}
}
