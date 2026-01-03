import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { MovimientoInventario } from 'src/app/Modelos/movimiento-inventario';
import { MovimientoInventarioService } from 'src/app/Servicios/movimiento-inventario.service';

@Component({
  selector: 'app-movimiento-inventario-list',
  templateUrl: './movimiento-inventario-list.component.html',
  styleUrls: ['./movimiento-inventario-list.component.css']
})
export class MovimientoInventarioListComponent implements OnInit{

  constructor(
    private movimientoInvetarioService : MovimientoInventarioService,
    private toastr : ToastrService
  ){}

  movimientoInventarios : MovimientoInventario[] =[];
  desde!: string;
  hasta!: string;
 
  ngOnInit(): void {
    this.cragarListaMovimientos();
  }

  cragarListaMovimientos(){
    this.movimientoInvetarioService.listarMovimientos().subscribe(
      data =>{
        this.movimientoInventarios = data;
        console.log("Datos recibidos en movimiento de inventario ", data);
        
      },
    error =>{
          console.error('Error al obtener los movimientos:', error);
          this.toastr.error('Error al cargar los movimientos', 'Error');
    });
      
  }

  filtrar(): void {
  if (!this.desde || !this.hasta) return;

  this.movimientoInvetarioService
    .getMovimientoInventarioPorFechas(this.desde, this.hasta)
    .subscribe(datos => {
      this.movimientoInventarios = datos;
    });
}

descargarExcel() {
  this.movimientoInvetarioService.exportarExcel(this.desde, this.hasta)
    .subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'MovimientoInventario.xlsx';
      a.click();
      window.URL.revokeObjectURL(url);
    });
}

descargarPdf() {
  this.movimientoInvetarioService.exportarPdf(this.desde, this.hasta)
    .subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'MovimientoInventario.pdf';
      a.click();
      window.URL.revokeObjectURL(url);
    });
}

movimientoPorPagina = 12;
paginaActual = 1;

get totalPaginas(): number {
  return Math.ceil(this.movimientoInventarios.length / this.movimientoPorPagina);
}

get movimientoPaginadas() {
  const inicio = (this.paginaActual - 1) * this.movimientoPorPagina;
  return this.movimientoInventarios.slice(inicio, inicio + this.movimientoPorPagina);
}

cambiarPagina(pagina: number) {
  if (pagina < 1 || pagina > this.totalPaginas) return;
  this.paginaActual = pagina;
}

}
