import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { Impuesto } from 'src/app/Modelos/impuesto';
import { ImpuestoService } from 'src/app/Servicios/impuesto.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-impuesto-lista',
  templateUrl: './impuesto-lista.component.html',
  styleUrls: ['./impuesto-lista.component.css']
})
export class ImpuestoListaComponent implements OnInit{

  impuestos: Impuesto[] = [];
    
      impuestoSeleccionado: Impuesto | null = null;
    
      private destroy$ = new Subject<void>();
      private modalInstance: any;
    
      loading = false;
    
      // Referencia al elemento HTML del modal
      @ViewChild('modalImpuesto') modalElement!: ElementRef;
    
      constructor(
        private impuestoService: ImpuestoService,
        private toastr: ToastrService
      ) {}
    
      ngOnInit(): void {
        this.obtenerListadoImpuestos();
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
    
      obtenerListadoImpuestos(): void {
        this.loading = true;
        this.impuestoService
          .listarImpuestos()
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (datos) => {
              console.log('Datos recibidos:', datos);
              this.impuestos = datos;
            },
            error: (err) => {
              console.error(' Error al obtener impuestos:', err);
              this.toastr.error('Error al cargar los impuestos.', 'Error');
            },
            complete: () => {
              this.loading = false;
            },
          });
      }
    
      //Abrir modal para crear o editar ciudad
      abrirModalEditar(impuesto?: Impuesto): void {
        this.impuestoSeleccionado = impuesto ? { ...impuesto } : null;
    
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
        this.obtenerListadoImpuestos();
        this.impuestoSeleccionado = null;
      }
    
      // Eliminar una forma de pago
      eliminarFormaPago(id: number): void {
        console.log("ID recibido para eliminar:", id);
        Swal.fire({
          title: '¿Eliminar impuesto?',
          text: 'Esta acción no se puede deshacer.',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonText: 'Sí, eliminar',
          cancelButtonText: 'Cancelar',
          confirmButtonColor: '#d33',
          cancelButtonColor: '#6c757d',
        }).then((resultado) => {
          if (resultado.isConfirmed) {
            this.impuestoService.elminarIpuesto(id).pipe(takeUntil(this.destroy$)).subscribe({
                next: () => {
                  this.toastr.success(
                    'Impuesto eliminado correctamente','Éxito '
                  );
                  this.obtenerListadoImpuestos();
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
      IVA: 'Iva',
      EXENTO: 'Exento',
    };
    return map[nombre] ?? nombre;
  }
}

