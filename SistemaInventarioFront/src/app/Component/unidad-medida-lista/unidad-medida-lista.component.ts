import { Component, ElementRef, EventEmitter, Input, OnInit, Output, SimpleChanges, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Subject, takeUntil } from 'rxjs';
import { UnidadMedida } from 'src/app/Modelos/unidad-medida';
import { UnidadMedidaService } from 'src/app/Servicios/unidad-medida.service';
import Swal from 'sweetalert2';

// Necesario para usar el JS de Bootstrap (modal)
declare var bootstrap: any;

@Component({
  selector: 'app-unidad-medida-lista',
  templateUrl: './unidad-medida-lista.component.html',
  styleUrls: ['./unidad-medida-lista.component.css']
})
export class UnidadMedidaListaComponent implements OnInit{

 unidades: UnidadMedida[] = [];
 
   unidadSeleccionada: UnidadMedida | null = null;
 
   private destroy$ = new Subject<void>();
   private modalInstance: any;
 
   loading = false;
 
   // Referencia al elemento HTML del modal
   @ViewChild('modalUnidad') modalElement!: ElementRef;
 
   constructor(
     private unidadMedidaService: UnidadMedidaService,
     private router: Router,
     private toastr: ToastrService
   ) {}
 
   ngOnInit(): void {
     this.obtenerUnidades();
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
 
   obtenerUnidades(): void {
     this.loading = true;
     this.unidadMedidaService
       .obtenerListaUnidadMedida()
       .pipe(takeUntil(this.destroy$))
       .subscribe({
         next: (datos) => {
           console.log('Datos recibidos:', datos);
           this.unidades = datos;
         },
         error: (err) => {
           console.error(' Error al obtener las unidades de medida:', err);
           this.toastr.error('Error al cargar unidades de medida', 'Error');
         },
         complete: () => {
           this.loading = false;
         },
       });
   }
 
   //Abrir modal para crear o editar ciudad
   abrirModalEditar(unidadMedida?: UnidadMedida): void {
     this.unidadSeleccionada = unidadMedida ? { ...unidadMedida } : null;
 
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
     this.obtenerUnidades();
     this.unidadSeleccionada = null;
   }
 
   // Eliminar una ciudad
   eliminarCiudad(id: number): void {
     console.log("ID recibido para eliminar:", id);
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
         this.unidadMedidaService.eliminarUnidadMedida(id).pipe(takeUntil(this.destroy$)).subscribe({
             next: () => {
               this.toastr.success(
                 'Unidad Medida eliminada correctamente','Éxito '
               );
               this.obtenerUnidades();
             },
             error: (err) => {
               console.error('Error al eliminar Unidad:', err);
               this.toastr.error('No se pudo eliminar la Unidad', 'Error ');
             },
           });
       }
     });
   }
 }

