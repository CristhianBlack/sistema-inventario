import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AutenticationService } from 'src/app/Servicios/autentication.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit{
  currentYear = new Date().getFullYear();
  darkMode = false;
  rol: string | null = null;
  nombreUsuario = '';
  rolUsuario: string | null = null;

  constructor(private authService : AutenticationService,
    private toastr : ToastrService,
    private router: Router
  ){}

  ngOnInit(): void {
    // Cargar la preferencia del usuario desde localStorage
    const savedTheme = localStorage.getItem('darkMode');
    this.darkMode = savedTheme === 'true';
    this.nombreUsuario = this.authService.getNombreCompleto();
    this.rolUsuario = this.authService.getRol();
  }

  formatearRolSistema(rolSistema: any): string {
  if (!rolSistema) return '';

  return String(rolSistema)
    .toLowerCase()
    .replace(/_/g, ' ')
    .replace(/\b\w/g, letra => letra.toUpperCase());
}

   get esAdmin(): boolean {
    return this.authService.getRol() === 'ADMIN_SISTEMA';
  }

  toggleDarkMode(): void {
    this.darkMode = !this.darkMode;
    localStorage.setItem('darkMode', String(this.darkMode));
  }

  cerrarSubmenu() {
  const submenu = document.getElementById('collapseProducto');
  if (submenu) {
    submenu.classList.remove('show');
  }
}

cerrarSubmenuContabilidad() {
  const submenu = document.getElementById('collapseContabilidad');
  if (submenu) {
    submenu.classList.remove('show');
  }
}

cerrarSubmenuInventario() {
  const submenu = document.getElementById('collapseInventario');
  if (submenu) {
    submenu.classList.remove('show');
  }
}

cerrarSubmenuVentas() {
  const submenu = document.getElementById('collapseVentas');
  if (submenu) {
    submenu.classList.remove('show');
  }
}

cerrarSubmenuConfiguracion() {
  const submenu = document.getElementById('collapseConfiguracion');
  if (submenu) {
    submenu.classList.remove('show');
  }
}

cerrarSubmenuPersona() {
  const submenu = document.getElementById('collapsePersonas');
  if (submenu) {
    submenu.classList.remove('show');
  }
}

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

}
