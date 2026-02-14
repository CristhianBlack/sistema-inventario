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
  styleUrls: ['./rol-persona-lista.component.css'],
})
export class RolPersonaListaComponent implements OnInit {
  /** Lista de roles asignados a personas */
  rolesPersonas: RolPersona[] = [];

  /** Rol seleccionado para crear o editar */
  RolPersonaSeleccionada: RolPersona | null = null;

  /** Subject para manejar la destrucción de suscripciones */
  private destroy$ = new Subject<void>();

  /** Instancia del modal de Bootstrap */
  private modalInstance: any;

  /** Indicador de carga para la vista */
  loading = false;

  /** Referencia al elemento HTML del modal */
  @ViewChild('modalRolPersona') modalElement!: ElementRef;

  constructor(
    private rolPersonaService: RolPersonaService,
    private toastr: ToastrService,
  ) {}

  /**
   * Ciclo de vida: se ejecuta al iniciar el componente
   * Carga la lista de roles por persona
   */
  ngOnInit(): void {
    this.obtenerListaRolPersona();
  }

  /**
   * Ciclo de vida: se ejecuta cuando la vista está completamente renderizada
   * Inicializa el modal de Bootstrap una sola vez
   */
  ngAfterViewInit(): void {
    if (this.modalElement?.nativeElement) {
      this.modalInstance = bootstrap.Modal.getOrCreateInstance(
        this.modalElement.nativeElement,
      );
    }
  }

  /**
   * Ciclo de vida: se ejecuta al destruir el componente
   * Se usa para cerrar suscripciones y evitar fugas de memoria
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Obtiene la lista de roles asignados a personas
   */
  obtenerListaRolPersona(): void {
    this.loading = true;
    this.rolPersonaService
      .obtenerListaRolPersona()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('Datos recibidos:', datos);
          this.rolesPersonas = datos;
        },
        error: (err) => {
          console.error('Error al obtener roles:', err);
          this.toastr.error('Error al cargar los roles', 'Error');
        },
        complete: () => {
          this.loading = false;
        },
      });
  }

  /**
   * Abre el modal para crear o editar un rol
   * Si se recibe un rol, se clona para evitar modificar el original
   */
  abrirModalEditar(rolPersona?: RolPersona): void {
    this.RolPersonaSeleccionada = rolPersona ? { ...rolPersona } : null;

    // Espera un ciclo del render para asegurar que el modal exista
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

  /**
   * Cierra el modal y recarga la lista de roles
   */
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerListaRolPersona();
    this.RolPersonaSeleccionada = null;
  }

  /**
   * Elimina un rol por ID con confirmación del usuario
   */
  eliminarRolPersona(id: number): void {
    Swal.fire({
      title: '¿Eliminar Rol?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33',
      cancelButtonColor: '#6c757d',
    }).then((resultado) => {
      if (resultado.isConfirmed) {
        this.rolPersonaService
          .eliminarRolPersona(id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.toastr.success('Rol eliminado correctamente', 'Éxito');
              this.obtenerListaRolPersona();
            },
            error: (err) => {
              console.error('Error al eliminar rol:', err);
              this.toastr.error('No se pudo eliminar el rol', 'Error');
            },
          });
      }
    });
  }

  /**
   * Formatea el nombre del rol recibido desde la base de datos
   * Convierte valores tipo ENUM a texto legible
   * Ejemplo: ADMIN_GENERAL → Admin General
   */
  formatearRol(rol: any): string {
    if (!rol) return '';

    return String(rol)
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }
}
