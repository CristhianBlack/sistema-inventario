import { Component, ElementRef, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { Compra } from 'src/app/Modelos/compra';
import { CompraService } from 'src/app/Servicios/compra.service';
import Swal from 'sweetalert2';

// Permite usar Bootstrap JS (Modal) sin errores de TypeScript
declare var bootstrap: any;

@Component({
  selector: 'app-compra-list',
  templateUrl: './compra-list.component.html',
  styleUrls: ['./compra-list.component.css']
})
export class CompraListComponent {

  /** Lista completa de compras */
  compras: Compra[] = [];

  /** Compra utilizada para mostrar el detalle */
  CompraDetalle: Compra | null = null;

  /** Subject para cancelar subscripciones y evitar memory leaks */
  private destroy$ = new Subject<void>();

  /** Instancia del modal de Bootstrap */
  private modalInstance: any;

  /** Compra seleccionada para edición */
  compraSeleccionada: Compra | null = null;

  /** ID de la compra seleccionada para pagos */
  compraSeleccionadaId!: number;

  /** Indicador de carga */
  loading = false;

  /** Indica si el modal de pago es solo lectura */
  modoSoloLecturaPago = false;

  /** Referencia al modal principal de compra */
  @ViewChild('modalCompra') modalElement!: ElementRef;

  /** Referencia al modal de detalle */
  @ViewChild('modalDetalle') modalDetalle!: ElementRef;

  /** Controla la visibilidad del modal de pagos */
  mostrarModalPago = false;

  constructor(
    private compraService: CompraService,
    private toastr: ToastrService
  ) {}

  /**
   * Se ejecuta al inicializar el componente
   * Carga el listado de compras
   */
  ngOnInit(): void {
    this.obtenerListadoCompras();
  }

  /**
   * Se ejecuta cuando la vista está renderizada
   * (Aquí solo se usa para debugging del modal)
   */
  ngAfterViewInit(): void {
    console.log('modalElement:', this.modalElement);
  }

  /**
   * Se ejecuta al destruir el componente
   * Cancela todas las subscripciones activas
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Obtiene el listado de compras desde el backend
   */
  obtenerListadoCompras(): void {
    this.loading = true;

    this.compraService
      .obtenerListaCompra()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('Datos recibidos:', datos);
          this.compras = datos;
        },
        error: (err) => {
          console.error('Error al obtener compras:', err);
          this.toastr.error('Error al cargar las compras', 'Error');
        },
        complete: () => {
          this.loading = false;
        }
      });
  }

  /**
   * Abre el modal para crear o editar una compra
   * @param compra Compra opcional para edición
   */
  abrirModalEditar(compra?: Compra): void {
    console.log('Click en abrir modal editar compra');

    // Si no hay ID, se abre en modo creación
    if (!compra?.idCompra) {
      this.compraSeleccionada = null;
      this.abrirModal();
      return;
    }

    // Si hay ID, se consulta la compra completa
    this.compraService.obtenerCompraPorId(compra.idCompra).subscribe({
      next: (data) => {
        console.log('COMPRA EDITAR RECIBIDA:', data);
        this.compraSeleccionada = data;
        this.abrirModal();
      },
      error: (err) => {
        console.error('Error al cargar compra:', err);
        this.toastr.error('No se pudo cargar la compra', 'Error');
      }
    });
  }

  /**
   * Abre el modal principal de compra
   */
  private abrirModal(): void {
    setTimeout(() => {
      const modalEl = this.modalElement?.nativeElement;
      if (modalEl) {
        this.modalInstance =
          bootstrap.Modal.getOrCreateInstance(modalEl);
        this.modalInstance.show();
      }
    }, 0);
  }

  /**
   * Abre el modal de detalle de una compra
   * @param compra Compra seleccionada
   */
  abrirModalDetalle(compra: Compra): void {
    if (!compra.idCompra) return;

    this.compraService.obtenerCompraPorId(compra.idCompra).subscribe({
      next: (data) => {
        console.log('COMPRA DETALLE RECIBIDA:', data);
        this.CompraDetalle = { ...compra };

        const modal =
          new bootstrap.Modal(this.modalDetalle.nativeElement);
        modal.show();
      },
      error: (err) => {
        console.error('Error al cargar detalle:', err);
        this.toastr.error('No se pudo cargar la compra', 'Error');
      }
    });
  }

  /**
   * Cierra el modal principal y refresca la lista
   */
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerListadoCompras();
    this.compraSeleccionada = null;
  }

  /* =======================
     GESTIÓN DE PAGOS
     ======================= */

  /**
   * Abre el modal de pagos
   * @param compra Compra seleccionada
   */
  abrirModalPago(compra: Compra): void {
    if (!compra.idCompra) {
      console.error('La compra no tiene ID');
      return;
    }

    this.compraSeleccionadaId = compra.idCompra;

    // Si está pagada, el modal es solo lectura
    this.modoSoloLecturaPago = compra.estado === 'PAGADA';
    this.mostrarModalPago = true;
  }

  /**
   * Cierra el modal de pagos
   */
  cerrarModalPago(): void {
    this.mostrarModalPago = false;
  }

  confirmarCompra(compra: Compra) {
  
    Swal.fire({
      title: '¿Confirmar compra?',
      html: `
        <b>Compra N° ${compra.idCompra}</b><br>
        Total: <b>$${compra.totalCompra}</b><br><br>
        <span style="color:#dc3545">
          Esta acción aumentara el inventario<br>
          y no podrá deshacerse.
        </span>
      `,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#198754', // verde
      cancelButtonColor: '#dc3545',  // rojo
      confirmButtonText: 'Sí, confirmar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
  
      if (!result.isConfirmed) return;
  
      // 🔁 Llamada al backend
      this.compraService.confirmarCompra(compra.idCompra!)
        .subscribe({
          next: () => {
            Swal.fire({
              title: 'Confirmada',
              text: `La compra N° ${compra.idCompra} fue confirmada correctamente`,
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
  
            this.obtenerListadoCompras();
          },
          error: (err) => {
            Swal.fire({
              title: 'Error',
              text: err.error?.mensaje || 'No se pudo confirmar la compra',
              icon: 'error'
            });
          }
        });
  
    });
  }
  
  cancelarCompra(compra: Compra) {
  
    Swal.fire({
      title: 'Cancelar Compra?',
      html: `
        <b>Compra N° ${compra.idCompra}</b><br>
        <span style="color:#dc3545">
          Esta acción no ejecutara ninguna accion en el inventario.<br>
          
        </span>
      `,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#198754', // verde
      cancelButtonColor: '#dc3545',  // rojo
      confirmButtonText: 'Sí, confirmar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
  
      if (!result.isConfirmed) return;
  
      // 🔁 Llamada al backend
      this.compraService.cancelar(compra.idCompra!)
        .subscribe({
          next: () => {
            Swal.fire({
              title: 'Confirmada',
              text: `La compra N° ${compra.idCompra} fue cancelada correctamente`,
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
  
            this.obtenerListadoCompras();
          },
          error: (err) => {
            Swal.fire({
              title: 'Error',
              text: err.error?.mensaje || 'No se pudo cancelar la compra por que ya esta confirmada o pagada.',
              icon: 'error'
            });
          }
        });
  
    });
  }

  /**
   * Indica si se puede registrar un pago
   */
  puedeRegistrarPago(compra: Compra): boolean {
    return compra.estado === 'CONFIRMADA'
        || compra.estado === 'PARCIAL';
  }

  /**
   * Indica si se pueden visualizar pagos
   */
  puedeVerPagos(compra: Compra): boolean {
    return compra.estado === 'PAGADA';
  }

  puedeCancelarCompra(compra : Compra){
    return compra.estado === 'PENDIENTE';
  }

  /* =======================
     PAGINACIÓN
     ======================= */

  /** Cantidad de registros por página */
  compraPorPagina = 6;

  /** Página actual */
  paginaActual = 1;

  /** Total de páginas */
  get totalPaginas(): number {
    return Math.ceil(this.compras.length / this.compraPorPagina);
  }

  /** Compras según la página actual */
  get comprasPaginadas() {
    const inicio =
      (this.paginaActual - 1) * this.compraPorPagina;
    return this.compras.slice(
      inicio,
      inicio + this.compraPorPagina
    );
  }

  /**
   * Cambia la página actual
   * @param pagina Número de página
   */
  cambiarPagina(pagina: number): void {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
  }
}
