import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Impuesto } from 'src/app/Modelos/impuesto';
import { ImpuestoService } from 'src/app/Servicios/impuesto.service';

@Component({
  selector: 'app-impuesto-form',
  templateUrl: './impuesto-form.component.html',
  styleUrls: ['./impuesto-form.component.css']
})
export class ImpuestoFormComponent implements OnInit{

  // Recibe un impuesto cuando se va a editar (puede venir vacía si es "crear")
      @Input() impuesto?: Impuesto | null = null;
    
      // Emite un evento al guardar, para que el padre (lista) actualice la tabla
      @Output() formGuardado = new EventEmitter<void>();
    
      // Modelo interno usado en el formulario (para no alterar directamente el @Input)
      formModel : Impuesto = new Impuesto();
    
      constructor(
        private impuestoService : ImpuestoService,
        private toastr : ToastrService
      ){}
    
      ngOnInit(): void {
        // Si se recibe una forma de pago por Input, la clonamos en el modelo local
        if(this.impuesto){
          this.formModel = {...this.impuesto};
        }
      }
    
      //  Detecta cuando cambia el Input() y actualiza el modelo
      //  Detecta cambios en el input y actualiza el formulario
      ngOnChanges(changes : SimpleChanges): void{
        if(changes['impuesto'] && this.impuesto){
          this.formModel = {...this.impuesto};
        }else{
          this.formModel = new Impuesto();
        }
      }
    
      onSubmit(): void{
        if(this.formModel.idImpuesto){
          // Editamos nuestra ciudad
          this.impuestoService.buscarImpuestoPorId(this.formModel.idImpuesto).subscribe({
            next: () =>{
              //this.toastr.success('Forma de pago actualizada correctamente', 'Éxito');
              // Emite el evento hacia el componente padre.
              this.formGuardado.emit();
              //this.limpiarFormulario(formFormaPago); // limpiar al editar
            },
            error: (err) =>{
              this.toastr.error('Error al editar la forma de pago', 'Error');
            }
          });
        }
      }
    
      limpiarFormulario(formFormaPago?: NgForm): void {
      this.formModel = new Impuesto(); //  reinicia el modelo
      formFormaPago?.resetForm();       //  limpia visualmente los inputs
    }

    get porcentajeVista(): string {
  return (this.formModel.porcentaje * 100) + ' %';
}
}
