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
  styleUrls: ['./proveedor-lista.component.css']
})
export class ProveedorListaComponent {
  
  proveedores : Proveedor[] = [];
  proveedoresPersona: any[] = [];

   
     proveedorSeleccionado: Proveedor | null = null;
   
     private destroy$ = new Subject<void>();
     private modalInstance: any;
   
     loading = false;
   
     // Referencia al elemento HTML del modal
     @ViewChild('modalProveedor') modalElement!: ElementRef;
   
     constructor(
       private proveedorService: ProveedorService,
       private personaService : PersonaService,
       private toastr: ToastrService
     ) {}
   
     ngOnInit(): void {
       this.obtenerProveedores();
     }
   
     ngAfterViewInit(): void {
       // Inicializamos el modal una sola vez cuando la vista está lista
       if (this.modalElement?.nativeElement) {
         this.modalInstance = bootstrap.Modal.getOrCreateInstance(
           this.modalElement.nativeElement
         );
       }
     }
   
     //  Método del ciclo de vida que se ejecuta cuando el componente se destruye
     // Se usa para cerrar suscripciones y liberar memoria
     ngOnDestroy(): void {
       this.destroy$.next();
       this.destroy$.complete();
     }
   
     obtenerProveedores(): void {
       this.loading = true;
       this.proveedorService.listarProveedorPersona()
         .pipe(takeUntil(this.destroy$))
         .subscribe({
           next: (datos) => {
             console.log('Datos recibidos:', datos);
             this.proveedoresPersona = datos;
           },
           error: (err) => {
             console.error(' Error al obtener los proveedores:', err);
             this.toastr.error('Error al cargar los proveedores', 'Error');
           },
           complete: () => {
             this.loading = false;
           },
         });
     }
   
     //Abrir modal para crear o editar ciudad
     abrirModalEditar(proveedor?: Proveedor): void {
       this.proveedorSeleccionado = proveedor ? { ...proveedor } : null;
   
       // Esperar un ciclo del render para asegurar que el modal exista
       setTimeout(() => {
         const modalEl = this.modalElement?.nativeElement;
         if (modalEl) {
           this.modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
           this.modalInstance.show();
         } else {
           console.error(' No se encontró el modal en el DOM.');
         }
       }, 0);
     }
   
     //  Cerrar modal y refrescar lista
     cerrarModalYActualizarLista(): void {
       if (this.modalInstance) {
         this.modalInstance.hide();
       }
       this.obtenerProveedores();
       this.proveedorSeleccionado = null;
     }
   
     // Eliminar una ciudad
     eliminarProveedor(id: number): void {
       console.log("ID recibido para eliminar:", id);
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
           this.proveedorService.eliminarProveedor(id).pipe(takeUntil(this.destroy$)).subscribe({
               next: () => {
                 this.toastr.success(
                   'Ciudad eliminada correctamente','Éxito '
                 );
                 this.obtenerProveedores();
               },
               error: (err) => {
                 console.error('Error al eliminar proveedores:', err);
                 this.toastr.error('No se pudo eliminar los proveedores', 'Error ');
               },
             });
         }
       });
     }

}
