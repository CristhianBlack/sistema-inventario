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
  styleUrls: ['./tipo-persona-lista.component.css'],
})
export class TipoPersonaListaComponent implements OnInit {
  /** Lista de tipos de persona */
  tipoPersonas: TipoPersona[] = [];

  /** Tipo de persona seleccionado para crear o editar */
  tipoPersonaSeleccionado: TipoPersona | null = null;

  /** Subject para manejar la destrucción de suscripciones */
  private destroy$ = new Subject<void>();

  /** Instancia del modal de Bootstrap */
  private modalInstance: any;

  /** Indicador de carga para la vista */
  loading = false;

  /** Referencia al elemento HTML del modal */
  @ViewChild('modalTipoPersona') modalElement!: ElementRef;

  constructor(
    private tipoPersonaService: TipoPersonaService,
    private toastr: ToastrService,
  ) {}

  /**
   * Ciclo de vida: se ejecuta al iniciar el componente
   * Carga la lista de tipos de persona
   */
  ngOnInit(): void {
    this.obtenerListaTipoPersona();
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
   * Ciclo de vida: se ejecuta cuando el componente se destruye
   * Se usa para cerrar suscripciones y evitar fugas de memoria
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Obtiene la lista de tipos de persona desde el backend
   */
  obtenerListaTipoPersona(): void {
    this.loading = true;
    this.tipoPersonaService
      .obtenerListaTipoPersona()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('Datos recibidos:', datos);
          this.tipoPersonas = datos;
        },
        error: (err) => {
          console.error('Error al obtener tipos de persona:', err);
          this.toastr.error('Error al cargar los tipos de personas', 'Error');
        },
        complete: () => {
          this.loading = false;
        },
      });
  }

  /**
   * Abre el modal para crear o editar un tipo de persona
   * Si se recibe uno existente, se clona para evitar modificar el original
   */
  abrirModalEditar(tipoPersona?: TipoPersona): void {
    this.tipoPersonaSeleccionado = tipoPersona ? { ...tipoPersona } : null;

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
   * Cierra el modal y actualiza la lista de tipos de persona
   */
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerListaTipoPersona();
    this.tipoPersonaSeleccionado = null;
  }

  /**
   * Elimina un tipo de persona por ID con confirmación del usuario
   */
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
        this.tipoPersonaService
          .eliminarTipoPersona(id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.toastr.success(
                'Tipo de Persona eliminada correctamente',
                'Éxito',
              );
              this.obtenerListaTipoPersona();
            },
            error: (err) => {
              console.error('Error al eliminar tipo de persona:', err);
              this.toastr.error(
                'No se pudo eliminar el tipo de persona',
                'Error',
              );
            },
          });
      }
    });
  }

  /**
   * Formatea valores tipo ENUM recibidos desde la base de datos
   * Ejemplo: PERSONA_NATURAL → Persona Natural
   */
  formatearTipoPersona(nombre: string): string {
    if (!nombre) return '';

    return nombre
      .replace(/_/g, ' ')
      .toLowerCase()
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }
}