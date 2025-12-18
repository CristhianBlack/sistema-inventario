import { Component, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { Producto } from 'src/app/Modelos/producto';
import { ProductoService } from 'src/app/Servicios/producto.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-producto-lista',
  templateUrl: './producto-lista.component.html',
  styleUrls: ['./producto-lista.component.css']
})
export class ProductoListaComponent {

  productos : Producto[] = [];
  productoDetalle: Producto | null = null;
  
    private destroy$ = new Subject<void>();
    private modalInstance: any;
  
    prodcutoSeleccionado : Producto | null = null;
  
    loading = false;
  
    // Referencia al elemento HTML del modal
      @ViewChild('modalProducto') modalElement!: ElementRef;
      @ViewChild('modalDetalle') modalDetalle!: ElementRef;
  
    constructor(
      private productoService : ProductoService,
      private toastr : ToastrService,
      private router : Router
    ){}
  
    ngOnInit(): void {
      this.obtenerListaProductos();
    }
  
    ngAfterViewInit(): void {
      // Inicializamos el modal una sola vez cuando la vista está lista
      if (this.modalElement?.nativeElement) {
        this.modalInstance = bootstrap.Modal.getOrCreateInstance(
          this.modalElement.nativeElement
        );
      }
    }
  
    // Método del ciclo de vida que se ejecuta cuando el componente se destruye
    // Se usa para cerrar suscripciones y liberar memoria
    ngOnDestroy(): void {
      this.destroy$.next();
      this.destroy$.complete();
    }
  
    obtenerListaProductos(): void{
      this.loading = true;
      this.productoService.obtenerListaProductos().pipe(takeUntil(this.destroy$)).subscribe({
        next: (datos) =>{
          console.log('Datos recibidos:', datos);
          this.productos = datos;
        },
        error: (err) =>{
          console.error('Error al obtener productos:', err);
          this.toastr.error('Error al cargar las productos', 'Error');
        },
        complete: () =>{
          this.loading = false
        }
      });
    }
  
  abrirModalEditar(producto?: Producto): void {
  
    if (!producto?.idProducto) {
      this.prodcutoSeleccionado = null;
      this.abrirModal(); 
      return;
    }
  
    this.productoService.obtenerProductoPorId(producto.idProducto).subscribe({
      next: (data) => {
        console.log("PRODUCTO DETALLADO RECIBIDA:", data);
  
        this.prodcutoSeleccionado = data; // 🔥 ahora sí con todos los objetos
        this.abrirModal();
      },
      error: (err) => {
        console.error("Error al cargar producto:", err);
        this.toastr.error("No se pudo cargar el producto", "Error");
      }
    });
  }
  
  private abrirModal(): void {
    setTimeout(() => {
      const modalEl = this.modalElement?.nativeElement;
      if (modalEl) {
        this.modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
        this.modalInstance.show();
      }
    }, 0);
  }
  
      // Abrir modal del detalle
     abrirModalDetalle(producto: Producto): void {
    if (!producto.idProducto) return;
  
    this.productoService.obtenerProductoPorId(producto.idProducto).subscribe({
      next: (data) => {
        console.log("PRODUCTO DETALLADO RECIBIDA:", data);
        this.productoDetalle = { ...producto };
        const modal = new bootstrap.Modal(this.modalDetalle.nativeElement);
        modal.show();
      },
      error: (err) => {
        console.error("Error al cargar producto:", err);
        this.toastr.error("No se pudo cargar el producto", "Error");
      }
    });
  }
    
      // Cerrar modal y refrescar lista
      cerrarModalYActualizarLista(): void {
        if (this.modalInstance) {
          this.modalInstance.hide();
        }
        this.obtenerListaProductos();
        this.prodcutoSeleccionado = null;
      }
  
    // Eliminar una persona
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
            this.productoService.eliminarProducto(id).pipe(takeUntil(this.destroy$)).subscribe({
                next: () => {
                  this.toastr.success(
                    'Producto eliminado correctamente','Éxito'
                  );
                  this.obtenerListaProductos();
                },
                error: (err) => {
                  console.error('Error al eliminar producto:', err);
                  this.toastr.error('No se pudo eliminar la producto', 'Error ');
                },
              });
          }
        });
      }

  verKardex(id: number) {
    console.log('ID enviado al Kardex:', id);
  this.router.navigate(['/Kardex', id]);
  }
}
  
