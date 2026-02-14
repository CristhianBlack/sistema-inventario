import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { TIPO_PERSONA } from 'src/app/Constantes/tipo-persona.const';
import { Persona } from 'src/app/Modelos/persona';
import { Proveedor } from 'src/app/Modelos/proveedor';
import { TipoPersona } from 'src/app/Modelos/tipo-persona';
import { PersonaService } from 'src/app/Servicios/persona.service';
import { ProveedorService } from 'src/app/Servicios/proveedor.service';

@Component({
  selector: 'app-proveedor-form',
  templateUrl: './proveedor-form.component.html',
  styleUrls: ['./proveedor-form.component.css']
})
export class ProveedorFormComponent implements OnInit {

  // ============================
  // INPUTS / OUTPUTS
  // ============================

  // Proveedor recibido desde el componente padre (editar)
  @Input() proveedor: Proveedor | null = null;

  // Evento para notificar al padre que el formulario fue guardado
  @Output() formGuardado = new EventEmitter<void>();

// ============================
  // MODELO DEL FORMULARIO
  // ============================

  // Modelo interno del formulario
  formModel: Proveedor = new Proveedor();

  // ============================
  // LISTAS Y SELECCIONES
  // ============================

  // Personas que tienen el rol de proveedor
  listaProveedores: Persona[] = [];

  // Persona seleccionada (solo para mostrar información)
  proveedorSeleccionado: Persona | null = null;

  // ============================
  // AUTOCOMPLETE / BÚSQUEDA
  // ============================
  searchTerm: string = '';          // lo que escribe el usuario
  personaSeleccionada: Persona | null = null; // selección real
  mostrarResultados: boolean = false; // dropdown abierto/cerrado
  

  constructor(
    private proveedorService: ProveedorService,
    private personaService: PersonaService,
    private toastr: ToastrService
  ) {}

  // ============================
  // CICLO DE VIDA
  // ============================
  ngOnInit(): void {
    // Cargar personas que pueden ser proveedor
    this.cargarPersonasProveedor();
    

    // Si viene proveedor para editar
    if (this.proveedor) {
      this.formModel = { ...this.proveedor };

      // Cargar persona asociada al proveedor
      this.personaService.obtnerPersonaPorID(this.proveedor.idPersona!)
        .subscribe(p => this.proveedorSeleccionado = p);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Detecta cambios cuando el modal cambia de proveedor
    if (changes['proveedor']) {
      if (this.proveedor) {
        // Editar
        this.formModel = { ...this.proveedor };

        this.personaService.obtnerPersonaPorID(this.proveedor.idPersona!)
          .subscribe(p => this.proveedorSeleccionado = p);
      } else {
        // Crear
        this.formModel = new Proveedor();
        this.proveedorSeleccionado = null;
      }
    }
  }

   // ============================
  // CARGA DE PERSONAS PROVEEDOR
  // ============================
  cargarPersonasProveedor() {
    this.personaService.obtenerPersonasConRolProveedor()
  .subscribe({
    next: data => {
      console.log('PERSONAS RAW', data);
      this.listaProveedores = data;
    },
    error: err => {
      console.error(err);
      this.toastr.error('Error cargando personas proveedor');
    }
  });
      
  }

   // ============================
  // SELECCIÓN DE PROVEEDOR
  // ============================
  onSeleccionarProveedor(idPersona: number) {
    idPersona = Number(idPersona);
    this.formModel.idPersona = idPersona;

    // Busca la persona seleccionada
    this.proveedorSeleccionado =
      this.listaProveedores.find(p => p.idPersona === idPersona) || null;
  }

  // ============================
  // GUARDAR FORMULARIO
  // ============================
  onSubmit(formProveedor: NgForm) {
    if (!formProveedor.valid) return;

    // EDITAR
    if (this.formModel.idProveedor) {
      this.proveedorService.actualizarProveedor(
        this.formModel.idProveedor,
        this.formModel
      ).subscribe({
        next: () => {
          this.toastr.success("Proveedor actualizado");
          this.formGuardado.emit();
        },
          error: (err) =>{
            this.toastr.error(err.mensaje || 'Error al actualizar el proveedor', 'Error');
          },
      });
    }
    // CREAR
    else {
      this.proveedorService.guardarProveedor(this.formModel)
        .subscribe({
          next: () => {
            this.toastr.success("Proveedor registrado");
            this.formGuardado.emit();
            this.limpiarFormulario(formProveedor);
          },
          error: (err) =>{
            this.toastr.error(err.mensaje || 'Error al crear proveedor', 'Error');
          },
        });
    }
  }

  // ============================
  // LIMPIAR FORMULARIO
  // ============================

  limpiarFormulario(formProveedor: NgForm) {
    this.formModel = new Proveedor();
    this.proveedorSeleccionado = null;
    formProveedor.resetForm();
  }

  compararPersonas(a: any, b: any) {
  return a === b;
}

// ============================
  // AUTOCOMPLETE / FILTRADO
  // ============================
onBuscar(valor: string) {
  this.searchTerm = valor;

  if (!valor || valor.trim().length === 0) {
    this.personasFiltradas = [];
    this.mostrarResultados = false;
    return;
  }

  this.personasFiltradas = this.listaProveedores.filter(p =>
    this.obtenerNombrePersona(p)
      .toLowerCase()
      .includes(valor.toLowerCase())
  );

  this.mostrarResultados = true;
  this.personaSeleccionada = null;
}



textoBusqueda: string = "";
personasFiltradas: Persona[] = [];
readonly TIPO_PERSONA_NATURAL = TIPO_PERSONA.NATURAL;
readonly TIPO_PERSONA_JURIDICA = TIPO_PERSONA.JURIDICA;

// ============================
  // UTILIDADES
  // ============================

  // Obtiene el nombre a mostrar según el tipo de persona
obtenerNombrePersona(p: any): string {
  console.log(
  p.idTipoPersona,
  p.idTipoPersona === this.TIPO_PERSONA_NATURAL ? p.nombre : p.razonSocial,
  p.idTipoPersona === this.TIPO_PERSONA_JURIDICA ? p.razonSocial : p.nombreContacto
  
);
   // PERSONA JURÍDICA
  if (p.idTipoPersona === this.TIPO_PERSONA_JURIDICA) {
    return p.razonSocial?.trim()
      || p.nombre?.trim()
      || '(Empresa sin nombre)';
  }

  // PERSONA NATURAL
  return `${p.nombre ?? ''} ${p.apellido ?? ''} ${p.segundoApellido ?? ''}`.trim();
}

// ============================
  // FILTRAMOS POR TEXTO BUSCADO
  // ============================

filtrarPersonas() {
  const texto = this.textoBusqueda.toLowerCase().trim();

  this.personasFiltradas = this.listaProveedores.filter(p =>
    this.obtenerNombrePersona(p).toLowerCase().includes(texto)
  );
  
}

seleccionarPersona(persona: Persona) {
  this.proveedorSeleccionado = persona;
  this.formModel.idPersona = persona.idPersona;
  // Mostrar texto correcto en el input
  this.searchTerm = this.obtenerNombrePersona(persona);
   // Cerrar dropdown
  this.mostrarResultados = false;
  // Limpiar lista
  this.personasFiltradas = [];
}
// ============================
  // OBTENEMOS EL NOMBRE SEA DE PERSONA NATURAL O PERSONA JURIDICA EL NOMBRE DEL CONTACTO
  // ============================
obtenerNombreParaInput(p: Persona | null): string {
  if (!p) return '';

  // Intentar CONTACTO (persona jurídica)
  const contacto = `${p.nombreContacto ?? ''} ${p.apellidoContacto ?? ''} ${p.segundoApellidoContacto ?? ''}`.trim();
  if (contacto) {
    return contacto;
  }

  // Fallback → PERSONA NATURAL
  const persona = `${p.nombre ?? ''} ${p.apellido ?? ''} ${p.segundoApellido ?? ''}`.trim();
  if (persona) {
    return persona;
  }

  return '';
}

}
