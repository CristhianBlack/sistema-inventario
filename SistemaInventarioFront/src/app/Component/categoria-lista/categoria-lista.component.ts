/*// Importaciones necesarias desde Angular y RxJS
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Categoria } from 'src/app/Modelos/categoria';
import { CategoriaService } from 'src/app/Servicios/categoria.service';
import { Subject, takeUntil } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';


declare var bootstrap: any; // ✅ le dice a TypeScript que "bootstrap" existe en el entorno global

@Component({
  selector: 'app-categoria-lista',
  templateUrl: './categoria-lista.component.html',
  styleUrls: ['./categoria-lista.component.css']
})
export class CategoriaListaComponent implements OnInit, OnDestroy {
  // ✅ Arreglo que almacenará las categorías obtenidas del servicio
  categorias: Categoria[] = [];
  categoriaSeleccionada?: Categoria | null = null;

  // ✅ Subject utilizado para limpiar las suscripciones al destruir el componente
  private destroy$ = new Subject<void>();

  private modalInstance: any;

  // ✅ Bandera que sirve para mostrar un loader o spinner mientras se cargan los datos
  loading = false;

  // ✅ Inyección de dependencias: el servicio y el enrutador
  constructor(
    private categoriaService: CategoriaService,
    private router: Router,
    private toastr: ToastrService // ✅ Inyectamos el servicio de Toastr
  ) {}

  // ✅ Método del ciclo de vida que se ejecuta al inicializar el componente
  ngOnInit(): void {
    console.log('✅ CategoriaListaComponent inicializado');
    this.obtenerCategorias(); // Llamamos al método que trae los datos desde el backend
  }

  // ✅ Método del ciclo de vida que se ejecuta cuando el componente se destruye
  // Se usa para cerrar suscripciones y liberar memoria
  ngOnDestroy(): void {
    this.destroy$.next();   // Emite una señal de destrucción
    this.destroy$.complete(); // Completa el observable para evitar fugas de memoria
  }

  // ✅ Método para obtener la lista de categorías desde el servicio
  obtenerCategorias(): void {
    this.loading = true; // Activamos el loader mientras llega la información

    this.categoriaService.obtenerListaCategoria()
      .pipe(takeUntil(this.destroy$)) // Se cancela la suscripción si el componente se destruye
      .subscribe({
        next: (datos) => {
          // Se ejecuta cuando la respuesta llega correctamente
          console.log('📦 Datos recibidos:', datos);
          this.categorias = datos;
        },
        error: (err) => {
          // Manejo de errores en caso de fallo
          console.error('❌ Error al obtener las categorías:', err);
          this.toastr.error('Ocurrió un error al cargar las categorías', 'Error');
        },
        complete: () => {
          // Se ejecuta al finalizar el observable
          this.loading = false; // Desactivamos el loader
        }
      });
  }

  abrirModalEditar(categoria?: Categoria) {
  this.categoriaSeleccionada = categoria ? { ...categoria } : new Categoria();
  const modal = new bootstrap.Modal(document.getElementById('modalCategoria'));
  modal.show();
}

onFormGuardado(): void {
    // Cierra el modal usando Bootstrap nativo
    if (this.modalInstance) this.modalInstance.hide();

    // Esperamos que se cierre para refrescar la lista
    setTimeout(() => this.obtenerCategorias(), 300);
  }

  cerrarModalYActualizarLista(): void {
  const modalEl = document.getElementById('modalCategoria');

  // ✅ Intentamos obtener la instancia del modal
  let modal;
  if (bootstrap.Modal.getInstance) {
    modal = bootstrap.Modal.getInstance(modalEl);
  } else if (bootstrap.Modal.getOrCreateInstance) {
    modal = bootstrap.Modal.getOrCreateInstance(modalEl);
  } else {
    modal = new bootstrap.Modal(modalEl);
  }

  // ✅ Cerramos el modal
  modal.hide();

  // ✅ Eliminamos manualmente cualquier backdrop residual
  const backdrop = document.querySelector('.modal-backdrop');
  if (backdrop) {
    backdrop.remove();
  }

  // ✅ Quitamos la clase que bloquea el scroll
  document.body.classList.remove('modal-open');
  document.body.style.overflow = 'auto';

  // ✅ Actualizamos la lista y reseteamos el formulario
  this.obtenerCategorias();
  this.categoriaSeleccionada = null;
}

onModalClosed(): void {
  // Limpia la selección cuando el modal se cierra
  this.categoriaSeleccionada = null;

  // Asegura que no quede el fondo bloqueado
  const backdrop = document.querySelector('.modal-backdrop');
  if (backdrop) backdrop.remove();

  document.body.classList.remove('modal-open');
  document.body.style.overflow = 'auto';
}


  // ✅ Método para navegar hacia la vista de edición de una categoría específica
  editarCategoria(id: number): void {
    // Navegamos a la ruta que corresponde al formulario de edición
    this.router.navigate(['Categoria', id]);
  }

  // ✅ Método para eliminar una categoría seleccionada
  eliminarCategoria(id: number): void {
  // Mostramos la ventana de confirmación
    Swal.fire({
    title: '¿Eliminar categoría?',
    text: 'Esta acción no se puede deshacer.',
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Sí, eliminar',
    cancelButtonText: 'Cancelar',
    confirmButtonColor: '#d33',
    cancelButtonColor: '#6c757d',
    reverseButtons: true
  }).then((resultado) => {
    if (resultado.isConfirmed) {
      // ✅ Si el usuario confirma, eliminamos
      this.categoriaService.eliminarCategoria(id)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: () => {
            this.toastr.success('Categoría eliminada correctamente', 'Éxito ✅');
            this.obtenerCategorias();
          },
          error: (err) => {
            console.error('Error al eliminar la categoría:', err);
            this.toastr.error('No se pudo eliminar la categoría', 'Error ❌');
          }
        });
    }
  });
 }
}*/

/*import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { Categoria } from 'src/app/Modelos/categoria';
import { CategoriaService } from 'src/app/Servicios/categoria.service';
import { Subject, takeUntil } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';

declare var bootstrap: any;

@Component({
  selector: 'app-categoria-lista',
  templateUrl: './categoria-lista.component.html',
  styleUrls: ['./categoria-lista.component.css']
})
export class CategoriaListaComponent implements OnInit, AfterViewInit, OnDestroy {
  categorias: Categoria[] = [];
  categoriaSeleccionada: Categoria | null = null;
  loading = false;

  private destroy$ = new Subject<void>();
  private modalInstance: any;
  private modalEl: HTMLElement | null = null;

  constructor(
    private categoriaService: CategoriaService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    console.log('✅ CategoriaListaComponent inicializado');
    this.obtenerCategorias();
  }

 ngAfterViewInit(): void {
  this.modalEl = document.getElementById('modalCategoria');

  // Espera a que bootstrap esté disponible antes de usarlo
  setTimeout(() => {
    if (this.modalEl && (window as any).bootstrap?.Modal) {
      const bs = (window as any).bootstrap;

      // ✅ Crea o recupera la instancia del modal
      this.modalInstance = bs.Modal.getOrCreateInstance(this.modalEl);

      // ✅ Escucha el cierre del modal
      this.modalEl.addEventListener('hidden.bs.modal', () => {
        this.categoriaSeleccionada = null;
        (document.activeElement as HTMLElement)?.blur();
      });

      console.log('✅ Bootstrap Modal inicializado correctamente');
    } else {
      console.error('❌ Bootstrap Modal no disponible en el contexto global');
    }
  }, 0); // 🔹 Delay mínimo para garantizar carga de bootstrap.bundle
}

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  obtenerCategorias(): void {
    this.loading = true;
    this.categoriaService.obtenerListaCategoria()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('📦 Datos recibidos:', datos);
          this.categorias = datos;
        },
        error: (err) => {
          console.error('❌ Error al obtener las categorías:', err);
          this.toastr.error('Ocurrió un error al cargar las categorías', 'Error');
        },
        complete: () => { this.loading = false; }
      });
  }

  abrirModalEditar(categoria?: Categoria): void {
    this.categoriaSeleccionada = categoria ? { ...categoria } : new Categoria();
    if (this.modalInstance) this.modalInstance.show();
  }

  onFormGuardado(): void {
    // Cierra el modal usando Bootstrap nativo
    if (this.modalInstance) this.modalInstance.hide();

    // Esperamos que se cierre para refrescar la lista
    setTimeout(() => this.obtenerCategorias(), 300);
  }

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
      reverseButtons: true
    }).then((res) => {
      if (res.isConfirmed) {
        this.categoriaService.eliminarCategoria(id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.toastr.success('Categoría eliminada correctamente', 'Éxito ✅');
              this.obtenerCategorias();
            },
            error: (err) => {
              console.error('Error al eliminar la categoría:', err);
              this.toastr.error('No se pudo eliminar la categoría', 'Error ❌');
            }
          });
      }
    });
  }
}*/

import { Component, OnInit, OnDestroy, AfterViewInit, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { Categoria } from 'src/app/Modelos/categoria';
import { CategoriaService } from 'src/app/Servicios/categoria.service';
import { Subject, takeUntil } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-categoria-lista',
  templateUrl: './categoria-lista.component.html',
  styleUrls: ['./categoria-lista.component.css']
})
export class CategoriaListaComponent implements OnInit, OnDestroy, AfterViewInit {
  categorias: Categoria[] = [];
  categoriaSeleccionada: Categoria | null = null;

  private destroy$ = new Subject<void>();
  private modalInstance: any;

  loading = false;

  // Referencia al elemento HTML del modal
  @ViewChild('modalCategoria') modalElement!: ElementRef;

  constructor(
    private categoriaService: CategoriaService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    console.log('CategoriaListaComponent inicializado');
    this.obtenerCategorias();
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

  // Cargar lista de categorías
  obtenerCategorias(): void {
    this.loading = true;
    this.categoriaService.obtenerListaCategoria()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log(' Datos recibidos:', datos);
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

  // Abrir modal para crear o editar categoría
  abrirModalEditar(categoria?: Categoria): void {
    this.categoriaSeleccionada = categoria ? { ...categoria } : null;

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

  // Cerrar modal y refrescar lista
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerCategorias();
    this.categoriaSeleccionada = null;
  }

  // Eliminar una categoría
  eliminarCategoria(id: number): void {
    console.log("ID recibido para eliminar:", id);
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
              this.toastr.success('Categoría eliminada correctamente', 'Éxito');
              this.obtenerCategorias();
            },
            error: (err) => {
              console.error('Error al eliminar categoría:', err);
              this.toastr.error('No se pudo eliminar la categoría', 'Error');
            }
          });
      }
    });
  }

  compraPorPagina = 6;
paginaActual = 1;

get totalPaginas(): number {
  return Math.ceil(this.categorias.length / this.compraPorPagina);
}

get categoriasPaginadas() {
  const inicio = (this.paginaActual - 1) * this.compraPorPagina;
  return this.categorias.slice(inicio, inicio + this.compraPorPagina);
}

cambiarPagina(pagina: number) {
  if (pagina < 1 || pagina > this.totalPaginas) return;
  this.paginaActual = pagina;
}
}
