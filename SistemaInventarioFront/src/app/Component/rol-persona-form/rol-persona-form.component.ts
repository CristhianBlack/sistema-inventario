import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { RolPersona } from 'src/app/Modelos/rol-persona';
import { RolPersonaService } from 'src/app/Servicios/rol-persona.service';

@Component({
  selector: 'app-rol-persona-form',
  templateUrl: './rol-persona-form.component.html',
  styleUrls: ['./rol-persona-form.component.css']
})
export class RolPersonaFormComponent implements OnInit{


    //  Recibe una categoría cuando se va a editar (puede venir vacía si es "crear")
    @Input() rolPersona?: RolPersona | null = null;
  
    //  Emite un evento al guardar, para que el padre (lista) actualice la tabla
    @Output() formGuardado = new EventEmitter<void>();
  
    //  Modelo interno usado en el formulario (para no alterar directamente el @Input)
    formModel: RolPersona = new RolPersona();
  
    constructor(
      private rolPersonaService: RolPersonaService,
      private toastr: ToastrService
    ) {}
  
    ngOnInit(): void {
      // Si se recibe un rol por Input, la clonamos en el modelo local
      if (this.rolPersona) {
        this.formModel = { ...this.rolPersona };
      }
    }
  
     //  Detecta cuando cambia el Input() y actualiza el modelo
     //  Detecta cambios en el input y actualiza el formulario
    ngOnChanges(changes: SimpleChanges): void {
      if (changes['rolPersona'] && this.rolPersona) {
        this.formModel = { ...this.rolPersona };
      } else {
        this.formModel = new RolPersona();
      }
    }
  
    onSubmit(formRolPersona: NgForm): void {
      if (this.formModel.idRolPersona) {
        // Editar
        this.rolPersonaService.editarRolPersona(this.formModel.idRolPersona, this.formModel).subscribe({
          next: () => {
            this.toastr.success('Rol actualizado correctamente', 'Éxito');
            this.formGuardado.emit();
            this.limpiarFormulario(formRolPersona);
          },
          error: (err) => {
            console.error(err);
            this.toastr.error('Error al editar rol', 'Error');
          }
        });
      } else {
        //  Agregar
        this.rolPersonaService.agregarRolPersona(this.formModel).subscribe({
          next: () => {
            this.toastr.success('Rol agregada correctamente', 'Éxito');
            this.formGuardado.emit();
          },
          error: (err) => {
            console.error(err);
            this.toastr.error('Error al agregar rol', 'Error');
            this.limpiarFormulario(formRolPersona);
          }
        });
      }
    }

    limpiarFormulario(formRolPersona?: NgForm): void {
        this.formModel = new RolPersona();
        formRolPersona?.resetForm();
      }
}
