import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Toast, ToastrService } from 'ngx-toastr';
import { Ciudad } from 'src/app/Modelos/ciudad';
import { CiudadService } from 'src/app/Servicios/ciudad.service';

@Component({
  selector: 'app-ciudad-form',
  templateUrl: './ciudad-form.component.html',
  styleUrls: ['./ciudad-form.component.css']
})
export class CiudadFormComponent implements OnInit{

  // Recibe una ciudad cuando se va a editar (puede venir vacía si es "crear")
  @Input() ciudad?: Ciudad | null = null;

  // Emite un evento al guardar, para que el padre (lista) actualice la tabla
  @Output() formGuardado = new EventEmitter<void>();

  // Modelo interno usado en el formulario (para no alterar directamente el @Input)
  formModel : Ciudad = new Ciudad();

  constructor(
    private ciudadService : CiudadService,
    private toastr : ToastrService
  ){}

  ngOnInit(): void {
    // Si se recibe una ciudad por Input, la clonamos en el modelo local
    if(this.ciudad){
      this.formModel = {...this.ciudad};
    }
  }

  //  Detecta cuando cambia el Input() y actualiza el modelo
  //  Detecta cambios en el input y actualiza el formulario
  ngOnChanges(changes : SimpleChanges): void{
    if(changes['ciudad'] && this.ciudad){
      this.formModel = {...this.ciudad};
    }else{
      this.formModel = new Ciudad();
    }
  }

  onSubmit(formCiudad : NgForm): void{
    if(this.formModel.idCiudad){
      // Editamos nuestra ciudad
      this.ciudadService.editarCiudad(this.formModel.idCiudad, this.formModel).subscribe({
        next: () =>{
          this.toastr.success('Ciudad actualizada correctamente', 'Éxito');
          // Emite el evento hacia el componente padre.
          this.formGuardado.emit();
          this.limpiarFormulario(formCiudad); // limpiar al editar
        },
        error: (err) =>{
          this.toastr.error('Error al editar ciudad', 'Error');
        }
      });
    }else{
      // Creamos nuestra ciudad
      console.log('📦 Datos que se envían:', this.formModel);
      this.ciudadService.agregarCiudad(this.formModel).subscribe({
        next: () =>{
          this.toastr.success('Ciudad agregada correctamente', 'Éxito');
          // Emite el evento hacia el componente padre.
          this.formGuardado.emit();
          this.limpiarFormulario(formCiudad); //  limpiar al editar
        },
        error: (err) =>{
          this.toastr.error(err.message, 'Error'); // muestra el mensaje del backend
        }
      });
    }
  }

  limpiarFormulario(formCiudad?: NgForm): void {
  this.formModel = new Ciudad(); //  reinicia el modelo
  formCiudad?.resetForm();       //  limpia visualmente los inputs
}
}
