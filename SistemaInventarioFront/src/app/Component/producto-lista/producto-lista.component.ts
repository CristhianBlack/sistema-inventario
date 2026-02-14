import { Component, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { EstadoStock } from 'src/app/Modelos/estado-stock';
import { Producto } from 'src/app/Modelos/producto';
import { ProductoService } from 'src/app/Servicios/producto.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-producto-lista',
  templateUrl: './producto-lista.component.html',
  styleUrls: ['./producto-lista.component.css'],
})
export class ProductoListaComponent {
  /** Listado de productos */
  productos: Producto[] = [];

  /** Producto seleccionado para ver detalle */
  productoDetalle: Producto | null = null;

  /** Enum de estado de stock (usado en la vista) */
  EstadoStock = EstadoStock;

  /** Subject para manejar la destrucción de suscripciones */
  private destroy$ = new Subject<void>();

  /** Instancia del modal de Bootstrap */
  private modalInstance: any;

  /** Producto seleccionado para crear o editar */
  prodcutoSeleccionado: Producto | null = null;

  /** Indicador de carga */
  loading = false;

  /** Referencia al modal de creación / edición */
  @ViewChild('modalProducto') modalElement!: ElementRef;

  /** Referencia al modal de detalle */
  @ViewChild('modalDetalle') modalDetalle!: ElementRef;

  /**
   * Constructor del componente
   * @param productoService Servicio de productos
   * @param toastr Servicio de notificaciones
   * @param router Servicio de navegación
   */
  constructor(
    private productoService: ProductoService,
    private toastr: ToastrService,
    private router: Router,
  ) {}

  /**
   * Ciclo de vida
   * Se ejecuta al inicializar el componente
   */
  ngOnInit(): void {
    this.obtenerListaProductos();
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
   * Libera recursos y suscripciones
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * Obtiene la lista de productos desde el backend
   */
  obtenerListaProductos(): void {
    this.loading = true;
    this.productoService
      .obtenerListaProductos()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('Datos recibidos:', datos);
          this.productos = datos;
        },
        error: (err) => {
          console.error('Error al obtener productos:', err);
          this.toastr.error('Error al cargar las productos', 'Error');
        },
        complete: () => {
          this.loading = false;
        },
      });
  }

  /**
   * Abre el modal para crear o editar un producto
   * @param producto Producto a editar (opcional)
   */
  abrirModalEditar(producto?: Producto): void {
    // Si no hay producto, se abre en modo creación
    if (!producto?.idProducto) {
      this.prodcutoSeleccionado = null;
      this.abrirModal();
      return;
    }

    // Obtiene el producto completo antes de editar
    this.productoService.obtenerProductoPorId(producto.idProducto).subscribe({
      next: (data) => {
        console.log('PRODUCTO DETALLADO RECIBIDA:', data);
        this.prodcutoSeleccionado = data;
        this.abrirModal();
      },
      error: (err) => {
        console.error('Error al cargar producto:', err);
        this.toastr.error('No se pudo cargar el producto', 'Error');
      },
    });
  }

  /**
   * Abre el modal principal de producto
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
   * Abre el modal de detalle del producto
   * @param producto Producto seleccionado
   */
  abrirModalDetalle(producto: Producto): void {
    if (!producto.idProducto) return;

    this.productoService.obtenerProductoPorId(producto.idProducto).subscribe({
      next: (data) => {
        console.log('PRODUCTO DETALLADO RECIBIDA:', data);
        this.productoDetalle = { ...producto };
        const modal = new bootstrap.Modal(this.modalDetalle.nativeElement);
        modal.show();
      },
      error: (err) => {
        console.error('Error al cargar producto:', err);
        this.toastr.error('No se pudo cargar el producto', 'Error');
      },
    });
  }

  /**
   * Cierra el modal y actualiza el listado
   */
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerListaProductos();
    this.prodcutoSeleccionado = null;
  }

  /**
   * Elimina un producto previa confirmación
   * @param id ID del producto
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
        this.productoService
          .eliminarProducto(id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.toastr.success('Producto eliminado correctamente', 'Éxito');
              this.obtenerListaProductos();
            },
            error: (err) => {
              console.error('Error al eliminar producto:', err);
              this.toastr.error('No se pudo eliminar la producto', 'Error');
            },
          });
      }
    });
  }

  /**
   * Navega al Kardex del producto
   * @param id ID del producto
   */
  verKardex(id: number): void {
    console.log('ID enviado al Kardex:', id);
    this.router.navigate(['/dashboard/Kardex', id]);
  }

  /** Paginación */
  productoPorPagina = 6;
  paginaActual = 1;

  /** Total de páginas */
  get totalPaginas(): number {
    return Math.ceil(this.productos.length / this.productoPorPagina);
  }

  /** Productos correspondientes a la página actual */
  get productosPaginadas(): Producto[] {
    const inicio = (this.paginaActual - 1) * this.productoPorPagina;
    return this.productos.slice(inicio, inicio + this.productoPorPagina);
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
   * Calcula el valor total del inventario
   */
  get totalInventario(): number {
    return this.productos.reduce((total, p) => {
      return total + p.stock * p.costoPromedio;
    }, 0);
  }

  /**
   * Formatea el estado del stock (enum) para mostrarlo legible
   * @param valor Estado recibido desde la BD
   */
  formatearEstado(valor: string | undefined): string {
    if (!valor) return '';
    return valor
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }
}
