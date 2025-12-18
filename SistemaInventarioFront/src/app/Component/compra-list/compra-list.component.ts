import { Component, ElementRef, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { Compra } from 'src/app/Modelos/compra';
import { CompraService } from 'src/app/Servicios/compra.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-compra-list',
  templateUrl: './compra-list.component.html',
  styleUrls: ['./compra-list.component.css']
})
export class CompraListComponent {
compras : Compra[] = [];
  compraDetalle: Compra | null = null;
  
    private destroy$ = new Subject<void>();
    private modalInstance: any;
  
    compraSeleccionada : Compra | null = null;
  
    loading = false;
  
    // Referencia al elemento HTML del modal
      @ViewChild('modalCompra') modalElement!: ElementRef;
      @ViewChild('modalDetalle') modalDetalle!: ElementRef;
  
    constructor(
      private compraService : CompraService,
      private toastr : ToastrService
    ){}
  
    ngOnInit(): void {
      this.obtenerListaCompras();
    }
  
    ngAfterViewInit(): void {
      // Inicializamos el modal una sola vez cuando la vista está lista
      if (this.modalElement?.nativeElement) {
        this.modalInstance = bootstrap.Modal.getOrCreateInstance(
          this.modalElement.nativeElement
        );
      }
    }
  
    // Método del ciclo de vida que se ejecuta cuando el componente se destruye
    // Se usa para cerrar suscripciones y liberar memoria
    ngOnDestroy(): void {
      this.destroy$.next();
      this.destroy$.complete();
    }
  
    obtenerListaCompras(): void{
      this.loading = true;
      this.compraService.obtenerListaCompra().pipe(takeUntil(this.destroy$)).subscribe({
        next: (datos) =>{
          console.log('Datos recibidos:', datos);
          this.compras = datos;
        },
        error: (err) =>{
          console.error('Error al obtener compras:', err);
          this.toastr.error('Error al cargar las compras', 'Error');
        },
        complete: () =>{
          this.loading = false
        }
      });
    }
  
  abrirModalEditar(compra?: Compra): void {
  
    if (!compra?.idCompra) {
      this.compraSeleccionada = null;
      this.abrirModal(); 
      return;
    }
  
    this.compraService.obtenerCompraPorId(compra.idCompra).subscribe({
      next: (data) => {
        console.log("COMPRA EDITAR RECIBIDA:", data);
  
        this.compraSeleccionada = data; // 🔥 ahora sí con todos los objetos
        this.abrirModal();
      },
      error: (err) => {
        console.error("Error al cargar compra:", err);
        this.toastr.error("No se pudo cargar el compra", "Error");
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
     abrirModalDetalle(compra: Compra): void {
    if (!compra.idCompra) return;
  
    this.compraService.obtenerCompraPorId(compra.idCompra).subscribe({
      next: (data) => {
        console.log("COMPRA DETALLADO RECIBIDA:", data);
        this.compraDetalle = { ...compra };
        const modal = new bootstrap.Modal(this.modalDetalle.nativeElement);
        modal.show();
      },
      error: (err) => {
        console.error("Error al cargar compra:", err);
        this.toastr.error("No se pudo cargar el compra", "Error");
      }
    });
  }
    
      // Cerrar modal y refrescar lista
      cerrarModalYActualizarLista(): void {
        if (this.modalInstance) {
          this.modalInstance.hide();
        }
        this.obtenerListaCompras();
        this.compraSeleccionada = null;
      }
  
    // Eliminar una persona
      eliminarPersona(id: number): void {
        Swal.fire({
          title: '¿Eliminar Compra?',
          text: 'Esta acción no se puede deshacer.',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonText: 'Sí, eliminar',
          cancelButtonText: 'Cancelar',
          confirmButtonColor: '#d33',
          cancelButtonColor: '#6c757d',
        }).then((resultado) => {
          if (resultado.isConfirmed) {
            this.compraService.eliminarCompra(id).pipe(takeUntil(this.destroy$)).subscribe({
                next: () => {
                  this.toastr.success(
                    'Producto eliminado correctamente','Éxito'
                  );
                  this.obtenerListaCompras();
                },
                error: (err) => {
                  console.error('Error al eliminar producto:', err);
                  this.toastr.error('No se pudo eliminar la producto', 'Error ');
                },
              });
          }
        });
      }
}
