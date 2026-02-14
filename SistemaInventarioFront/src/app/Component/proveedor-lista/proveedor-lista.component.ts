import { Component, ElementRef, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { Persona } from 'src/app/Modelos/persona';
import { Proveedor } from 'src/app/Modelos/proveedor';
import { PersonaService } from 'src/app/Servicios/persona.service';
import { ProveedorService } from 'src/app/Servicios/proveedor.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-proveedor-lista',
  templateUrl: './proveedor-lista.component.html',
  styleUrls: ['./proveedor-lista.component.css'],
})
export class ProveedorListaComponent {
  /** Lista de proveedores */
  proveedores: Proveedor[] = [];

  /** Lista de proveedores relacionados con persona (vista principal) */
  proveedoresPersona: any[] = [];

  /** Proveedor seleccionado para ver detalle */
  proveedorDetalle: Proveedor | null = null;

  /** Proveedor seleccionado para crear o editar */
  proveedorSeleccionado: Proveedor | null = null;

  /** Subject para manejar la destrucción de suscripciones */
  private destroy$ = new Subject<void>();

  /** Instancia del modal de Bootstrap */
  private modalInstance: any;

  /** Indicador de carga para la vista */
  loading = false;

  /** Referencia al modal de creación / edición */
  @ViewChild('modalProveedor') modalElement!: ElementRef;

  /** Referencia al modal de detalle */
  @ViewChild('modalDetalle') modalDetalle!: ElementRef;

  constructor(
    private proveedorService: ProveedorService,
    private personaService: PersonaService,
    private toastr: ToastrService,
  ) {}

  /**
   * Ciclo de vida: se ejecuta al iniciar el componente
   * Carga la lista de proveedores
   */
  ngOnInit(): void {
    this.obtenerProveedores();
  }

  /**
   * Ciclo de vida: se ejecuta cuando la vista ya fue renderizada
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
   * Obtiene la lista de proveedores asociados a persona
   */
  obtenerProveedores(): void {
    this.loading = true;
    this.proveedorService
      .listarProveedorPersona()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('Datos recibidos:', datos);
          this.proveedoresPersona = datos;
        },
        error: (err) => {
          console.error('Error al obtener los proveedores:', err);
          this.toastr.error('Error al cargar los proveedores', 'Error');
        },
        complete: () => {
          this.loading = false;
        },
      });
  }

  /**
   * Abre el modal para crear o editar un proveedor
   * Si se recibe un proveedor, se clona para edición
   */
  abrirModalEditar(proveedor?: Proveedor): void {
    this.proveedorSeleccionado = proveedor ? { ...proveedor } : null;

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
   * Cierra el modal y recarga la lista de proveedores
   */
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerProveedores();
    this.proveedorSeleccionado = null;
  }

  /**
   * Abre el modal de detalle del proveedor
   */
  abrirModalDetalle(proveedor: Proveedor): void {
    if (!proveedor.idProveedor) return;

    this.proveedorService.listarProveedorPersona().subscribe({
      next: (data) => {
        console.log('Proveedor detallado recibido:', data);
        this.proveedorDetalle = { ...proveedor };
        const modal = new bootstrap.Modal(this.modalDetalle.nativeElement);
        modal.show();
      },
      error: (err) => {
        console.error('Error al cargar proveedor:', err);
        this.toastr.error('No se pudo cargar el proveedor', 'Error');
      },
    });
  }

  /**
   * Elimina un proveedor por ID con confirmación
   */
  eliminarProveedor(id: number): void {
    console.log('ID recibido para eliminar:', id);
    Swal.fire({
      title: '¿Eliminar proveedor?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33',
      cancelButtonColor: '#6c757d',
    }).then((resultado) => {
      if (resultado.isConfirmed) {
        this.proveedorService
          .eliminarProveedor(id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.toastr.success('Proveedor eliminado correctamente', 'Éxito');
              this.obtenerProveedores();
            },
            error: (err) => {
              console.error('Error al eliminar proveedor:', err);
              this.toastr.error('No se pudo eliminar el proveedor', 'Error');
            },
          });
      }
    });
  }

  /** Cantidad de proveedores por página */
  proveedorPorPagina = 6;

  /** Página actual de la paginación */
  paginaActual = 1;

  /**
   * Calcula el total de páginas
   */
  get totalPaginas(): number {
    return Math.ceil(this.proveedoresPersona.length / this.proveedorPorPagina);
  }

  /**
   * Devuelve los proveedores paginados para la vista
   */
  get proveedoresPaginadas() {
    const inicio = (this.paginaActual - 1) * this.proveedorPorPagina;
    return this.proveedoresPersona.slice(
      inicio,
      inicio + this.proveedorPorPagina,
    );
  }

  /**
   * Cambia la página actual
   */
  cambiarPagina(pagina: number) {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
  }
}
