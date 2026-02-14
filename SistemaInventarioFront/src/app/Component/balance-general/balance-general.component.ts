import { Component, OnInit } from '@angular/core';
import { BalanceGeneral } from 'src/app/Modelos/balance-general';
import { BalanceGeneralService } from 'src/app/Servicios/balance-general.service';

@Component({
  selector: 'app-balance-general',
  templateUrl: './balance-general.component.html',
  styleUrls: ['./balance-general.component.css'],
})
export class BalanceGeneralComponent implements OnInit {
  /**
   * Objeto que contiene la información del balance general
   * (activos, pasivos, patrimonio, totales, etc.)
   */
  balance!: BalanceGeneral;

  constructor(private service: BalanceGeneralService) {}

  /**
   * Método del ciclo de vida
   * Se ejecuta al inicializar el componente
   * Obtiene la información del balance general desde el backend
   */
  ngOnInit(): void {
    this.service.obtenerBalance().subscribe((data) => {
      this.balance = data;
      console.log('Balance:', data);
    });
  }

  /**
   * Exporta el balance general en formato Excel (.xlsx)
   * El archivo es generado por el backend
   */
  exportarExcel(): void {
    this.service
      .exportarExcel()
      .subscribe((blob) => this.descargar(blob, 'balance_general.xlsx'));
  }

  /**
   * Exporta el balance general en formato PDF (.pdf)
   * El archivo es generado por el backend
   */
  exportarPdf(): void {
    this.service
      .exportarPdf()
      .subscribe((blob) => this.descargar(blob, 'balance_general.pdf'));
  }

  /**
   * Método auxiliar para descargar archivos desde el navegador
   * @param blob Archivo recibido desde el backend
   * @param nombre Nombre con el que se descargará el archivo
   */
  private descargar(blob: Blob, nombre: string): void {
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = url;
    a.download = nombre;
    a.click();

    // Libera memoria asociada al objeto URL
    window.URL.revokeObjectURL(url);
  }
}
