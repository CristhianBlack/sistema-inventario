import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Compra } from 'src/app/Modelos/compra';
import { CompraPagos } from 'src/app/Modelos/compra-pagos';
import { EstadoPago } from 'src/app/Modelos/estado-pago';
import { FormaPago } from 'src/app/Modelos/forma-pago';
import { CompraPagoService } from 'src/app/Servicios/compra-pago.service';
import { CompraService } from 'src/app/Servicios/compra.service';
import { FormaPagoService } from 'src/app/Servicios/forma-pago.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-compra-pago',
  templateUrl: './compra-pago.component.html',
  styleUrls: ['./compra-pago.component.css']
})
export class CompraPagoComponent {


   // ============================
  // INPUTS / OUTPUTS
  // ============================

  // Indica si el componente está en modo solo lectura
  @Input() soloLectura: boolean = false;

  // ID de la compra a la que pertenecen los pagos
  @Input() idCompra!: number;

  // Evento para notificar al componente padre que los pagos cambiaron
  @Output() pagosActualizados = new EventEmitter<void>();

  // ============================
  // VARIABLES DE ESTADO
  // ============================

  pagos: CompraPagos[] = [];                 // Lista de pagos de la compra
  monto = 0;                                 // Monto a registrar
  estadoPago: EstadoPago = EstadoPago.PENDIENTE_CONFIRMACION;

  // Enum expuesto para usarlo en el template
  EstadoPago = EstadoPago;

  totalCompra: number = 0;                   // Total de la compra
  compra: Compra[] = [];
  estado: string = "";
  codigoCompra?: number = 0;

  formaPagos: FormaPago[] = [];               // Formas de pago disponibles
  idFormaPago: number | null = null;          // Forma de pago seleccionada

  constructor(
    private compraPagoService: CompraPagoService,
    private compraService: CompraService,
    private formaPagoService: FormaPagoService,
    private toastr: ToastrService
  ) {}

  // ============================
  // CICLO DE VIDA
  // ============================

  ngOnChanges(changes: SimpleChanges): void {
    // Cuando cambia el ID de la compra, se recargan pagos y datos
    if (changes['idCompra'] && this.idCompra) {
      this.cargarPagos();
      this.cargarCompra();
    }

    // Cargar siempre las formas de pago
    this.cargarFormaPago();
  }

  // ============================
  // CARGA DE DATOS
  // ============================

  // Obtiene los pagos asociados a la compra
  cargarPagos(): void {
    this.compraPagoService
      .listarPagosPorCompra(this.idCompra)
      .subscribe(data => {
        this.pagos = data ?? [];
      });
  }

  // Obtiene las formas de pago disponibles
  cargarFormaPago(): void {
    this.formaPagoService.obtenerListaFormaPago().subscribe(data => {
      this.formaPagos = data;
    });
  }

  // Obtiene información general de la compra
  cargarCompra(): void {
    this.compraService.obtenerCompraPorId(this.idCompra).subscribe(data => {
      console.log('Compra:', data);
      this.totalCompra = data.totalCompra;
      this.estado = data.estado;
      this.codigoCompra = data.idCompra;
    });
  }

  // ============================
  // REGISTRO DE PAGOS
  // ============================

  registrarPago(): void {
    if (this.monto <= 0) return;

    this.compraPagoService.registrarPago(this.idCompra, {
      montoCompra: this.monto,
      estadoPago: this.estadoPago,
      idFormaPago: this.idFormaPago,
    }).subscribe({
      next: () => {
        this.toastr.success('Se agregó el pago correctamente', 'Éxito');
        this.limpiarCapmpos();
        this.cargarPagos();
        this.cargarCompra();
      },
      error: (err) => {
        console.error('Error al registrar el pago:', err);
        this.toastr.error(err.mensaje, 'Error');
      }
    });
  }

  // Confirma un pago pendiente
  confirmarPago(idPago: number): void {
    this.compraPagoService.confirmarPago(idPago).subscribe({
      next: () => {
        this.toastr.success('El pago fue confirmado correctamente', 'Éxito');
        this.cargarPagos();
        this.cargarCompra();
        this.pagosActualizados.emit(); // avisa al padre
      },
      error: () => {
        this.toastr.error('No se pudo confirmar el pago', 'Error');
      }
    });
  }

  // Rechaza un pago pendiente
  rechazarPago(idPago: number): void {
    console.log("Id pago que llega ", idPago)
    this.compraPagoService.rechazarPago(idPago).subscribe({
      next: () => {
        this.toastr.success('El pago fue rechazado correctamente', 'Éxito');
        this.cargarPagos();
        this.cargarCompra();
        this.pagosActualizados.emit(); // avisa al padre
      },
      error: () => {
        this.toastr.error('No se pudo rechazar el pago', 'Error');
      }
    });
  }

  // ============================
  // CÁLCULOS
  // ============================

  // Total pagado (solo pagos confirmados)
  get totalPagado(): number {
    if (!Array.isArray(this.pagos)) return 0;
    return this.pagos
      .filter(p => p.estadoPago === 'CONFIRMADO')
      .reduce((sum, p) => sum + p.montoCompra, 0);
  }

  // Saldo pendiente de la compra
  get saldoPendiente(): number {
    return this.totalCompra - this.totalPagado;
  }

  // Optimiza el renderizado del *ngFor
  trackByPago(index: number, pago: any): number {
    return pago.idVentaPago;
  }

  // ============================
  // CONFIRMACIONES CON SWEETALERT
  // ============================

  confirmacionPago(idPago: number): void {
    Swal.fire({
      title: '¿Desea confirmar el pago?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, confirmar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.confirmarPago(idPago);
      }
    });
  }

  pagoRechazado(idPago: number): void {
    Swal.fire({
      title: '¿Desea rechazar el pago?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, rechazar',
      cancelButtonText: 'Cancelar'
    }).then(result => {
      if (result.isConfirmed) {
        this.rechazarPago(idPago);
      }
    });
  }

  // ============================
  // UTILIDADES
  // ============================

  limpiarCapmpos(): void {
    this.monto = 0;
    this.estadoPago = EstadoPago.PENDIENTE_CONFIRMACION;
    this.cargarFormaPago();
  }
}

