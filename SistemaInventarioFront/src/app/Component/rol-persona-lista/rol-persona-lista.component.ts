import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { RolPersona } from 'src/app/Modelos/rol-persona';
import { RolPersonaService } from 'src/app/Servicios/rol-persona.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-rol-persona-lista',
  templateUrl: './rol-persona-lista.component.html',
  styleUrls: ['./rol-persona-lista.component.css']
})
export class RolPersonaListaComponent implements OnInit {

  rolesPersonas : RolPersona[] = [];

  RolPersonaSeleccionada: RolPersona | null = null;
  
    private destroy$ = new Subject<void>();
    private modalInstance: any;

  loading = false;

  //Referencia al elemento HTML del modal
    @ViewChild('modalRolPersona') modalElement!: ElementRef;

  constructor(
    private rolPersonaService : RolPersonaService,
    private toastr : ToastrService
  ){}

  ngOnInit(): void {
    this.obtenerListaRolPersona();
  }

  ngAfterViewInit(): void {
    // Inicializamos el modal una sola vez cuando la vista está lista
    if (this.modalElement?.nativeElement) {
      this.modalInstance = bootstrap.Modal.getOrCreateInstance(this.modalElement.nativeElement);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  obtenerListaRolPersona() : void{
    this.loading = true;
    this.rolPersonaService.obtenerListaRolPersona().pipe(takeUntil(this.destroy$)).subscribe({
      next:(datos) =>{
        console.log(' Datos recibidos:', datos);
        this.rolesPersonas = datos;
      },
      error:(err) =>{
        console.error('Error al obtener roles:', err);
        this.toastr.error('Error al cargar los roles', 'Error');
      },
      complete: () =>{
        this.loading = false;
      }
    });
  }

  // Abrir modal para crear o editar categoría
    abrirModalEditar(rolPersona?: RolPersona): void {
      this.RolPersonaSeleccionada = rolPersona ? { ...rolPersona } : null;
  
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
  
    //Cerrar modal y refrescar lista
    cerrarModalYActualizarLista(): void {
      if (this.modalInstance) {
        this.modalInstance.hide();
      }
      this.obtenerListaRolPersona();
      this.RolPersonaSeleccionada = null;
    }
  
    // Eliminar un rol
    eliminarRolPersona(id: number): void {
      Swal.fire({
        title: '¿Eliminar categoría?',
        text: 'Esta acción no se puede deshacer.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar',
        confirmButtonColor: '#d33',
        cancelButtonColor: '#6c757d'
      }).then((resultado) => {
        if (resultado.isConfirmed) {
          this.rolPersonaService.eliminarRolPersona(id)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => {
                this.toastr.success('Rol eliminado correctamente', 'Éxito');
                this.obtenerListaRolPersona();
              },
              error: (err) => {
                console.error('Error al eliminar rol:', err);
                this.toastr.error('No se pudo eliminar la rol', 'Error');
              }
            });
        }
      });
    }

}
