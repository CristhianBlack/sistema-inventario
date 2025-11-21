/*import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
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
    
    forkJoin({
    documentos: this.tipoDocumentoService.obtenerListaTipoDocumento(),
    ciudades: this.ciudadService.obtenerListaciudades(),
    tiposPersona: this.tipoPersonaService.obtenerListaTipoPersona(),
    roles: this.rolPersonaService.obtenerListaRolPersona()
  }).subscribe({
    next: (resp) => {
      this.tipoDocumentos = resp.documentos;
      this.ciudades = resp.ciudades;
      this.tipoPersonas = resp.tiposPersona;
      this.listaRoles = resp.roles;

      // 🔥 Si es edición, asigno ahora los valores
      if (this.persona) {
        this.ngOnChanges({
          persona: {
            currentValue: this.persona,
            previousValue: null,
            firstChange: false,
            isFirstChange: () => false
          }
        });
      }
    }
  });

    if (this.persona) {
      this.formModel = { ...this.persona };

      if (this.persona.roles && this.persona.roles.length > 0) {
      this.formModel.idsRoles = this.persona.roles.map((rol: any) => rol.idRolPersona);
    }
    }
  }

  /*ngOnChanges(changes: SimpleChanges): void {
     if (changes['persona'] && this.persona) {
    this.formModel = { ...this.persona };

    this.formModel.idTipoDocumento = this.persona.idTipoDocumento;
    this.formModel.idCiudad = this.persona.idCiudad;
    this.formModel.idTipoPersona = this.persona.idTipoPersona;

    // roles
    this.formModel.idsRoles = this.persona.roles?.map(r => r.idRolPersona) || [];
  }else {
      this.formModel = new Persona();
    }
  }*/

  /*  ngOnChanges(changes: SimpleChanges) {
  if (changes['persona'] && this.persona) {
    this.formModel = { ...this.persona };

    // 🔥 Mapeo correcto según lo que devuelve tu backend
    this.formModel.idTipoDocumento = this.persona.tipoDocumento?.idTipoDocumento ?? null;
    this.formModel.idCiudad = this.persona.ciudad?.idCiudad ?? null
    this.formModel.idTipoPersona = this.persona.tipoPersona?.idTipoPersona ?? null;

    // Roles
    this.formModel.idsRoles = this.persona.roles?.map(r => r.idRolPersona) || [];
  }
}


  obtenerListaTipoDocumento(): void {
     
    this.tipoDocumentoService.obtenerListaTipoDocumento().subscribe({
      next: (data) => {
      this.tipoDocumentos = data;
      console.log("lista tipoDocumentos:", this.tipoDocumentos);
      if (this.persona) {
        this.formModel.idTipoDocumento = this.persona.idTipoDocumento; // sincronizar
      }
    }
    });
  }

  obtenerListaCiudades(): void {
  this.ciudadService.obtenerListaciudades().subscribe({
    next: (data) => {
      this.ciudades = data;

      if (this.persona) {
        this.formModel.idCiudad = this.persona.idCiudad; // sincronizar
      }
    }
  });
}

  obtenerListaTipoPersona(): void {
    this.tipoPersonaService.obtenerListaTipoPersona().subscribe({
      next: (data) => {
      this.tipoPersonas = data;

      if (this.persona) {
        this.formModel.idTipoPersona = this.persona.idTipoPersona; // sincronizar
      }
    }
    });
  }

  cargarRoles(): void {
  this.rolPersonaService.obtenerListaRolPersona().subscribe({
    next: (data) => {
      this.listaRoles = data;

      if (this.persona) {
        this.formModel.idsRoles = this.persona.idsRoles; // sincronizar
      }
    }
  });
}


  Mapeo limpio para enviar solo los IDs relacionados
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
     tipoDocumento: this.formModel.idTipoDocumento,
     ciudad: this.formModel.idCiudad,
     tipoPersona: this.formModel.idTipoPersona,
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
          console.log("lista tipoDocumentos en boton editar:", this.tipoDocumentos);
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

obtnerSelects(): void {

  forkJoin({
    tipoDocumentos: this.tipoDocumentoService.obtenerListaTipoDocumento(),
    ciudades: this.ciudadService.obtenerListaciudades(),
    tipoPersonas: this.tipoPersonaService.obtenerListaTipoPersona(),
    roles: this.rolPersonaService.obtenerListaRolPersona()
  }).subscribe({
    next: (data) => {

      this.tipoDocumentos = data.tipoDocumentos;
      this.ciudades = data.ciudades;
      this.tipoPersonas = data.tipoPersonas;
      this.listaRoles = data.roles;

      // 🔥 Al editar
      if (this.persona) {

        this.formModel = { ...this.persona };

        // 🔥 Asignar IDs DIRECTOS
        this.formModel.idTipoDocumento = this.persona.idTipoDocumento ?? null;
        this.formModel.idCiudad = this.persona.idCiudad ?? null;
        this.formModel.idTipoPersona = this.persona.idTipoPersona ?? null;

        // 🔥 Roles
        this.formModel.idsRoles = this.persona.roles?.map(r => r.idRolPersona) ?? [];
      }
    },
    error: (err) => console.error("Error cargando datos", err)
  });
}


}*/


import { Component, EventEmitter, Input, OnInit, OnChanges, SimpleChanges, Output } from '@angular/core';
import { NgForm } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ToastrService } from 'ngx-toastr';
import { Persona } from 'src/app/Modelos/persona';
import { Ciudad } from 'src/app/Modelos/ciudad';
import { TipoPersona } from 'src/app/Modelos/tipo-persona';
import { TipoDocumento } from 'src/app/Modelos/tipo-documento';

import { PersonaService } from 'src/app/Servicios/persona.service';
import { CiudadService } from 'src/app/Servicios/ciudad.service';
import { TipoPersonaService } from 'src/app/Servicios/tipo-persona.service';
import { TipoDocumentoService } from 'src/app/Servicios/tipo-documento.service';
import { RolPersonaService } from 'src/app/Servicios/rol-persona.service';

@Component({
  selector: 'app-persona-form',
  templateUrl: './persona-form.component.html',
  styleUrls: ['./persona-form.component.css']
})
export class PersonaFormComponent implements OnInit, OnChanges {

  @Input() persona: Persona | null = null;
  @Output() formGuardado = new EventEmitter<void>();

  formModel: Persona = new Persona();

  tipoDocumentos: TipoDocumento[] = [];
  ciudades: Ciudad[] = [];
  tipoPersonas: TipoPersona[] = [];
  listaRoles: any[] = [];

  constructor(
    private personaService: PersonaService,
    private toastr: ToastrService,
    private tipoDocumentoService: TipoDocumentoService,
    private ciudadService: CiudadService,
    private tipoPersonaService: TipoPersonaService,
    private rolPersonaService: RolPersonaService
  ) {}

  ngOnInit(): void {
    this.cargarListas();
  }

  /*ngOnChanges(changes: SimpleChanges): void {
  if (changes['persona']) {
    // Cuando cambie la persona, recargamos las listas
    // y luego asignamos correctamente
    //this.cargarListas();
  }
}*/

  /*ngOnChanges(changes: SimpleChanges): void {

  // SI persona existe → es edición
  if (changes['persona'] && this.persona) {
    this.formModel = {
      ...this.persona,
      idTipoDocumento: this.persona.tipoDocumento?.idTipoDocumento ?? null,
      idCiudad: this.persona.ciudad?.idCiudad ?? null,
      idTipoPersona: this.persona.tipoPersona?.idTipoPersona ?? null,
      idsRoles: this.persona.roles?.map(r => r.idRolPersona) ?? []
    };
  } 
  else {
    // SI persona es null → NUEVA PERSONA
    this.formModel = new Persona();
  }
}*/
ngOnChanges(changes: SimpleChanges): void {
  if (changes['persona'] && this.persona) {
    this.asignarPersonaAlFormulario(); // ✔ Solo asignar datos
  }

  if (changes['persona'] && !this.persona) {
    this.formModel = new Persona(); // ✔ para modo crear
  }
}



  private cargarListas(): void {
    forkJoin({
      documentos: this.tipoDocumentoService.obtenerListaTipoDocumento(),
      ciudades: this.ciudadService.obtenerListaciudades(),
      tiposPersona: this.tipoPersonaService.obtenerListaTipoPersona(),
      roles: this.rolPersonaService.obtenerListaRolPersona()
    }).subscribe({
      next: data => {
        this.tipoDocumentos = data.documentos;
        this.ciudades = data.ciudades;
        this.tipoPersonas = data.tiposPersona;
        this.listaRoles = data.roles;

        if (this.persona) {
          this.asignarPersonaAlFormulario();
        }
      }
    });
  }

  private asignarPersonaAlFormulario(): void {
  if (!this.persona) return;

  this.formModel = {
    ...this.persona,

     idTipoDocumento: this.persona.idTipoDocumento ?? null,
    idCiudad: this.persona.idCiudad ?? null,
    idTipoPersona: this.persona.idTipoPersona ?? null,
    idsRoles: this.persona.idsRoles ?? []
  };
  console.log("FORM MODEL AL ABRIR EDITAR:", this.formModel);
  console.log("LISTAS:", {
    documentos: this.tipoDocumentos,
    ciudades: this.ciudades,
    tipoPersonas: this.tipoPersonas,
    roles: this.listaRoles
  });
}


  /*private asignarPersonaAlFormulario(): void {
    this.formModel = {
      ...this.persona!,

      idTipoDocumento: this.persona?.tipoDocumento?.idTipoDocumento ?? null,
      idCiudad: this.persona?.ciudad?.idCiudad ?? null,
      idTipoPersona: this.persona?.tipoPersona?.idTipoPersona ?? null,

      // roles
      idsRoles: this.persona?.roles?.map(r => Number(r.idRolPersona)) ?? []
    };
  }*/

  private mapPersonaToRequest(): any {
  return {
    idPersona: this.formModel.idPersona ?? null,
    documentoPersona: this.formModel.documentoPersona,
    nombre: this.formModel.nombre,
    apellido: this.formModel.apellido,
    segundoApellido: this.formModel.segundoApellido,
    direccion: this.formModel.direccion,
    telefono: this.formModel.telefono,
    email: this.formModel.email,

    // 🔥 NOMBRES CORRECTOS que espera tu DTO
    idTipoDocumento: Number(this.formModel.idTipoDocumento),
    idTipoPersona: Number(this.formModel.idTipoPersona),
    idCiudad: Number(this.formModel.idCiudad),

    idsRoles: this.formModel.idsRoles || []
  };
}



  onSubmit(formPersona: NgForm): void {
    if (formPersona.invalid) {
      this.toastr.warning('Complete todos los campos requeridos');
      return;
    }
    const request = this.mapPersonaToRequest();
    console.log("JSON que envío:", JSON.stringify(request, null, 2));

    // EDITAR
    if (this.formModel.idPersona) {
      this.personaService.editarPersona(this.formModel.idPersona, request)
        .subscribe(() => {
          console.log("EStoy validando donde estoy entrando ");
          this.toastr.success('Persona actualizada');
          this.formGuardado.emit();
          this.limpiarFormulario();
        });
      return;
    }else{
      console.log("Entro al else de crear");
      // CREAR
      this.personaService.agregarPersona(request)
      .subscribe({
        next: () => {
          console.log("Entro al subscribe y al next ");
          this.toastr.success('Persona agregada correctamente', 'Éxito');
          this.formGuardado.emit();
          this.limpiarFormulario(formPersona);
          console.log("Enviando REAL al backend:", JSON.stringify(request, null, 2));
        console.log("JSON FINAL:", request);
        this.toastr.success('Persona creada');
        },
        error: (err) => {
          console.error("ERROR COMPLETO DEL SERVIDOR:", err);
          this.toastr.error(err.message || 'Error al crear persona', 'Error');
        }
    });
    }   
  }

  onRolChange(event: any, idRol: number): void {
    if (!this.formModel.idsRoles) this.formModel.idsRoles = [];

    if (event.target.checked) {
      if (!this.formModel.idsRoles.includes(idRol)) {
        this.formModel.idsRoles.push(idRol);
      }
    } else {
      this.formModel.idsRoles = this.formModel.idsRoles.filter(r => r !== idRol);
    }
  }
  limpiarFormulario(formPersona?: NgForm): void {
    this.formModel = new Persona();
    formPersona?.resetForm();
      this.persona = null;
  this.formModel = new Persona();

  this.formModel.idTipoDocumento = null;
  this.formModel.idTipoPersona = null;
  this.formModel.idCiudad = null;

  this.formModel.idsRoles = [];
  }
}
