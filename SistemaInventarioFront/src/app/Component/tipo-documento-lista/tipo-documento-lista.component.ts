import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { TipoDocumento } from 'src/app/Modelos/tipo-documento';
import { TipoDocumentoService } from 'src/app/Servicios/tipo-documento.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-tipo-documento-lista',
  templateUrl: './tipo-documento-lista.component.html',
  styleUrls: ['./tipo-documento-lista.component.css']
})
export class TipoDocumentoListaComponent implements OnInit{

  tipoDocumentos : TipoDocumento[] = [];

  tipoDocumentoSeleccionado : TipoDocumento | null = null;

   private destroy$ = new Subject<void>();
    private modalInstance: any;

  loading = false;
  
    // Referencia al elemento HTML del modal
    @ViewChild('modalTipoDocumento') modalElement!: ElementRef;
  

  constructor(
    private tipoDocumentoService : TipoDocumentoService,
    private toastr : ToastrService,

  ){}
  ngOnInit(): void {
    this.obtenerTipoDocumentos();
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

  obtenerTipoDocumentos(): void{

    this.loading = true;
    this.tipoDocumentoService.obtenerListaTipoDocumento().pipe(takeUntil(this.destroy$)).subscribe({
      next:(datos) =>{
         console.log(' Datos recibidos:', datos);
          this.tipoDocumentos = datos;
      },
      error:(err) =>{
        console.error(' Error al obtener ciudades:', err);
          this.toastr.error('Error al cargar los tipos de docmuentos', 'Error');
      },
      complete: ()=>{
        this.loading = false;
      }
    });
  }

  // Abrir modal para crear o editar ciudad
    abrirModalEditar(tipoDocumento?: TipoDocumento): void {
      this.tipoDocumentoSeleccionado = tipoDocumento ? { ...tipoDocumento } : null;
  
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

    // Cerrar modal y refrescar lista
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerTipoDocumentos();
    this.tipoDocumentoSeleccionado = null;
  }

   // Eliminar una categoría
    eliminarCategoria(id: number): void {
      Swal.fire({
        title: '¿Eliminar categoría?',
        text: 'Esta acción no se puede deshacer.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#d33',
        cancelButtonColor: '#6c757d',
      }).then((resultado) => {
        if (resultado.isConfirmed) {
          this.tipoDocumentoService.eliminarTipoDocumento(id).pipe(takeUntil(this.destroy$)).subscribe({
              next: () => {
                this.toastr.success(
                  'Tipo de Documento eliminado correctamente','Éxito '
                );
                this.obtenerTipoDocumentos();
              },
              error: (err) => {
                console.error('Error al eliminar categoría:', err);
                this.toastr.error('No se pudo eliminar la categoría', 'Error');
              },
            });
        }
      });
    }

}
