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
/**
   * Constructor del componente
   * @param movimientoInvetarioService Servicio para consultar movimientos de inventario
   * @param toastr Servicio para mostrar notificaciones
   */
  constructor(
    private movimientoInvetarioService: MovimientoInventarioService,
    private toastr: ToastrService,
  ) {}

  /** Lista de movimientos de inventario */
  movimientoInventarios: MovimientoInventario[] = [];

  /** Fecha inicial del filtro */
  desde: string | null = null;

  /** Fecha final del filtro */
  hasta: string | null = null;

  /**
   * Método del ciclo de vida
   * Se ejecuta al iniciar el componente
   */
  ngOnInit(): void {
    this.cragarListaMovimientos();
  }

  /**
   * Obtiene el listado completo de movimientos de inventario
   */
  cragarListaMovimientos(): void {
    this.movimientoInvetarioService.listarMovimientos().subscribe(
      (data) => {
        this.movimientoInventarios = data;
        console.log('Datos recibidos en movimiento de inventario', data);
      },
      (error) => {
        console.error('Error al obtener los movimientos:', error);
        this.toastr.error('Error al cargar los movimientos', 'Error');
      },
    );
  }

  /**
   * Filtra los movimientos por rango de fechas
   */
  filtrar(): void {
    if (!this.desde || !this.hasta) return;

    this.movimientoInvetarioService
      .getMovimientoInventarioPorFechas(this.desde, this.hasta)
      .subscribe((datos) => {
        this.movimientoInventarios = datos;
      });
  }

  /**
   * Exporta los movimientos de inventario a Excel
   */
  descargarExcel(): void {
    this.movimientoInvetarioService
      .exportarExcel(this.desde, this.hasta)
      .subscribe((blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'MovimientoInventario.xlsx';
        a.click();
        window.URL.revokeObjectURL(url);
      });
  }

  /**
   * Exporta los movimientos de inventario a PDF
   */
  descargarPdf(): void {
    this.movimientoInvetarioService
      .exportarPdf(this.desde, this.hasta)
      .subscribe((blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'MovimientoInventario.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      });
  }

  /** Cantidad de movimientos por página */
  movimientoPorPagina = 10;

  /** Página actual */
  paginaActual = 1;

  /**
   * Calcula el total de páginas
   */
  get totalPaginas(): number {
    return Math.ceil(
      this.movimientoInventarios.length / this.movimientoPorPagina,
    );
  }

  /**
   * Retorna los movimientos correspondientes a la página actual
   */
  get movimientoPaginadas(): MovimientoInventario[] {
    const inicio = (this.paginaActual - 1) * this.movimientoPorPagina;
    return this.movimientoInventarios.slice(
      inicio,
      inicio + this.movimientoPorPagina,
    );
  }

  /**
   * Cambia la página del listado
   * @param pagina número de página
   */
  cambiarPagina(pagina: number): void {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
  }

}
