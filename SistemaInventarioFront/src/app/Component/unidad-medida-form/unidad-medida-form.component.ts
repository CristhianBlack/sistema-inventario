import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { UnidadMedida } from 'src/app/Modelos/unidad-medida';
import { UnidadMedidaService } from 'src/app/Servicios/unidad-medida.service';

@Component({
  selector: 'app-unidad-medida-form',
  templateUrl: './unidad-medida-form.component.html',
  styleUrls: ['./unidad-medida-form.component.css']
})
export class UnidadMedidaFormComponent implements OnInit{
   // Recibe una unidad cuando se va a editar (puede venir vacía si es "crear")
    @Input() unidadMedida?: UnidadMedida | null = null;
  
    // Emite un evento al guardar, para que el padre (lista) actualice la tabla
    @Output() formGuardado = new EventEmitter<void>();
  
    // Modelo interno usado en el formulario (para no alterar directamente el @Input)
    formModel : UnidadMedida = new UnidadMedida();
  
    constructor(
      private unidadMedidaService : UnidadMedidaService,
      private toastr : ToastrService
    ){}
  
    ngOnInit(): void {
      // Si se recibe una unidad por Input, la clonamos en el modelo local
      if(this.unidadMedida){
        this.formModel = {...this.unidadMedida};
      }
    }
  
    //  Detecta cuando cambia el Input() y actualiza el modelo
    //  Detecta cambios en el input y actualiza el formulario
    ngOnChanges(changes : SimpleChanges): void{
      if(changes['unidadMedida'] && this.unidadMedida){
        this.formModel = {...this.unidadMedida};
      }else{
        this.formModel = new UnidadMedida();
      }
    }
  
    onSubmit(formUnidadMedida : NgForm): void{
      if(this.formModel.idUnidadMedida){
        // Editamos nuestra unidad
        this.unidadMedidaService.editarUnidadMedida(this.formModel.idUnidadMedida, this.formModel).subscribe({
          next: () =>{
            this.toastr.success('Unidad Medida actualizada correctamente', 'Éxito');
            // Emite el evento hacia el componente padre.
            this.formGuardado.emit();
            this.limpiarFormulario(formUnidadMedida); // limpiar al editar
          },
          error: (err) =>{
            this.toastr.error('Error al editar la unidad de medida', 'Error');
          }
        });
      }else{
        // Creamos nuestra unidad de medida
        console.log(' Datos que se envían:', this.formModel);
        this.unidadMedidaService.guardarUnidadMedida(this.formModel).subscribe({
          next: () =>{
            this.toastr.success('Unidad de meidad agregada correctamente', 'Éxito');
            // Emite el evento hacia el componente padre.
            this.formGuardado.emit();
            this.limpiarFormulario(formUnidadMedida); //  limpiar al editar
          },
          error: (err) =>{
            this.toastr.error(err.message, 'Error'); // muestra el mensaje del backend
          }
        });
      }
    }
  
    limpiarFormulario(formUnidadMedida?: NgForm): void {
    this.formModel = new UnidadMedida(); //  reinicia el modelo
    formUnidadMedida?.resetForm();       //  limpia visualmente los inputs
  }  
}


