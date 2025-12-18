import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { FormaPago } from 'src/app/Modelos/forma-pago';
import { FormaPagoService } from 'src/app/Servicios/forma-pago.service';

@Component({
  selector: 'app-forma-pago-form',
  templateUrl: './forma-pago-form.component.html',
  styleUrls: ['./forma-pago-form.component.css']
})
export class FormaPagoFormComponent {

   // Recibe una forma de pago cuando se va a editar (puede venir vacía si es "crear")
    @Input() formaPago?: FormaPago | null = null;
  
    // Emite un evento al guardar, para que el padre (lista) actualice la tabla
    @Output() formGuardado = new EventEmitter<void>();
  
    // Modelo interno usado en el formulario (para no alterar directamente el @Input)
    formModel : FormaPago = new FormaPago();
  
    constructor(
      private formaPagoService : FormaPagoService,
      private toastr : ToastrService
    ){}
  
    ngOnInit(): void {
      // Si se recibe una forma de pago por Input, la clonamos en el modelo local
      if(this.formaPago){
        this.formModel = {...this.formaPago};
      }
    }
  
    //  Detecta cuando cambia el Input() y actualiza el modelo
    //  Detecta cambios en el input y actualiza el formulario
    ngOnChanges(changes : SimpleChanges): void{
      if(changes['formaPago'] && this.formaPago){
        this.formModel = {...this.formaPago};
      }else{
        this.formModel = new FormaPago();
      }
    }
  
    onSubmit(formFormaPago : NgForm): void{
      if(this.formModel.idFormaPago){
        // Editamos nuestra ciudad
        this.formaPagoService.editarFormaPago(this.formModel.idFormaPago, this.formModel).subscribe({
          next: () =>{
            this.toastr.success('Forma de pago actualizada correctamente', 'Éxito');
            // Emite el evento hacia el componente padre.
            this.formGuardado.emit();
            this.limpiarFormulario(formFormaPago); // limpiar al editar
          },
          error: (err) =>{
            this.toastr.error('Error al editar la forma de pago', 'Error');
          }
        });
      }
    }
  
    limpiarFormulario(formFormaPago?: NgForm): void {
    this.formModel = new FormaPago(); //  reinicia el modelo
    formFormaPago?.resetForm();       //  limpia visualmente los inputs
  }
  
}
