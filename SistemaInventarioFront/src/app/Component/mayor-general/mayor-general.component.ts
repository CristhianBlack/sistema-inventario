import { Component, OnInit } from '@angular/core';
import { MayorGeneral } from 'src/app/Modelos/mayor-general';
import { CuentaContableService } from 'src/app/Servicios/cuenta-contable.service';
import { MayorGeneralService } from 'src/app/Servicios/mayor-general.service';

@Component({
  selector: 'app-mayor-general',
  templateUrl: './mayor-general.component.html',
  styleUrls: ['./mayor-general.component.css']
})
export class MayorGeneralComponent  implements OnInit{

  /** Fecha inicial del filtro */
  desde: string | null = null;

  /** Fecha final del filtro */
  hasta: string | null = null;

  /** ID de la cuenta contable seleccionada */
  idCuenta!: number;

  /** Lista de cuentas contables para el selector */
  cuentas: any[] = [];

  /** Movimientos contables del mayor general */
  movimientos: MayorGeneral[] = [];

  /** Saldo inicial calculado antes del rango de fechas */
  saldoInicial = 0;

  /** Total de registros obtenidos */
  totalRegistros = 0;

  /** Total de páginas calculadas */
  totalPaginas = 0;

  /** Página actual */
  page = 0;

  /** Cantidad de registros por página */
  size = 20;

  constructor(
    private mayorService: MayorGeneralService,
    private cuentaContableSevice: CuentaContableService
  ) {}

  /**
   * Método que se ejecuta al iniciar el componente.
   * Carga las cuentas contables y el mayor general.
   */
  ngOnInit(): void {
    this.cargarMayor();
    this.cargarCuentas();
  }

  /**
   * Obtiene el listado de cuentas contables
   * para el selector de cuentas.
   */
  cargarCuentas(): void {
    this.cuentaContableSevice.cargarCuentas()
      .subscribe(data => {
        this.cuentas = data;
        console.log('Cuentas ', data);
      });
  }

  /**
   * Carga el mayor general según la cuenta seleccionada,
   * filtros de fecha y paginación.
   */
  cargarMayor(): void {

    // Si no hay cuenta seleccionada, no se consulta
    if (!this.idCuenta) {
      this.movimientos = [];
      return;
    }

    this.mayorService.obtenerMayorPaginado(
      this.idCuenta,
      this.page,
      this.size,
      this.desde ?? undefined,
      this.hasta ?? undefined
    ).subscribe(resp => {
      this.movimientos = resp.movimientos;
      this.saldoInicial = resp.saldoInicial;
      this.totalRegistros = resp.totalRegistros;
      this.totalPaginas = Math.ceil(this.totalRegistros / this.size);
    });
  }

  /**
   * Aplica los filtros seleccionados
   * y reinicia la paginación.
   */
  filtrar(): void {
    if (!this.desde && !this.hasta && !this.idCuenta) {
      alert('Debe seleccionar al menos una cuenta o un rango de fechas');
      return;
    }

    this.page = 0;
    this.cargarMayor();
  }

  /**
   * Cambia la página del listado
   * @param p número de página
   */
  cambiarPagina(p: number): void {
    this.page = p;
    this.cargarMayor();
  }

  /**
   * Calcula el total del debe
   * de los movimientos mostrados.
   */
  getTotalDebe(): number {
    return this.movimientos.reduce((sum, m) => sum + (m.debe || 0), 0);
  }

  /**
   * Calcula el total del haber
   * de los movimientos mostrados.
   */
  getTotalHaber(): number {
    return this.movimientos.reduce((sum, m) => sum + (m.haber || 0), 0);
  }

  /**
   * Limpia los filtros aplicados
   * y reinicia el listado.
   */
  limpiarFiltro(): void {
    this.movimientos = [];
    this.desde = null;
    this.hasta = null;
  }

  /**
   * Exporta el mayor general a Excel.
   */
  exportarExcel(): void {
    if (!this.idCuenta) {
      alert('Debe seleccionar una cuenta');
      return;
    }

    this.mayorService
      .exportarExcel(this.idCuenta, this.desde ?? undefined, this.hasta ?? undefined)
      .subscribe(blob => this.descargar(blob, 'mayor_general.xlsx'));
  }

  /**
   * Exporta el mayor general a PDF.
   */
  exportarPdf(): void {
    if (!this.idCuenta) {
      alert('Debe seleccionar una cuenta');
      return;
    }

    this.mayorService
      .exportarPdf(this.idCuenta, this.desde ?? undefined, this.hasta ?? undefined)
      .subscribe(blob => this.descargar(blob, 'mayor_general.pdf'));
  }

  /**
   * Descarga un archivo generado por el backend
   * @param blob archivo recibido
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
