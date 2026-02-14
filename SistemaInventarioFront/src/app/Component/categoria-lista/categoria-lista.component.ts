import { Component, OnInit, OnDestroy, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Categoria } from 'src/app/Modelos/categoria';
import { CategoriaService } from 'src/app/Servicios/categoria.service';
import { Subject, takeUntil } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';

// Permite usar Bootstrap JS (Modal) sin errores de TypeScript
declare var bootstrap: any;

@Component({
  selector: 'app-categoria-lista',
  templateUrl: './categoria-lista.component.html',
  styleUrls: ['./categoria-lista.component.css']
})
export class CategoriaListaComponent
  implements OnInit, OnDestroy, AfterViewInit {

  /** Lista completa de categorías */
  categorias: Categoria[] = [];

  /** Categoría seleccionada para edición */
  categoriaSeleccionada: Categoria | null = null;

  /** Subject para cancelar subscripciones y evitar memory leaks */
  private destroy$ = new Subject<void>();

  /** Instancia del modal de Bootstrap */
  private modalInstance: any;

  /** Indica estado de carga para spinners */
  loading = false;

  /** Referencia al elemento HTML del modal */
  @ViewChild('modalCategoria') modalElement!: ElementRef;

  constructor(
    private categoriaService: CategoriaService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  /**
   * Se ejecuta al inicializar el componente
   * Carga la lista de categorías
   */
  ngOnInit(): void {
    console.log('CategoriaListaComponent inicializado');
    this.obtenerCategorias();
  }

  /**
   * Se ejecuta cuando la vista ya está renderizada
   * Inicializa el modal de Bootstrap una sola vez
   */
  ngAfterViewInit(): void {
    if (this.modalElement?.nativeElement) {
      this.modalInstance =
        bootstrap.Modal.getOrCreateInstance(this.modalElement.nativeElement);
    }
  }

  /**
   * Se ejecuta al destruir el componente
   * Cancela todas las subscripciones activas
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Obtiene la lista de categorías desde el backend
   */
  obtenerCategorias(): void {
    this.loading = true;

    this.categoriaService.obtenerListaCategoria()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('Datos recibidos:', datos);
          this.categorias = datos;
        },
        error: (err) => {
          console.error('Error al obtener categorías:', err);
          this.toastr.error('Error al cargar categorías', 'Error');
        },
        complete: () => {
          this.loading = false;
        }
      });
  }

  /**
   * Abre el modal para crear o editar una categoría
   * @param categoria Categoría opcional para edición
   */
  abrirModalEditar(categoria?: Categoria): void {
    // Se clona para evitar modificar el objeto original
    this.categoriaSeleccionada = categoria ? { ...categoria } : null;

    // Se espera un ciclo del DOM antes de mostrar el modal
    setTimeout(() => {
      const modalEl = this.modalElement?.nativeElement;
      if (modalEl) {
        this.modalInstance =
          bootstrap.Modal.getOrCreateInstance(modalEl);
        this.modalInstance.show();
      } else {
        console.error('No se encontró el modal en el DOM.');
      }
    }, 0);
  }

  /**
   * Cierra el modal y actualiza la lista de categorías
   */
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerCategorias();
    this.categoriaSeleccionada = null;
  }

  /**
   * Elimina una categoría previa confirmación
   * @param id ID de la categoría a eliminar
   */
  eliminarCategoria(id: number): void {
    console.log('ID recibido para eliminar:', id);

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
        this.categoriaService.eliminarCategoria(id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.toastr.success(
                'Categoría eliminada correctamente',
                'Éxito'
              );
              this.obtenerCategorias();
            },
            error: (err) => {
              console.error('Error al eliminar categoría:', err);
              this.toastr.error(
                'No se pudo eliminar la categoría',
                'Error'
              );
            }
          });
      }
    });
  }

  /* =======================
     PAGINACIÓN
     ======================= */

  /** Cantidad de registros por página */
  compraPorPagina = 6;

  /** Página actual */
  paginaActual = 1;

  /** Total de páginas calculadas */
  get totalPaginas(): number {
    return Math.ceil(this.categorias.length / this.compraPorPagina);
  }

  /** Lista de categorías según la página actual */
  get categoriasPaginadas() {
    const inicio =
      (this.paginaActual - 1) * this.compraPorPagina;
    return this.categorias.slice(
      inicio,
      inicio + this.compraPorPagina
    );
  }

  /**
   * Cambia la página actual
   * @param pagina Número de página
   */
  cambiarPagina(pagina: number) {
    if (pagina < 1 || pagina > this.totalPaginas) return;
    this.paginaActual = pagina;
  }
}