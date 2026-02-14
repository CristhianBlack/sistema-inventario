import { Component, ElementRef, ViewChild } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { Usuario } from 'src/app/Modelos/usuario';
import { UsuarioService } from 'src/app/Servicios/usuario.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-usuario-list',
  templateUrl: './usuario-list.component.html',
  styleUrls: ['./usuario-list.component.css'],
})
export class UsuarioListComponent {
  /** Lista completa de usuarios */
  usuarios: Usuario[] = [];

  /** Usuario seleccionado para crear o editar */
  usuarioSeleccionado: Usuario | null = null;

  /** Subject para controlar la destrucción de suscripciones */
  private destroy$ = new Subject<void>();

  /** Instancia del modal de Bootstrap */
  private modalInstance: any;

  /** Indicador de carga */
  loading = false;

  /** Referencia al elemento HTML del modal */
  @ViewChild('modalUsuario') modalElement!: ElementRef;

  constructor(
    private usuarioService: UsuarioService,
    private toastr: ToastrService,
  ) {}

  /**
   * Ciclo de vida: se ejecuta al iniciar el componente
   * Carga la lista de usuarios
   */
  ngOnInit(): void {
    this.obtenerUsuarios();
  }

  /**
   * Ciclo de vida: se ejecuta cuando la vista está renderizada
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
   * Libera recursos y evita fugas de memoria
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Obtiene la lista de usuarios desde el backend
   */
  obtenerUsuarios(): void {
    this.loading = true;
    this.usuarioService
      .obtenerListaUsuarios()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('Datos recibidos:', datos);
          this.usuarios = datos;
        },
        error: (err) => {
          console.error('Error al obtener los usuarios:', err);
          this.toastr.error('Error al cargar los usuarios', 'Error');
        },
        complete: () => {
          this.loading = false;
        },
      });
  }

  /**
   * Abre el modal para crear o editar un usuario
   * Se clona el objeto para evitar modificar el original
   */
  abrirModalEditar(usuario?: Usuario): void {
    this.usuarioSeleccionado = usuario ? { ...usuario } : null;

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
   * Cierra el modal y refresca la lista de usuarios
   */
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerUsuarios();
    this.usuarioSeleccionado = null;
  }

  /**
   * Elimina un usuario por ID con confirmación previa
   */
  eliminarUsuario(id: number): void {
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
        this.usuarioService
          .eliminarUsuario(id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.toastr.success('Proveedor eliminado correctamente', 'Éxito');
              this.obtenerUsuarios();
            },
            error: (err) => {
              console.error('Error al eliminar proveedor:', err);
              this.toastr.error('No se pudo eliminar el proveedor', 'Error');
            },
          });
      }
    });
  }

  /** Cantidad de usuarios mostrados por página */
  usuarioPorPagina = 6;

  /** Página actual */
  paginaActual = 1;

  /**
   * Calcula el total de páginas según la cantidad de usuarios
   */
  get totalPaginas(): number {
    return Math.ceil(this.usuarios.length / this.usuarioPorPagina);
  }

  /**
   * Retorna la lista de usuarios paginada
   */
  get usuariosPaginados() {
    const inicio = (this.paginaActual - 1) * this.usuarioPorPagina;
    return this.usuarios.slice(inicio, inicio + this.usuarioPorPagina);
  }

  /**
   * Cambia la página actual validando límites
   */
  cambiarPagina(pagina: number) {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
  }

  /**
   * Formatea el rol del sistema a un texto legible
   * Ej: ADMIN_SISTEMA → Admin Sistema
   */
  formatearRolSistema(rolSistema: any): string {
    if (!rolSistema) return '';

    return String(rolSistema)
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }
}
