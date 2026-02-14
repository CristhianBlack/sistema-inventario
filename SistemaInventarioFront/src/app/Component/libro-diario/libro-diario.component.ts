import { Component, OnInit } from '@angular/core';
import { LibroDiario } from 'src/app/Modelos/libro-diario';
import { Page } from 'src/app/Modelos/page';
import { LibroDiarioService } from 'src/app/Servicios/libro-diario.service';

@Component({
  selector: 'app-libro-diario',
  templateUrl: './libro-diario.component.html',
  styleUrls: ['./libro-diario.component.css'],
})
export class LibroDiarioComponent implements OnInit {
  /** Lista de movimientos contables del libro diario */
  movimientos: LibroDiario[] = [];

  /** Fecha inicial del filtro */
  desde: string | null = null;

  /** Fecha final del filtro */
  hasta: string | null = null;

  /** Total acumulado del debe */
  totalDebe = 0;

  /** Total acumulado del haber */
  totalHaber = 0;

  /* ===================== PAGINACIÓN ===================== */

  /** Página actual (base 0 para backend tipo Spring Page) */
  paginaActual = 0;

  /** Total de páginas retornadas por el backend */
  totalPaginas = 0;

  /** Cantidad de registros por página */
  pageSize = 10;

  constructor(private libroDiarioService: LibroDiarioService) {}

  /**
   * Método que se ejecuta al iniciar el componente.
   * Inicializa el rango de fechas al mes actual
   * y carga el libro diario.
   */
  ngOnInit(): void {
    const hoy = new Date();

    // Fecha final: hoy
    this.hasta = hoy.toISOString().substring(0, 10);

    // Fecha inicial: primer día del mes actual
    const primerDiaMes = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
    this.desde = primerDiaMes.toISOString().substring(0, 10);

    this.cargarLibro();
  }

  /**
   * Carga los movimientos del libro diario según filtros y paginación.
   * Calcula totales de debe y haber.
   */
  cargarLibro(): void {
    this.libroDiarioService
      .obtenerLibroDiario(
        this.desde ?? undefined,
        this.hasta ?? undefined,
        this.paginaActual,
        this.pageSize,
      )
      .subscribe((resp: Page<LibroDiario>) => {
        this.movimientos = resp.content;
        this.totalPaginas = resp.totalPages;
        this.calcularTotales();
      });
  }

  /**
   * Aplica el filtro por fechas y reinicia la paginación.
   */
  filtrar(): void {
    this.paginaActual = 0; // Reinicia a la primera página
    this.cargarLibro();
  }

  /**
   * Cambia la página actual del listado
   * @param pagina número de página seleccionada
   */
  cambiarPagina(pagina: number): void {
    if (pagina < 0 || pagina >= this.totalPaginas) return;
    this.paginaActual = pagina;
    this.cargarLibro();
  }

  /**
   * Calcula los totales de debe y haber
   * a partir de los movimientos cargados.
   */
  calcularTotales(): void {
    this.totalDebe = this.movimientos.reduce(
      (suma, mov) => suma + (mov.debe || 0),
      0,
    );

    this.totalHaber = this.movimientos.reduce(
      (suma, mov) => suma + (mov.haber || 0),
      0,
    );
  }

  /**
   * Exporta el libro diario en formato Excel.
   */
  exportarExcel(): void {
    this.libroDiarioService
      .exportarExcel(this.desde!, this.hasta!)
      .subscribe((blob) => this.descargar(blob, 'libro_diario.xlsx'));
  }

  /**
   * Exporta el libro diario en formato PDF.
   */
  exportarPDF(): void {
    this.libroDiarioService
      .exportarPDF(this.desde!, this.hasta!)
      .subscribe((blob) => this.descargar(blob, 'libro_diario.pdf'));
  }

  /**
   * Descarga un archivo recibido como Blob
   * @param blob archivo generado por el backend
   * @param nombre nombre del archivo
   */
  private descargar(blob: Blob, nombre: string): void {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = nombre;
    a.click();
    window.URL.revokeObjectURL(url);
  }
}
