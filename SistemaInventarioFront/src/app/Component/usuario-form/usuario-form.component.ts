import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import { Persona } from 'src/app/Modelos/persona';
import { Usuario } from 'src/app/Modelos/usuario';
import { PersonaService } from 'src/app/Servicios/persona.service';
import { UsuarioService } from 'src/app/Servicios/usuario.service';

@Component({
  selector: 'app-usuario-form',
  templateUrl: './usuario-form.component.html',
  styleUrls: ['./usuario-form.component.css']
})
export class UsuarioFormComponent implements OnInit{

   // Recibe una ciudad cuando se va a editar (puede venir vacía si es "crear")
    @Input() usuario?: Usuario | null = null;
  
    // Emite un evento al guardar, para que el padre (lista) actualice la tabla
    @Output() formGuardado = new EventEmitter<void>();
  
    // Modelo interno usado en el formulario (para no alterar directamente el @Input)
    formModel : Usuario = new Usuario();

    personas: Persona[] = [];
  
    constructor(
      private usuarioService : UsuarioService,
      private toastr : ToastrService,
      private personaService : PersonaService
    ){}
  
    ngOnInit(): void {
      this.cargarLista();
      // Si se recibe un usuario por Input, la clonamos en el modelo local
      if(this.usuario){
        this.formModel = {...this.usuario};
      }
    }
  
    //  Detecta cuando cambia el Input() y actualiza el modelo
    //  Detecta cambios en el input y actualiza el formulario
    ngOnChanges(changes : SimpleChanges): void{
      if(changes['usuario'] && this.usuario){
        this.formModel = {...this.usuario};
      }else{
        this.formModel = new Usuario();
      }
    }

    
    private cargarLista(): void {
      const ID_PERSONA_NATURAL = 1;
          forkJoin({
            personas: this.personaService.obtenerListaPersona()
          }).subscribe({
            next: data => {
              this.personas = data.personas.filter(p => p.idTipoPersona == ID_PERSONA_NATURAL);
              
      
              if (this.usuario) {
                this.asignarPersonaAlFormulario();
              }
            }
          });
        }
      
        private asignarPersonaAlFormulario(): void {
        if (!this.usuario) return;
      
        this.formModel = {
          ...this.usuario,
      
          idPersona: this.usuario.idPersona ?? null
        };
        console.log("FORM MODEL AL ABRIR EDITAR:", this.formModel);
        console.log("LISTAS:", {
          personas: this.personas          
        });
      }

      private mapUsuarioToRequest(): any {
    return {
      idUsuario: this.formModel.idUsuario ?? null,
      username: this.formModel.username,
      password: this.formModel.password,
      rolSeguridad : this.formModel.rolSeguridad,

    // NOMBRES CORRECTOS que espera tu DTO
    idPersona : Number(this.formModel.idPersona),
     
      
    };
  }
      
  
    onSubmit(formUsuario : NgForm): void{
      if (formUsuario.invalid) {
        this.toastr.warning('Complete todos los campos requeridos');
        return;
      }
      const request = this.mapUsuarioToRequest();
      console.log("JSON que envío:", JSON.stringify(request, null, 2));
      //Editar
      if(this.formModel.idUsuario){
        // Editamos nuestra usuario
        this.usuarioService.editarUsuario(this.formModel.idUsuario, request).subscribe({
          next: () =>{
            this.toastr.success('Usuario actualizado correctamente', 'Éxito');
            // Emite el evento hacia el componente padre.
            this.formGuardado.emit();
            this.limpiarFormulario(formUsuario); // limpiar al editar
          },
          error: () =>{
            this.toastr.error('Error al editar ciudad', 'Error');
          }
        });
      }else{
        // Creamos nuestro usuario
        console.log(' Datos que se envían:', this.formModel);
        this.usuarioService.crearUsuario(request).subscribe({
          next: () =>{
            this.toastr.success('usuario creado correctamente', 'Éxito');
            // Emite el evento hacia el componente padre.
            this.formGuardado.emit();
            this.limpiarFormulario(formUsuario); //  limpiar al editar
          },
          error: (err) =>{
            this.toastr.error(err.mensaje, 'Error'); // muestra el mensaje del backend
          }
        });
      }
    }
  
    limpiarFormulario(formUsuario?: NgForm): void {
    this.formModel = new Usuario(); //  reinicia el modelo
    formUsuario?.resetForm();       //  limpia visualmente los inputs
  }

 

}
