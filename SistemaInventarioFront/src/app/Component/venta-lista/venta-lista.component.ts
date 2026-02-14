import { Component, ElementRef, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { TIPO_PERSONA } from 'src/app/Constantes/tipo-persona.const';
import { Persona } from 'src/app/Modelos/persona';
import { Venta } from 'src/app/Modelos/venta';
import { VentaService } from 'src/app/Servicios/venta.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-venta-lista',
  templateUrl: './venta-lista.component.html',
  styleUrls: ['./venta-lista.component.css']
})
export class VentaListaComponent {

  ventas : Venta[] = [];
  VentaDetalle: Venta | null = null;
  
    private destroy$ = new Subject<void>();
    private modalInstance: any;
  
    ventaSeleccionada : Venta | null = null;
    mostrarModalPago = false;
    ventaSeleccionadaId!: number;
    modoSoloLecturaPago = false;
  
    loading = false;
  
    // Referencia al elemento HTML del modal
      @ViewChild('modalVenta') modalElement!: ElementRef;
      @ViewChild('modalDetalle') modalDetalle!: ElementRef;
      //@ViewChild('modalPago') modalPago!:ElementRef;
      
  
    constructor(
      private ventaService : VentaService,
      private toastr : ToastrService
    ){}
  
    ngOnInit(): void {
      this.obtenerListadoVentas();
    }
  
    ngAfterViewInit(): void {
      // Inicializamos el modal una sola vez cuando la vista está lista
      /*if (this.modalElement?.nativeElement) {
        this.modalInstance = bootstrap.Modal.getOrCreateInstance(
          this.modalElement.nativeElement
        );
      }*/
     console.log('modalElement:', this.modalElement);
    }
  
    // Método del ciclo de vida que se ejecuta cuando el componente se destruye
    // Se usa para cerrar suscripciones y liberar memoria
    ngOnDestroy(): void {
      this.destroy$.next();
      this.destroy$.complete();
    }
  
    obtenerListadoVentas(): void{
      this.loading = true;
      this.ventaService.listarVentas().pipe(takeUntil(this.destroy$)).subscribe({
        next: (datos) =>{
          console.log('Datos recibidos:', datos);
          this.ventas = datos;
        },
        error: (err) =>{
          console.error('Error al obtener ventas:', err);
          this.toastr.error('Error al cargar las ventas', 'Error');
        },
        complete: () =>{
          this.loading = false
        }
      });
    }
  
  abrirModalEditar(venta?: Venta): void {
    console.log('Click en abrir modal editar venta');
  
    if (!venta?.idVenta) {
      this.ventaSeleccionada = null;
      this.abrirModal(); 
      return;
    }
  
    this.ventaService.obtenerVentaPorId(venta.idVenta).subscribe({
      next: (data) => {
        console.log("VENTA EDITAR RECIBIDA:", data);
  
        this.ventaSeleccionada = data; //  ahora sí con todos los objetos
        this.abrirModal();
      },
      error: (err) => {
        console.error("Error al cargar venta:", err);
        this.toastr.error("No se pudo cargar la venta", "Error");
      }
    });
  }
  
  private abrirModal(): void {
    setTimeout(() => {
      const modalEl = this.modalElement?.nativeElement;
      if (modalEl) {
        this.modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
        this.modalInstance.show();
      }
    }, 0);
  }
  
      // Abrir modal del detalle
     abrirModalDetalle(venta : Venta): void {
    if (!venta.idVenta) return;
  
    this.ventaService.obtenerVentaPorId(venta.idVenta).subscribe({
      next: (data) => {
        console.log("VENTA DETALLADO RECIBIDA:", data);
        this.VentaDetalle = { ...venta };
        const modal = new bootstrap.Modal(this.modalDetalle.nativeElement);
        modal.show();
      },
      error: (err) => {
        console.error("Error al cargar venta:", err);
        this.toastr.error("No se pudo cargar la venta", "Error");
      }
    });
  }
    
      // Cerrar modal y refrescar lista
      cerrarModalYActualizarLista(): void {
        if (this.modalInstance) {
          this.modalInstance.hide();
        }
        this.obtenerListadoVentas();
        this.ventaSeleccionada = null;
      }
  


abrirModalPago(venta: Venta) {

  if (!venta.idVenta) {
    console.error('La venta no tiene ID');
    return;
  }

  this.ventaSeleccionadaId = venta.idVenta;

  //Si ya está pagada → solo consulta
  this.modoSoloLecturaPago = venta.estado === 'PAGADA';

  this.mostrarModalPago = true;
}

cerrarModalPago() {
  this.mostrarModalPago = false;
}

confirmarVenta(venta: Venta) {

  Swal.fire({
    title: '¿Confirmar venta?',
    html: `
      <b>Venta N° ${venta.idVenta}</b><br>
      Total: <b>$${venta.totalVenta}</b><br><br>
      <span style="color:#dc3545">
        Esta acción descontará el inventario<br>
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
    this.ventaService.confirmarVenta(venta.idVenta!)
      .subscribe({
        next: () => {
          Swal.fire({
            title: 'Confirmada',
            text: `La venta N° ${venta.idVenta} fue confirmada correctamente`,
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
          });

          this.obtenerListadoVentas();
        },
        error: (err) => {
          Swal.fire({
            title: 'Error',
            text: err.error?.mensaje || 'No se pudo confirmar la venta',
            icon: 'error'
          });
        }
      });

  });
}

cancelarVenta(venta: Venta) {

  Swal.fire({
    title: 'Cancelar venta?',
    html: `
      <b>Venta N° ${venta.idVenta}</b><br>
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
    this.ventaService.cancelar(venta.idVenta!)
      .subscribe({
        next: () => {
          Swal.fire({
            title: 'Confirmada',
            text: `La venta N° ${venta.idVenta} fue cancelada correctamente`,
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
          });

          this.obtenerListadoVentas();
        },
        error: (err) => {
          Swal.fire({
            title: 'Error',
            text: err.error?.mensaje || 'No se pudo cancelar la venta por que ya esta confirmada o pagada.',
            icon: 'error'
          });
        }
      });

  });
}

puedeRegistrarPago(venta: Venta): boolean {
  return venta.estado === 'CONFIRMADA' || venta.estado === 'PARCIAL';
}

puedeVerPagos(venta: Venta): boolean {
  return venta.estado === 'PAGADA';
}

puedeCancelarVentas(venta : Venta){
  return venta.estado === 'PENDIENTE';
}

ventasPorPagina = 6;
paginaActual = 1;

get totalPaginas(): number {
  return Math.ceil(this.ventas.length / this.ventasPorPagina);
}

get ventasPaginadas() {
  const inicio = (this.paginaActual - 1) * this.ventasPorPagina;
  return this.ventas.slice(inicio, inicio + this.ventasPorPagina);
}

cambiarPagina(pagina: number) {
  if (pagina < 1 || pagina > this.totalPaginas) return;
  this.paginaActual = pagina;
}



}
