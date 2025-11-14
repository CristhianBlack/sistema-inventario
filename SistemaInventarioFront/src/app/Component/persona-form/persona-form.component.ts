import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Ciudad } from 'src/app/Modelos/ciudad';
import { Persona } from 'src/app/Modelos/persona';
import { TipoDocumento } from 'src/app/Modelos/tipo-documento';
import { TipoPersona } from 'src/app/Modelos/tipo-persona';
import { CiudadService } from 'src/app/Servicios/ciudad.service';
import { PersonaService } from 'src/app/Servicios/persona.service';
import { RolPersonaService } from 'src/app/Servicios/rol-persona.service';
import { TipoDocumentoService } from 'src/app/Servicios/tipo-documento.service';
import { TipoPersonaService } from 'src/app/Servicios/tipo-persona.service';

@Component({
  selector: 'app-persona-form',
  templateUrl: './persona-form.component.html',
  styleUrls: ['./persona-form.component.css']
})
export class PersonaFormComponent implements OnInit {

  @Input() persona?: Persona | null = null;
  @Output() formGuardado = new EventEmitter<void>();

  formModel: Persona = new Persona();
  tipoDocumentos: TipoDocumento[] = [];
  ciudades: Ciudad[] = [];
  tipoPersonas: TipoPersona[] = [];
  listaRoles: any[] = [];
  roles?: any[];

  constructor(
    private personaService: PersonaService,
    private toastr: ToastrService,
    private tipoDocumentoService: TipoDocumentoService,
    private ciudadService: CiudadService,
    private tipoPersonaService: TipoPersonaService,
    private rolPersonaService : RolPersonaService
  ) {}

  ngOnInit(): void {
    this.obtenerListaTipoDocumento();
    this.obtenerListaCiudades();
    this.obtenerListaTipoPersona();
    this.cargarRoles();
    

    if (this.persona) {
      this.formModel = { ...this.persona };

      if (this.persona.roles && this.persona.roles.length > 0) {
      this.formModel.idsRoles = this.persona.roles.map((rol: any) => rol.idRolPersona);
    }
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['persona'] && this.persona) {
      this.formModel = { ...this.persona };

      // Si la persona tiene roles, convertirlos a IDs
    if (this.persona.roles && this.persona.roles.length > 0) {
      this.formModel.idsRoles = this.persona.roles.map((r: any) => r.idRolPersona);
    } else {
      this.formModel.idsRoles = [];
    }
    } else {
      this.formModel = new Persona();
    }
  }

  obtenerListaTipoDocumento(): void {
    this.tipoDocumentoService.obtenerListaTipoDocumento().subscribe({
      next: (data) => (this.tipoDocumentos = data),
      error: (err) => console.error('Error cargando tipos de documento', err)
    });
  }

  obtenerListaCiudades(): void {
    this.ciudadService.obtenerListaciudades().subscribe({
      next: (data) => (this.ciudades = data),
      error: (err) => console.error('Error cargando ciudades', err)
    });
  }

  obtenerListaTipoPersona(): void {
    this.tipoPersonaService.obtenerListaTipoPersona().subscribe({
      next: (data) => (this.tipoPersonas = data),
      error: (err) => console.error('Error cargando tipos de persona', err)
    });
  }

  cargarRoles(): void {
  this.rolPersonaService.obtenerListaRolPersona().subscribe({
    next: (data) => {
      this.listaRoles = data;
      // Sincronizar roles después de cargar
      if (this.persona?.roles?.length) {
        this.formModel.idsRoles = this.persona.roles.map((r: any) => Number(r.idRolPersona));
        console.log('Roles sincronizados:', this.formModel.idsRoles);
      }
    },
    error: (err) => console.error('Error cargando roles', err)
  });
}


  // Mapeo limpio para enviar solo los IDs relacionados
  private mapPersonaToRequest(): any {
    return {
      idPersona: this.formModel.idPersona || null,
      nombre: this.formModel.nombre,
      apellido: this.formModel.apellido,
      segundoApellido: this.formModel.segundoApellido,
      direccion: this.formModel.direccion,
      telefono: this.formModel.telefono,
      email: this.formModel.email,
      documentoPersona: this.formModel.documentoPersona,
      idTipoDocumento: this.formModel.tipoDocumento?.idTipoDocumento,
      idCiudad: this.formModel.ciudad?.idCiudad,
      idTipoPersona: this.formModel.tipoPersona?.idTipoPersona,
      idsRoles: this.formModel.idsRoles
    };
  }

  onSubmit(formPersona: NgForm): void {
    if (formPersona.invalid) {
      this.toastr.warning('Por favor complete todos los campos requeridos.', 'Campos incompletos');
      return;
    }

    const personaAEnviar = this.mapPersonaToRequest();
    console.log(' Enviando al backend:', personaAEnviar);

    if (this.formModel.idPersona) {
      // Editar
      this.personaService.editarPersona(this.formModel.idPersona, personaAEnviar).subscribe({
        next: () => {
          this.toastr.success('Persona actualizada correctamente', 'Éxito');
          this.formGuardado.emit();
          this.limpiarFormulario(formPersona);
        },
        error: (err) => {
          this.toastr.error(err.message || 'Error al editar persona', 'Error');
        }
      });
    } else {
      // Crear
      this.personaService.agregarPersona(personaAEnviar).subscribe({
        next: () => {
          this.toastr.success('Persona agregada correctamente', 'Éxito');
          this.formGuardado.emit();
          this.limpiarFormulario(formPersona);
        },
        error: (err) => {
          this.toastr.error(err.message || 'Error al crear persona', 'Error');
        }
      });
    }
  }

  limpiarFormulario(formPersona?: NgForm): void {
    this.formModel = new Persona();
    formPersona?.resetForm();
  }

  compareById(a: any, b: any): boolean {
  return a && b ? 
    (a.id === b.id ||
     a.idTipoDocumento === b.idTipoDocumento ||
     a.idCiudad === b.idCiudad ||
     a.idTipoPersona === b.idTipoPersona ||
     a.idsRoles === b.idsRoles)
    : a === b;
}

onRolChange(event: any, idRol: number): void {
  if (!this.formModel.idsRoles) this.formModel.idsRoles = [];

  if (event.target.checked) {
    // Añadir rol si fue seleccionado
    if (!this.formModel.idsRoles.includes(idRol)) {
      this.formModel.idsRoles.push(idRol);
    }
  } else {
    // Quitar rol si fue desmarcado
    this.formModel.idsRoles = this.formModel.idsRoles.filter(id => id !== idRol);
  }
}

}
