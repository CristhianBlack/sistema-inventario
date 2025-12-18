import { Component, ElementRef, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { FormaPago } from 'src/app/Modelos/forma-pago';
import { FormaPagoService } from 'src/app/Servicios/forma-pago.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-forma-pago-lista',
  templateUrl: './forma-pago-lista.component.html',
  styleUrls: ['./forma-pago-lista.component.css']
})
export class FormaPagoListaComponent {

   pagos: FormaPago[] = [];
  
    formaPagoSeleccionado: FormaPago | null = null;
  
    private destroy$ = new Subject<void>();
    private modalInstance: any;
  
    loading = false;
  
    // Referencia al elemento HTML del modal
    @ViewChild('modalFormaPago') modalElement!: ElementRef;
  
    constructor(
      private formaPagoService: FormaPagoService,
      private toastr: ToastrService
    ) {}
  
    ngOnInit(): void {
      this.obtenerFormaPagos();
    }
  
    ngAfterViewInit(): void {
      // Inicializamos el modal una sola vez cuando la vista está lista
      if (this.modalElement?.nativeElement) {
        this.modalInstance = bootstrap.Modal.getOrCreateInstance(
          this.modalElement.nativeElement
        );
      }
    }
  
    //  Método del ciclo de vida que se ejecuta cuando el componente se destruye
    // Se usa para cerrar suscripciones y liberar memoria
    ngOnDestroy(): void {
      this.destroy$.next();
      this.destroy$.complete();
    }
  
    obtenerFormaPagos(): void {
      this.loading = true;
      this.formaPagoService
        .obtenerListaFormaPago()
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (datos) => {
            console.log('Datos recibidos:', datos);
            this.pagos = datos;
          },
          error: (err) => {
            console.error(' Error al obtener forma de pago:', err);
            this.toastr.error('Error al cargar la forma de pago.', 'Error');
          },
          complete: () => {
            this.loading = false;
          },
        });
    }
  
    //Abrir modal para crear o editar ciudad
    abrirModalEditar(formaPago?: FormaPago): void {
      this.formaPagoSeleccionado = formaPago ? { ...formaPago } : null;
  
      // Esperar un ciclo del render para asegurar que el modal exista
      setTimeout(() => {
        const modalEl = this.modalElement?.nativeElement;
        if (modalEl) {
          this.modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
          this.modalInstance.show();
        } else {
          console.error(' No se encontró el modal en el DOM.');
        }
      }, 0);
    }
  
    //  Cerrar modal y refrescar lista
    cerrarModalYActualizarLista(): void {
      if (this.modalInstance) {
        this.modalInstance.hide();
      }
      this.obtenerFormaPagos();
      this.formaPagoSeleccionado = null;
    }
  
    // Eliminar una forma de pago
    eliminarFormaPago(id: number): void {
      console.log("ID recibido para eliminar:", id);
      Swal.fire({
        title: '¿Eliminar forma DE pago?',
        text: 'Esta acción no se puede deshacer.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#d33',
        cancelButtonColor: '#6c757d',
      }).then((resultado) => {
        if (resultado.isConfirmed) {
          this.formaPagoService.eliminarFormPago(id).pipe(takeUntil(this.destroy$)).subscribe({
              next: () => {
                this.toastr.success(
                  'forma de pago eliminada correctamente','Éxito '
                );
                this.obtenerFormaPagos();
              },
              error: (err) => {
                console.error('Error al eliminar categoría:', err);
                this.toastr.error('No se pudo eliminar la categoría', 'Error ');
              },
            });
        }
      });
    }

  getNombreLegible(nombre: string): string {
  const map: any = {
    EFECTIVO: 'Efectivo',
    NEQUI: 'Nequi',
    DAVIPLATA: 'Daviplata',
    TRANSFERENCIA: 'Transferencia',
    TARJETA_DEBITO: 'Tarjeta Débito',
    TARJETA_CREDITO: 'Tarjeta Crédito'
  };
  return map[nombre] ?? nombre;
}
}
