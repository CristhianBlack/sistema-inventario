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
export class TipoDocumentoListaComponent implements OnInit {

  /** Lista de tipos de documento */
  tipoDocumentos: TipoDocumento[] = [];

  /** Tipo de documento seleccionado para crear o editar */
  tipoDocumentoSeleccionado: TipoDocumento | null = null;

  /** Subject para manejar la destrucción de suscripciones */
  private destroy$ = new Subject<void>();

  /** Instancia del modal de Bootstrap */
  private modalInstance: any;

  /** Indicador de carga para la vista */
  loading = false;

  /** Referencia al elemento HTML del modal */
  @ViewChild('modalTipoDocumento') modalElement!: ElementRef;

  constructor(
    private tipoDocumentoService: TipoDocumentoService,
    private toastr: ToastrService
  ) {}

  /**
   * Ciclo de vida: se ejecuta al iniciar el componente
   * Carga la lista de tipos de documento
   */
  ngOnInit(): void {
    this.obtenerTipoDocumentos();
  }

  /**
   * Ciclo de vida: se ejecuta cuando la vista está completamente renderizada
   * Inicializa el modal de Bootstrap una sola vez
   */
  ngAfterViewInit(): void {
    if (this.modalElement?.nativeElement) {
      this.modalInstance = bootstrap.Modal.getOrCreateInstance(
        this.modalElement.nativeElement
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
   * Obtiene la lista de tipos de documento desde el backend
   */
  obtenerTipoDocumentos(): void {
    this.loading = true;
    this.tipoDocumentoService
      .obtenerListaTipoDocumento()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('Datos recibidos:', datos);
          this.tipoDocumentos = datos;
        },
        error: (err) => {
          console.error('Error al obtener tipos de documento:', err);
          this.toastr.error('Error al cargar los tipos de documentos', 'Error');
        },
        complete: () => {
          this.loading = false;
        }
      });
  }

  /**
   * Abre el modal para crear o editar un tipo de documento
   * Si se recibe uno existente, se clona para evitar modificar el original
   */
  abrirModalEditar(tipoDocumento?: TipoDocumento): void {
    this.tipoDocumentoSeleccionado = tipoDocumento ? { ...tipoDocumento } : null;

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
   * Cierra el modal y recarga la lista de tipos de documento
   */
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerTipoDocumentos();
    this.tipoDocumentoSeleccionado = null;
  }

  /**
   * Elimina un tipo de documento por ID con confirmación del usuario
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
        this.tipoDocumentoService
          .eliminarTipoDocumento(id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.toastr.success(
                'Tipo de documento eliminado correctamente',
                'Éxito'
              );
              this.obtenerTipoDocumentos();
            },
            error: (err) => {
              console.error('Error al eliminar tipo de documento:', err);
              this.toastr.error(
                'No se pudo eliminar el tipo de documento',
                'Error'
              );
            },
          });
      }
    });
  }

  /**
   * Formatea valores tipo ENUM recibidos desde la base de datos
   * Ejemplo: DOCUMENTO_IDENTIDAD → Documento Identidad
   */
  formatearTipoDocumento(valor: string | undefined): string {
    if (!valor) return '';

    return valor
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, letra => letra.toUpperCase());
  }

}
