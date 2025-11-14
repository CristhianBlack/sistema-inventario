import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { TipoDocumento } from 'src/app/Modelos/tipo-documento';
import { TipoDocumentoService } from 'src/app/Servicios/tipo-documento.service';

@Component({
  selector: 'app-tipo-documento-form',
  templateUrl: './tipo-documento-form.component.html',
  styleUrls: ['./tipo-documento-form.component.css']
})
export class TipoDocumentoFormComponent implements OnInit{

  // Recibe una tipo de documento cuando se va a editar (puede venir vacía si es "crear")
    @Input() tipoDocumento?: TipoDocumento | null = null;
  
    // Emite un evento al guardar, para que el padre (lista) actualice la tabla
    @Output() formGuardado = new EventEmitter<void>();
  
    // Modelo interno usado en el formulario (para no alterar directamente el @Input)
    formModel : TipoDocumento = new TipoDocumento();

    constructor(
      private tipoDocumentService : TipoDocumentoService,
      private toastr : ToastrService
    ){}

  ngOnInit(): void {
    // Si se recibe una ciudad por Input, la clonamos en el modelo local
    if(this.tipoDocumento){
      this.formModel = {...this.tipoDocumento};
    }
  }

  //  Detecta cuando cambia el Input() y actualiza el modelo
    //  Detecta cambios en el input y actualiza el formulario
    ngOnChanges(changes : SimpleChanges): void{
      if(changes['tipoDocumento'] && this.tipoDocumento){
        this.formModel = {...this.tipoDocumento};
      }else{
        this.formModel = new TipoDocumento();
      }
    }

    onSubmit(formTipoDocumento : NgForm): void{
        if(this.formModel.idTipoDocumento){
          // Editamos nuestra ciudad
          this.tipoDocumentService.editarTipoDocumento(this.formModel.idTipoDocumento, this.formModel).subscribe({
            next: () =>{
              this.toastr.success('Ciudad actualizada correctamente', 'Éxito');
              // Emite el evento hacia el componente padre.
              this.formGuardado.emit();
              this.limpiarFormulario(formTipoDocumento); // limpiar al editar
            },
            error: (err) =>{
              this.toastr.error('Error al editar ciudad', 'Error');
            }
          });
        }else{
          // Creamos nuestra ciudad
          console.log(' Datos que se envían:', this.formModel);
          this.tipoDocumentService.agregarTipoDocumento(this.formModel).subscribe({
            next: () =>{
              this.toastr.success('Ciudad agregada correctamente', 'Éxito');
              // Emite el evento hacia el componente padre.
              this.formGuardado.emit();
              this.limpiarFormulario(formTipoDocumento); //  limpiar al editar
            },
            error: (err) =>{
              this.toastr.error(err.message, 'Error'); // muestra el mensaje del backend
            }
          });
        }
      }
    
      limpiarFormulario(formTipoDocumento?: NgForm): void {
      this.formModel = new TipoDocumento(); //  reinicia el modelo
      formTipoDocumento?.resetForm();       //  limpia visualmente los inputs
    }    
}
