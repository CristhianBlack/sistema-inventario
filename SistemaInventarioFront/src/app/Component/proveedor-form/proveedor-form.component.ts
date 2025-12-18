import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { Persona } from 'src/app/Modelos/persona';
import { Proveedor } from 'src/app/Modelos/proveedor';
import { PersonaService } from 'src/app/Servicios/persona.service';
import { ProveedorService } from 'src/app/Servicios/proveedor.service';

@Component({
  selector: 'app-proveedor-form',
  templateUrl: './proveedor-form.component.html',
  styleUrls: ['./proveedor-form.component.css']
})
export class ProveedorFormComponent implements OnInit {

  // El proveedor que llega desde el modal al editar
  @Input() proveedor: Proveedor | null = null;

  @Output() formGuardado = new EventEmitter<void>();

  // Modelo para guardar
  formModel: Proveedor = new Proveedor();

  // Lista de personas que tienen rol proveedor
  listaProveedores: Persona[] = [];

  // Persona seleccionada en el select (solo lectura)
  proveedorSeleccionado: Persona | null = null;

  constructor(
    private proveedorService: ProveedorService,
    private personaService: PersonaService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.cargarPersonasProveedor();
    

    // Si viene proveedor para editar
    if (this.proveedor) {
      this.formModel = { ...this.proveedor };

      // Cargar datos de la persona asociada
      this.personaService.obtnerPersonaPorID(this.proveedor.idPersona!)
        .subscribe(p => this.proveedorSeleccionado = p);
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['proveedor']) {
      if (this.proveedor) {
        this.formModel = { ...this.proveedor };

        this.personaService.obtnerPersonaPorID(this.proveedor.idPersona!)
          .subscribe(p => this.proveedorSeleccionado = p);
      } else {
        this.formModel = new Proveedor();
        this.proveedorSeleccionado = null;
      }
    }
  }

  cargarPersonasProveedor() {
    this.personaService.obtenerPersonasConRolProveedor()
      .subscribe({
        next: data => this.listaProveedores = data,
        error: () => this.toastr.error("Error cargando personas proveedor")
      });
  }

  onSeleccionarProveedor(idPersona: number) {
    idPersona = Number(idPersona);
    this.formModel.idPersona = idPersona;

    this.proveedorSeleccionado =
      this.listaProveedores.find(p => p.idPersona === idPersona) || null;
  }

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
        }
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
          }
        });
    }
  }

  limpiarFormulario(formProveedor: NgForm) {
    this.formModel = new Proveedor();
    this.proveedorSeleccionado = null;
    formProveedor.resetForm();
  }

  compararPersonas(a: any, b: any) {
  return a === b;
}

textoBusqueda: string = "";
personasFiltradas: Persona[] = [];

filtrarPersonas() {
  const texto = this.textoBusqueda.toLowerCase();

  this.personasFiltradas = this.listaProveedores.filter(p =>
    (p.nombre + ' ' + p.apellido + ' ' + p.segundoApellido)
      .toLowerCase()
      .includes(texto)
  );
}

seleccionarPersona(persona: Persona) {
  this.proveedorSeleccionado = persona;
  this.formModel.idPersona = persona.idPersona;
}


}
