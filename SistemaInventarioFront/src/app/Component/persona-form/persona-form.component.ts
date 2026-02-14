import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  OnChanges,
  SimpleChanges,
  Output,
} from '@angular/core';
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
  styleUrls: ['./persona-form.component.css'],
})
export class PersonaFormComponent implements OnInit, OnChanges {
  // ============================
  // INPUTS / OUTPUTS
  // ============================

  // Persona recibida desde el componente padre (editar)
  @Input() persona: Persona | null = null;

  // Evento para notificar al padre que se guardó el formulario
  @Output() formGuardado = new EventEmitter<void>();

  // ============================
  // MODELO DEL FORMULARIO
  // ============================

  // Modelo interno para evitar mutar directamente el @Input
  formModel: Persona = new Persona();

  // ============================
  // LISTAS PARA SELECTS / CHECKS
  // ============================

  tipoDocumentos: TipoDocumento[] = [];
  ciudades: Ciudad[] = [];
  tipoPersonas: TipoPersona[] = [];
  listaRoles: any[] = [];

  // ============================
  // FLAGS DE CONTROL
  // ============================

  esNatural: boolean = true; // Controla si es persona natural o jurídica
  esAdminSistema = false; // Controla permisos del rol administrador

  constructor(
    private personaService: PersonaService,
    private toastr: ToastrService,
    private tipoDocumentoService: TipoDocumentoService,
    private ciudadService: CiudadService,
    private tipoPersonaService: TipoPersonaService,
    private rolPersonaService: RolPersonaService,
  ) {}

  // ============================
  // CICLO DE VIDA
  // ============================

  ngOnInit(): void {
    // Carga catálogos necesarios para el formulario
    this.cargarListas();

    // Obtiene el rol desde el localStorage para control de permisos
    const rol = localStorage.getItem('rol');
    this.esAdminSistema = rol === 'ADMIN_SISTEMA';
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Cuando llega una persona para editar
    if (changes['persona'] && this.persona?.idPersona) {
      // Reset completo del formulario
      this.formModel = new Persona();
      this.formModel.idsRoles = [];

      // Se obtiene la persona + roles reales desde backend
      forkJoin({
        persona: this.personaService.obtnerPersonaPorID(this.persona.idPersona),
        roles: this.personaService.obtenerRolesPorPersona(
          this.persona.idPersona,
        ),
      }).subscribe(({ persona, roles }) => {
        // Se asignan SOLO los IDs de roles activos
        persona.idsRoles = roles.map((r) => r.idRolPersona);

        this.persona = persona;
        this.asignarPersonaAlFormulario();
        this.onTipoPersonaChange();

        console.log('ROLES DESDE BACKEND:', persona.idsRoles);
      });
    }

    // Si se limpia la persona → modo crear
    if (changes['persona'] && !this.persona) {
      this.limpiarFormulario();
    }
  }

  // ============================
  // CARGA DE CATÁLOGOS
  // ============================

  private cargarListas(): void {
    forkJoin({
      documentos: this.tipoDocumentoService.obtenerListaTipoDocumento(),
      ciudades: this.ciudadService.obtenerListaciudades(),
      tiposPersona: this.tipoPersonaService.obtenerListaTipoPersona(),
      roles: this.rolPersonaService.obtenerListaRolPersona(),
    }).subscribe({
      next: (data) => {
        this.tipoDocumentos = data.documentos;
        this.ciudades = data.ciudades;
        this.tipoPersonas = data.tiposPersona;
        this.listaRoles = data.roles;

        // Si se está editando, asigna datos al formulario
        if (this.persona) {
          this.asignarPersonaAlFormulario();
        }
      },
    });
  }

  // ============================
  // ASIGNAR PERSONA AL FORMULARIO
  // ============================

  private asignarPersonaAlFormulario(): void {
    if (!this.persona) return;

    this.formModel = {
      ...this.persona,

      // Normaliza nombres (contacto vs persona)
      nombre: this.persona.nombre?.trim()
        ? this.persona.nombre
        : this.persona.nombreContacto,

      apellido: this.persona.apellido?.trim()
        ? this.persona.apellido
        : this.persona.apellidoContacto,

      segundoApellido: this.persona.segundoApellido?.trim()
        ? this.persona.segundoApellido
        : this.persona.segundoApellidoContacto,

      idTipoDocumento: this.persona.idTipoDocumento ?? null,
      idCiudad: this.persona.idCiudad ?? null,
      idTipoPersona: this.persona.idTipoPersona ?? null,

      // Copia segura de roles
      idsRoles: [...(this.persona.idsRoles ?? [])],
    };

    console.log('IDS ROLES EN FORM:', this.formModel.idsRoles);
  }

  // ============================
  // MAPEO A REQUEST BACKEND
  // ============================

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
      razonSocial: this.formModel.razonSocial,

      // Contacto (para personas jurídicas)
      nombreContacto: this.formModel.nombre,
      apellidoContacto: this.formModel.apellido,
      segundoApellidoContacto: this.formModel.segundoApellido,

      // IDs que espera el DTO
      idTipoDocumento: Number(this.formModel.idTipoDocumento),
      idTipoPersona: Number(this.formModel.idTipoPersona),
      idCiudad: Number(this.formModel.idCiudad),

      idsRoles: this.formModel.idsRoles || [],
    };
  }

  // ============================
  // GUARDAR PERSONA
  // ============================

  onSubmit(formPersona: NgForm): void {
    if (formPersona.invalid) {
      this.toastr.warning('Complete todos los campos requeridos');
      return;
    }

    const request = this.mapPersonaToRequest();
    console.log('JSON enviado:', request);

    // EDITAR
    if (this.formModel.idPersona) {
      this.personaService
        .editarPersona(this.formModel.idPersona, request)
        .subscribe({
          next: () => {
            this.toastr.success('Persona actualizada');
            this.formGuardado.emit();
            this.limpiarFormulario();
          },
          error: (err) => {
            this.toastr.error(err.mensaje || 'Error al actualizar', 'Error');
          },
        });
      return;
    }

    // CREAR
    this.personaService.agregarPersona(request).subscribe({
      next: () => {
        this.toastr.success('Persona creada correctamente', 'Éxito');
        this.formGuardado.emit();
        this.limpiarFormulario(formPersona);
      },
      error: (err) => {
        this.toastr.error(err.mensaje || 'Error al crear persona', 'Error');
      },
    });
  }

  // ============================
  // MANEJO DE ROLES
  // ============================

  onRolChange(event: any, idRol: number): void {
    if (!this.formModel.idsRoles) this.formModel.idsRoles = [];

    if (event.target.checked) {
      if (!this.formModel.idsRoles.includes(idRol)) {
        this.formModel.idsRoles.push(idRol);
      }
    } else {
      this.formModel.idsRoles = this.formModel.idsRoles.filter(
        (r) => r !== idRol,
      );
    }
  }

  // ============================
  // TIPO DE PERSONA
  // ============================

  private readonly ID_PERSONA_NATURAL = 1;
  private readonly ID_OTRO = 3;

  onTipoPersonaChange(): void {
    this.esNatural =
      this.formModel.idTipoPersona === this.ID_PERSONA_NATURAL ||
      this.formModel.idTipoPersona === this.ID_OTRO;

    if (this.esNatural) {
      this.formModel.razonSocial = '';
    }
  }

  // ============================
  // UTILIDADES
  // ============================

  // Formatea enums (TIPO_DOCUMENTO → Tipo Documento)
  formatearTipoDocumento(valor?: string): string {
    if (!valor) return '';
    return valor
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/\b\w/g, (letra) => letra.toUpperCase());
  }

  limpiarFormulario(formPersona?: NgForm): void {
    formPersona?.resetForm();
    this.persona = null;
    this.formModel = new Persona();
    this.formModel.idsRoles = [];
  }

  trackByRol(index: number, rol: any) {
    return rol.idRolPersona;
  }
}
