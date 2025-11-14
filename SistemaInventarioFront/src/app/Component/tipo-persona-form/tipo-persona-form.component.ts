import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { TipoPersona } from 'src/app/Modelos/tipo-persona';
import { TipoPersonaService } from 'src/app/Servicios/tipo-persona.service';

@Component({
  selector: 'app-tipo-persona-form',
  templateUrl: './tipo-persona-form.component.html',
  styleUrls: ['./tipo-persona-form.component.css']
})
export class TipoPersonaFormComponent implements OnInit{

    // Recibe una tipo de documento cuando se va a editar (puede venir vacía si es "crear")
      @Input() tipoPersona?: TipoPersona | null = null;
    
      // Emite un evento al guardar, para que el padre (lista) actualice la tabla
      @Output() formGuardado = new EventEmitter<void>();
    
      // Modelo interno usado en el formulario (para no alterar directamente el @Input)
      formModel : TipoPersona = new TipoPersona();

      constructor(
        private tipoPersonaService : TipoPersonaService,
        private toastr : ToastrService
      ){}

  ngOnInit(): void {
      // Si se recibe una ciudad por Input, la clonamos en el modelo local
      if(this.tipoPersona){
        this.formModel = {...this.tipoPersona};
      }
    }
  
    //  Detecta cuando cambia el Input() y actualiza el modelo
      //  Detecta cambios en el input y actualiza el formulario
      ngOnChanges(changes : SimpleChanges): void{
        if(changes['tipoPersona'] && this.tipoPersona){
          this.formModel = {...this.tipoPersona};
        }else{
          this.formModel = new TipoPersona();
        }
      }
  
      onSubmit(formTipoPersona : NgForm): void{
          if(this.formModel.idTipoPersona){
            // Editamos nuestra tipo persona
            this.tipoPersonaService.editarTipoPersona(this.formModel.idTipoPersona, this.formModel).subscribe({
              next: () =>{
                this.toastr.success('Tipo de persona actualizada correctamente', 'Éxito');
                // Emite el evento hacia el componente padre.
                this.formGuardado.emit();
                this.limpiarFormulario(formTipoPersona); // limpiar al editar
              },
              error: (err) =>{
                this.toastr.error('Error al editar el tipo de persona', 'Error');
              }
            });
          }else{
            // Creamos nuestra ciudad
            console.log(' Datos que se envían:', this.formModel);
            this.tipoPersonaService.agregarTipoPersona(this.formModel).subscribe({
              next: () =>{
                this.toastr.success('Tipo de persona agregada correctamente', 'Éxito');
                // Emite el evento hacia el componente padre.
                this.formGuardado.emit();
                this.limpiarFormulario(formTipoPersona); //  limpiar al editar
              },
              error: (err) =>{
                this.toastr.error(err.message, 'Error'); // muestra el mensaje del backend
              }
            });
          }
        }
      
        limpiarFormulario(formTipoPersona?: NgForm): void {
        this.formModel = new TipoPersona(); //  reinicia el modelo
        formTipoPersona?.resetForm();       //  limpia visualmente los inputs
      }

}
