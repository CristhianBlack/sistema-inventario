import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import { TIPO_PERSONA } from 'src/app/Constantes/tipo-persona.const';
import { Impuesto } from 'src/app/Modelos/impuesto';
import { Persona } from 'src/app/Modelos/persona';
import { Producto } from 'src/app/Modelos/producto';
import { Venta } from 'src/app/Modelos/venta';
import { ImpuestoService } from 'src/app/Servicios/impuesto.service';
import { PersonaService } from 'src/app/Servicios/persona.service';
import { ProductoService } from 'src/app/Servicios/producto.service';
import { VentaService } from 'src/app/Servicios/venta.service';

@Component({
  selector: 'app-venta-form',
  templateUrl: './venta-form.component.html',
  styleUrls: ['./venta-form.component.css']
})
export class VentaFormComponent implements OnInit{

    @Input() venta: Venta | null = null;
    @Output() formGuardado = new EventEmitter<void>();
  
    formModel: Venta = new Venta();
  
   personas : Persona[] = []
   listadoCliente : Persona[] =[];
  
    // ============================
    // NUEVAS VARIABLES DETALLE
    // ============================

    productos : Producto[]=[];

    impuestos : Impuesto[]=[];
  
   productoSeleccionado : Producto | null = null;
   impuestoSeleccionado: Impuesto | null = null;

  
    cantidad: number = 0;
   // precio: number = 0;
   descuento : number = 0;
   
  
    detalleVenta: any[] = [];
    total: number = 0;
  
    indiceEditando: number | null = null;
  
  
    modoSoloLectura: boolean = false;

    personaSeleccionada: any = null;

    usarSaldoFavor: boolean = false;

    saldoAplicado: number = 0;
    totalPagar: number = 0;
    precio: number = 0;

    // ============================
          // AUTOCOMPLETE / BÚSQUEDA
          // ============================
          searchTerm: string = '';          // lo que escribe el usuario
          mostrarResultados: boolean = false; // dropdown abierto/cerrado
          textoBusqueda: string = "";
          personasFiltradas: Persona[] = [];
          readonly TIPO_PERSONA_NATURAL = TIPO_PERSONA.NATURAL;
          readonly TIPO_PERSONA_JURIDICA = TIPO_PERSONA.JURIDICA;
  
    // ============================
  
  
    constructor(
      private ventaService: VentaService,
      private toastr: ToastrService,
      private personaService: PersonaService,
      private productoService : ProductoService,
      private impuestoService : ImpuestoService
  
    ) {}
  
    ngOnInit(): void {
      this.cargarListas();
      this.formModel.totalVenta = 0;
    }
  
    ngOnChanges(changes: SimpleChanges): void {
      if (changes['venta'] && this.venta) {
        this.asignarDatosAlFormulario();
      }
  
      if (changes['venta'] && !this.venta) {
        this.formModel = new Venta();
        this.detalleVenta = [];
        this.total = 0;
      }
    }
  
  
    // ============================
    // CARGAR CATÁLOGOS
    // ============================
    private cargarListas(): void {
      forkJoin({
        listadoCliente: this.personaService.obtenerPersonasConRolCliente(),
        productos: this.productoService.obtenerListaProductos(),
        impuestos : this.impuestoService.listarImpuestos()
      }).subscribe({
        next: data => {
          this.listadoCliente = data.listadoCliente;
          this.productos = data.productos;
          this.impuestos = data.impuestos;
          console.log('Datos de la persona', this.personas);
  
          if (this.venta) {
            this.asignarDatosAlFormulario();
          }
        }
      });
    }
  
  
    private asignarDatosAlFormulario(): void {
    if (!this.venta) return;
  
    this.formModel = {
      ...this.venta,
      idPersona: this.venta.idPersona ?? null,
    };
  
    // ←← CARGAR DETALLES EN LA TABLA
    this.detalleVenta = this.venta.detalles?.map(d => ({
      idProducto: d.idProducto,
  nombreProducto: d.nombreProducto,
  cantidad: d.cantidad,
  precioUnitario: d.precioUnitario,
  descuento: d.descuento,
  //ivaPorcentaje: d.porcentajeImpuesto,
  subtotalLinea: d.subtotalLinea,
  impuestoLinea: d.impuestoLinea,
  totalLinea: d.totalLinea

    })) || [];
  
    this.calcularTotal();
  
    //activamos el modo lecutra
    this.modoSoloLectura = this.formModel.estado === 'CONFIRMADA' ||
  this.formModel.estado === 'PAGADA' ||
  this.formModel.estado === 'CANCELADA';
  
    // mensaje informativo
    if (this.modoSoloLectura) {
      this.toastr.info(
        "Esta venta ya fue registrada. No es posible modificar los detalles.",
        "Modo solo lectura"
      );
    }
  
  
    console.log("FORM MODEL AL ABRIR EDITAR:", this.formModel);
    console.log("DETALLES CARGADOS:", this.detalleVenta);
  }
  
    // ============================
    // MANEJO DEL DETALLE
    // ============================
  
    agregarItem() {
      if (!this.productoSeleccionado || this.cantidad <= 0) {
        this.toastr.warning("Complete todos los campos del detalle");
        return;
      }

      // Usar precio manual o precio del producto
    const precioUnitario =
      this.precio > 0
        ? this.precio
        : this.productoSeleccionado.precioVenta ?? 0;

    /*if (precioUnitario <= 0) {
      this.toastr.warning('El producto no tiene precio válido');
      return;
    }*/
     
    // Obtener impuesto del producto
    this.impuestoSeleccionado = this.obtenerImpuestoPorProducto(this.productoSeleccionado);
     // const precio = this.productoSeleccionado.precioVenta;
      

      const impuestoPct = this.impuestoSeleccionado?.porcentaje ?? 0;

      // Cálculos
      const subtotalLinea = precioUnitario * this.cantidad;
      const impuestoLinea = subtotalLinea * impuestoPct;
      const totalLinea = subtotalLinea + impuestoLinea - this.descuento;
  
    // Si estamos editando un ítem
    if (this.indiceEditando !== null) {
      this.detalleVenta[this.indiceEditando] = {
        idProducto: this.productoSeleccionado.idProducto,
        codigoProducto: this.productoSeleccionado.codigoProducto,
        nombreProducto: this.productoSeleccionado.nombreProducto,
        cantidad: this.cantidad,
        precioUnitario,
        impuestoPct,
        impuestoLinea,
        descuento: this.descuento,
        subtotalLinea,
        totalLinea
        
      };

      
      this.indiceEditando = null;
      this.toastr.success("Ítem actualizado");
    } else {
      // Crear nuevo ítem
      this.detalleVenta.push({
        idProducto: this.productoSeleccionado.idProducto,
        codigoProducto: this.productoSeleccionado.codigoProducto,
        nombreProducto: this.productoSeleccionado.nombreProducto,
        cantidad: this.cantidad,
        precioUnitario,
        impuestoPct,
        impuestoLinea,
        descuento: this.descuento,
        subtotalLinea,
        totalLinea
      });
    }
  
    console.log("precio de venta en editar item ", this.productoSeleccionado.codigoProducto)
    console.log("iva en porcentaje ",impuestoPct)
    this.calcularTotal();
  
      // Limpiar
      this.productoSeleccionado = null;
      this.cantidad = 0;
      this.descuento = 0;
      this.precio = 0;
    }
  
    eliminarItem(i: number) {
      this.detalleVenta.splice(i, 1);
      this.calcularTotal();
    }
  
  calcularTotal() {
      this.total = this.detalleVenta.reduce((sum, item) => sum + item.totalLinea, 0);

    this.recalcularTotales();
  }
  
  
    // ============================
    // MAP A REQUEST FINAL
    // ============================
    private mapDatoToRequest(): any {
      return {
    idVenta: this.formModel.idVenta ?? null,
    fechaVenta: this.formModel.fechaVenta ?? null,
    idPersona: Number(this.formModel.idPersona),

    saldoAplicado: this.saldoAplicado,

    detalles: this.detalleVenta.map(item => ({
      cantidad: item.cantidad,
      idProducto: item.idProducto,
      descuento: item.descuento
    }))
  };
}

  
  
    // ============================
    // GUARDAR
    // ============================
    onSubmit(formVenta: NgForm): void {
      if (formVenta.invalid) {
        this.toastr.warning('Complete todos los campos requeridos');
        return;
      }
  
      if (this.detalleVenta.length === 0) {
        this.toastr.warning("Debe agregar al menos un producto");
        return;
      }

      if (!this.usarSaldoFavor) {
    this.saldoAplicado = 0;
  }

      const request = this.mapDatoToRequest();
      console.log("JSON FINAL:", request);
  
      // EDITAR
      if (this.formModel.idVenta) {
        this.ventaService.obtenerVentaPorId(this.formModel.idVenta)
          .subscribe(() => {
            this.toastr.success('Venta actualizada correctamente');
            this.formGuardado.emit();
            this.limpiarFormulario();
          });
        return;
      }
  
      // CREAR
      this.ventaService.guardarVenta(request).subscribe({
        next: () => {
          this.toastr.success('Venta agregada correctamente', 'Éxito');
          this.formGuardado.emit();
          this.limpiarFormulario(formVenta);
        },
        error: (err) => {
          console.error("ERROR COMPLETO DEL SERVIDOR:", err);
          this.toastr.error(err.message || 'Error al crear venta', 'Error');
        }
      });
    }
  
  
  
    // ============================
    // LIMPIAR FORMULARIO
    // ============================
    limpiarFormulario(formVenta?: NgForm): void {
      this.formModel = new Venta();
      this.venta = null;
      this.personaSeleccionada = null
  
      // limpiar detalle
      this.detalleVenta = [];
      this.total = 0;
  
      formVenta?.resetForm();
    }
  
    // ============================
    // METODO PARA EDITAR UN ITEM EXISTENTE
    // ============================
    editarItem(i: number) {
  const item = this.detalleVenta[i];
  this.indiceEditando = i;

  const producto = this.productos.find(
    p => p.idProducto === item.idProducto
  );

  if (!producto) {
    this.toastr.error('Producto no encontrado');
    return;
  }

  this.productoSeleccionado = producto;
  this.cantidad = item.cantidad;
  this.precio = item.precioUnitario;
  this.descuento = item.descuento ?? 0;
  this.impuestoSeleccionado = this.obtenerImpuestoPorProducto(this.productoSeleccionado);
}

  obtenerImpuestoPorProducto(producto: Producto | null): Impuesto | null {
  if (!producto || producto.idImpuesto == null) {
    return null;
  }

  return this.impuestos.find(
    i => i.idImpuesto === producto.idImpuesto
  ) ?? null;
}

recalcularTotales() {

  this.total = this.detalleVenta
    .reduce((acc, item) => acc + (Number(item.totalLinea) || 0), 0);

  // Reset por defecto
  this.saldoAplicado = 0;

  if (
    this.usarSaldoFavor &&
    this.personaSeleccionada &&
    Number(this.personaSeleccionada.saldoFavor) > 0
  ) {
    this.saldoAplicado = Math.min(
      Number(this.personaSeleccionada.saldoFavor),
      this.total
    );
  }

  this.totalPagar = this.total - this.saldoAplicado;

  console.log("Usar saldo:", this.usarSaldoFavor);
  console.log("Saldo Aplicado:", this.saldoAplicado);
  console.log("Total a pagar:", this.totalPagar);
}

//Detectar la persona seleccionada
onPersonaChange(idPersona: number) {
  this.personaSeleccionada =
    this.personas.find(p => p.idPersona === idPersona) || null;

    this.usarSaldoFavor = false;
  this.saldoAplicado = 0;

  this.recalcularTotales();
}

onProductoSeleccionado(): void {
    if (this.productoSeleccionado) {
      this.precio = this.productoSeleccionado.precioVenta ?? 0;

    }
  }

  // ============================
  // AUTOCOMPLETE / FILTRADO
  // ============================
onBuscar(valor: string) {
  console.log("Lista proveedores:", this.listadoCliente);
  console.log("Valor buscado:", valor);
  this.searchTerm = valor;

  if (!valor || valor.trim().length === 0) {
    this.personasFiltradas = [];
    this.mostrarResultados = false;
    return;
  }

  this.personasFiltradas = this.listadoCliente.filter(p =>
    this.obtenerNombrePersona(p)
      .toLowerCase()
      .includes(valor.toLowerCase())
  );

  this.mostrarResultados = true;
  this.personaSeleccionada = null;
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

seleccionarPersona(persona: Persona) {
  this.personaSeleccionada = persona;
  this.formModel.idPersona = persona.idPersona;
  // Mostrar texto correcto en el input
  this.searchTerm = this.obtenerNombrePersona(persona);
   // Cerrar dropdown
  this.mostrarResultados = false;
  // Limpiar lista
  this.personasFiltradas = [];
}



 
}
