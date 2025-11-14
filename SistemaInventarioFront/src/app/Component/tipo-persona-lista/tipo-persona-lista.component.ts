import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { TipoPersona } from 'src/app/Modelos/tipo-persona';
import { TipoPersonaService } from 'src/app/Servicios/tipo-persona.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-tipo-persona-lista',
  templateUrl: './tipo-persona-lista.component.html',
  styleUrls: ['./tipo-persona-lista.component.css']
})
export class TipoPersonaListaComponent implements OnInit{

  tipoPersonas: TipoPersona[] = [];

  tipoPersonaSeleccionado : TipoPersona | null = null;

  private destroy$ = new Subject<void>();
  private modalInstance: any;
  
  loading = false;
  
  // Referencia al elemento HTML del modal
  @ViewChild('modalTipoPersona') modalElement!: ElementRef;

  constructor(
    private tipoPersonaService : TipoPersonaService,
    private toastr : ToastrService
  ){}

  ngOnInit(): void {
    this.obtenerListaTipoPersona();
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


  obtenerListaTipoPersona(): void{
    this.loading = true;
    this.tipoPersonaService.obtenerListaTipoPersona().pipe(takeUntil(this.destroy$)).subscribe({
      next:(datos) =>{
        console.log('Datos recibidos:', datos);
        this.tipoPersonas = datos;
      },
      error:(err) =>{
        console.error('Error al obtener ciudades:', err);
          this.toastr.error('Error al cargar los tipos de personas', 'Error');
      },
      complete:() =>{
        this.loading = false;
      }
    });
  }

   // Abrir modal para crear o editar ciudad
      abrirModalEditar(tipoPersona?: TipoPersona): void {
        this.tipoPersonaSeleccionado = tipoPersona ? { ...tipoPersona } : null;
    
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
  
      // 🔹 Cerrar modal y refrescar lista
    cerrarModalYActualizarLista(): void {
      if (this.modalInstance) {
        this.modalInstance.hide();
      }
      this.obtenerListaTipoPersona();
      this.tipoPersonaSeleccionado = null;
    }
  
     // 🔹 Eliminar una categoría
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
            this.tipoPersonaService.eliminarTipoPersona(id).pipe(takeUntil(this.destroy$)).subscribe({
                next: () => {
                  this.toastr.success(
                    'Ciudad eliminada correctamente','Éxito'
                  );
                  this.obtenerListaTipoPersona();
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
