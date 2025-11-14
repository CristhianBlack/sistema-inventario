import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { Persona } from 'src/app/Modelos/persona';
import { PersonaService } from 'src/app/Servicios/persona.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-persona-lista',
  templateUrl: './persona-lista.component.html',
  styleUrls: ['./persona-lista.component.css']
})
export class PersonaListaComponent implements OnInit{
  
  personas : Persona[] = [];
  personaDetalle: Persona | null = null;

  private destroy$ = new Subject<void>();
  private modalInstance: any;

  personaSeleccionada : Persona | null = null;

  loading = false;

  // Referencia al elemento HTML del modal
    @ViewChild('modalPersona') modalElement!: ElementRef;
    @ViewChild('modalDetalle') modalDetalle!: ElementRef;

  constructor(
    private personaService : PersonaService,
    private toastr : ToastrService
  ){}

  ngOnInit(): void {
    this.obtenerListaPersona();
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

  obtenerListaPersona(): void{
    this.loading = true;
    this.personaService.obtenerListaPersona().pipe(takeUntil(this.destroy$)).subscribe({
      next: (datos) =>{
        console.log('Datos recibidos:', datos);
        this.personas = datos;
      },
      error: (err) =>{
        console.error('Error al obtener personas:', err);
        this.toastr.error('Error al cargar las personas', 'Error');
      },
      complete: () =>{
        this.loading = false
      }
    });
  }

  // Abrir modal para crear o editar ciudad
    abrirModalEditar(persona?: Persona): void {
      this.personaSeleccionada = persona ? { ...persona } : null;
  
      // Esperar un ciclo del render para asegurar que el modal exista
      setTimeout(() => {
        const modalEl = this.modalElement?.nativeElement;
        if (modalEl) {
          this.modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
          this.modalInstance.show();
        } else {
          console.error('No se encontró el modal en el DOM.');
        }
      }, 0);
    }

    // Abrir modal del detalle
   abrirModalDetalle(persona: Persona): void {
  if (!persona.idPersona) return;

  this.personaService.obtenerRolesPorPersona(persona.idPersona).subscribe({
    next: (roles) => {
      this.personaDetalle = { ...persona, roles };
      const modal = new bootstrap.Modal(this.modalDetalle.nativeElement);
      modal.show();
    },
    error: (err) => {
      console.error('Error al cargar roles:', err);
      this.toastr.error('No se pudieron cargar los roles', 'Error');
    }
  });
}
  
    // Cerrar modal y refrescar lista
    cerrarModalYActualizarLista(): void {
      if (this.modalInstance) {
        this.modalInstance.hide();
      }
      this.obtenerListaPersona();
      this.personaSeleccionada = null;
    }

  // Eliminar una persona
    eliminarPersona(id: number): void {
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
          this.personaService.eliminarPersona(id).pipe(takeUntil(this.destroy$)).subscribe({
              next: () => {
                this.toastr.success(
                  'Persona eliminada correctamente','Éxito'
                );
                this.obtenerListaPersona();
              },
              error: (err) => {
                console.error('Error al eliminar persona:', err);
                this.toastr.error('No se pudo eliminar la persona', 'Error ');
              },
            });
        }
      });
    }

    get tieneRoles(): boolean {
  return !!this.personaDetalle?.roles?.length;
}
}
