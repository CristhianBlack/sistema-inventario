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
  styleUrls: ['./persona-lista.component.css'],
})
export class PersonaListaComponent implements OnInit {
  /** Listado de personas */
  personas: Persona[] = [];

  /** Persona seleccionada para ver detalle */
  personaDetalle: Persona | null = null;

  /** Subject para cancelar suscripciones al destruir el componente */
  private destroy$ = new Subject<void>();

  /** Instancia del modal de Bootstrap */
  private modalInstance: any;

  /** Persona seleccionada para crear o editar */
  personaSeleccionada: Persona | null = null;

  /** Indicador de carga */
  loading = false;

  /** Valores formateados para mostrar en el detalle */
  tipoPersonaFormateado = '';
  TipoDocumentoFormateado = '';
  proveedorFormateado = '';

  /** Referencias a los modales del DOM */
  @ViewChild('modalPersona') modalElement!: ElementRef;
  @ViewChild('modalDetalle') modalDetalle!: ElementRef;

  /**
   * Constructor del componente
   * @param personaService Servicio de personas
   * @param toastr Servicio de notificaciones
   */
  constructor(
    private personaService: PersonaService,
    private toastr: ToastrService,
  ) {}

  /**
   * Ciclo de vida
   * Se ejecuta al inicializar el componente
   */
  ngOnInit(): void {
    this.obtenerListaPersona();
  }

  /**
   * Inicializa el modal de Bootstrap cuando la vista está lista
   */
  ngAfterViewInit(): void {
    if (this.modalElement?.nativeElement) {
      this.modalInstance = bootstrap.Modal.getOrCreateInstance(
        this.modalElement.nativeElement,
      );
    }
  }

  /**
   * Ciclo de vida
   * Se ejecuta al destruir el componente
   * Libera memoria cerrando suscripciones
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Obtiene el listado de personas desde el backend
   */
  obtenerListaPersona(): void {
    this.loading = true;
    this.personaService
      .obtenerListaPersona()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('Datos recibidos:', datos);
          this.personas = datos;
        },
        error: (err) => {
          console.error('Error al obtener personas:', err);
          this.toastr.error('Error al cargar las personas', 'Error');
        },
        complete: () => {
          this.loading = false;
        },
      });
  }

  /**
   * Abre el modal para crear o editar una persona
   * @param persona Persona a editar (opcional)
   */
  abrirModalEditar(persona?: Persona): void {
    // Si no hay persona, se abre en modo creación
    if (!persona?.idPersona) {
      this.personaSeleccionada = null;
      this.abrirModal();
      return;
    }

    // Obtiene la persona completa antes de editar
    this.personaService.obtnerPersonaPorID(persona.idPersona).subscribe({
      next: (data) => {
        console.log('PERSONA DETALLADA RECIBIDA:', data);
        this.personaSeleccionada = data;
        this.abrirModal();
      },
      error: (err) => {
        console.error('Error al cargar persona:', err);
        this.toastr.error('No se pudo cargar la persona', 'Error');
      },
    });
  }

  /**
   * Abre el modal principal de persona
   */
  private abrirModal(): void {
    setTimeout(() => {
      const modalEl = this.modalElement?.nativeElement;
      if (modalEl) {
        this.modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
        this.modalInstance.show();
      }
    }, 0);
  }

  /**
   * Abre el modal de detalle de persona
   * @param persona Persona seleccionada
   */
  abrirModalDetalle(persona: Persona): void {
    if (!persona.idPersona) return;

    console.log('persona que llega al modal detalle :', persona);
    this.personaService.obtenerRolesPorPersona(persona.idPersona).subscribe({
      next: (roles) => {
        this.personaDetalle = { ...persona, roles };
        const modal = new bootstrap.Modal(this.modalDetalle.nativeElement);
        modal.show();

        // Formateo de textos para visualización
        this.tipoPersonaFormateado = this.formatearTipoPersona(
          this.personaDetalle.nombreTipoPersona,
        );
        this.TipoDocumentoFormateado = this.formatearTipoDocumento(
          this.personaDetalle.nombreTipoDocumento,
        );
      },
      error: (err) => {
        console.error('Error al cargar roles:', err);
        this.toastr.error('No se pudieron cargar los roles', 'Error');
      },
    });
  }

  /**
   * Cierra el modal y recarga la lista
   */
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerListaPersona();
    this.personaSeleccionada = null;
  }

  /**
   * Elimina una persona previa confirmación
   * @param id ID de la persona
   */
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
        this.personaService
          .eliminarPersona(id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.toastr.success('Persona eliminada correctamente', 'Éxito');
              this.obtenerListaPersona();
            },
            error: (err) => {
              console.error('Error al eliminar persona:', err);
              this.toastr.error('No se pudo eliminar la persona', 'Error');
            },
          });
      }
    });
  }

  /**
   * Indica si la persona tiene roles asignados
   */
  get tieneRoles(): boolean {
    return !!this.personaDetalle?.roles?.length;
  }

  /** Paginación */
  personaPorPagina = 6;
  paginaActual = 1;

  /** Total de páginas */
  get totalPaginas(): number {
    return Math.ceil(this.personas.length / this.personaPorPagina);
  }

  /** Personas correspondientes a la página actual */
  get personasPaginadas(): Persona[] {
    const inicio = (this.paginaActual - 1) * this.personaPorPagina;
    return this.personas.slice(inicio, inicio + this.personaPorPagina);
  }

  /**
   * Cambia la página del listado
   * @param pagina Número de página
   */
  cambiarPagina(pagina: number): void {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
  }

  /**
   * Formatea el tipo de persona para mostrarlo legible
   */
  formatearTipoPersona(tipo: any): string {
    if (!tipo) return '';
    return String(tipo)
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }

  /**
   * Formatea el tipo de documento para mostrarlo legible
   */
  formatearTipoDocumento(tipoDocumento: any): string {
    if (!tipoDocumento) return '';
    return String(tipoDocumento)
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }
  formatearRol(rol: any): string {
    if (!rol) return '';
 
   return String(rol)
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }
}
