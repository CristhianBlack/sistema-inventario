import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { Ciudad } from 'src/app/Modelos/ciudad';
import { CiudadService } from 'src/app/Servicios/ciudad.service';
import Swal from 'sweetalert2';

// 👇 Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-ciudad-lista',
  templateUrl: './ciudad-lista.component.html',
  styleUrls: ['./ciudad-lista.component.css'],
})
export class CiudadListaComponent implements OnInit {
  ciudades: Ciudad[] = [];

  ciudadSeleccionada: Ciudad | null = null;

  private destroy$ = new Subject<void>();
  private modalInstance: any;

  loading = false;

  // 👇 Referencia al elemento HTML del modal
  @ViewChild('modalCiudad') modalElement!: ElementRef;

  constructor(
    private ciudadService: CiudadService,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.obtenerCiudades();
  }

  ngAfterViewInit(): void {
    // Inicializamos el modal una sola vez cuando la vista está lista
    if (this.modalElement?.nativeElement) {
      this.modalInstance = bootstrap.Modal.getOrCreateInstance(
        this.modalElement.nativeElement
      );
    }
  }

  // ✅ Método del ciclo de vida que se ejecuta cuando el componente se destruye
  // Se usa para cerrar suscripciones y liberar memoria
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  obtenerCiudades(): void {
    this.loading = true;
    this.ciudadService
      .obtenerListaciudades()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (datos) => {
          console.log('📦 Datos recibidos:', datos);
          this.ciudades = datos;
        },
        error: (err) => {
          console.error('❌ Error al obtener ciudades:', err);
          this.toastr.error('Error al cargar ciudades', 'Error');
        },
        complete: () => {
          this.loading = false;
        },
      });
  }

  // 🔹 Abrir modal para crear o editar ciudad
  abrirModalEditar(ciudad?: Ciudad): void {
    this.ciudadSeleccionada = ciudad ? { ...ciudad } : null;

    // Esperar un ciclo del render para asegurar que el modal exista
    setTimeout(() => {
      const modalEl = this.modalElement?.nativeElement;
      if (modalEl) {
        this.modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
        this.modalInstance.show();
      } else {
        console.error('⚠️ No se encontró el modal en el DOM.');
      }
    }, 0);
  }

  // 🔹 Cerrar modal y refrescar lista
  cerrarModalYActualizarLista(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }
    this.obtenerCiudades();
    this.ciudadSeleccionada = null;
  }

  // 🔹 Eliminar una categoría
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
        this.ciudadService.eliminarciudad(id).pipe(takeUntil(this.destroy$)).subscribe({
            next: () => {
              this.toastr.success(
                'Ciudad eliminada correctamente','Éxito ✅'
              );
              this.obtenerCiudades();
            },
            error: (err) => {
              console.error('Error al eliminar categoría:', err);
              this.toastr.error('No se pudo eliminar la categoría', 'Error ❌');
            },
          });
      }
    });
  }
}
