import { Component, OnInit } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { AperturaCuentaService } from 'src/app/Servicios/apertura-cuenta.service';
import { CuentaContableService } from 'src/app/Servicios/cuenta-contable.service';

@Component({
  selector: 'app-apertura-cuenta',
  templateUrl: './apertura-cuenta.component.html',
  styleUrls: ['./apertura-cuenta.component.css'],
})
export class AperturaCuentaComponent implements OnInit {
  /**
   * Lista de cuentas contables disponibles
   * Se utiliza para mostrar las opciones en el formulario
   */
  cuentas: any[] = [];

  /**
   * Modelo del formulario de apertura de cuenta
   * - idCuenta: cuenta seleccionada
   * - monto: valor inicial de la cuenta
   * - fecha: fecha del asiento
   * - descripcion: descripción por defecto del asiento contable
   */
  form = {
    idCuenta: null,
    monto: 0,
    fecha: '',
    descripcion: 'Asiento de apertura',
  };

  constructor(
    private cuentaService: CuentaContableService,
    private aperturaService: AperturaCuentaService,
    private toastr: ToastrService,
  ) {}

  /**
   * Método del ciclo de vida
   * Se ejecuta al inicializar el componente
   * Carga las cuentas contables desde el backend
   */
  ngOnInit(): void {
    this.cuentaService.cargarCuentas().subscribe((res) => {
      this.cuentas = res;
    });
  }

  /**
   * Guarda la apertura de la cuenta contable
   * - Valida que exista una cuenta seleccionada
   * - Valida que el monto sea mayor a cero
   * - Envía la información al backend
   */
  guardar(): void {
    // Validación básica del formulario
    if (!this.form.idCuenta || this.form.monto <= 0) {
      this.toastr.error('Datos inválidos');
      return;
    }

    // Llamado al servicio para registrar la apertura
    this.aperturaService.abrirCuenta(this.form).subscribe({
      next: () => {
        this.toastr.success('Cuenta aperturada correctamente');

        // Reinicia el monto después de guardar
        this.form.monto = 0;
      },
      error: (err) => {
        // Muestra el mensaje de error enviado desde el backend
        this.toastr.error(err.mensaje, 'Error');
      },
    });
  }
}
