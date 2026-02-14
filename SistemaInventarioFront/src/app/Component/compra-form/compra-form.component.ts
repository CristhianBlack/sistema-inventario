import { ApplicationRef, Component, EventEmitter, Input, Output, SimpleChanges, ViewContainerRef } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import { Compra } from 'src/app/Modelos/compra';
import { Producto } from 'src/app/Modelos/producto';
import { Proveedor } from 'src/app/Modelos/proveedor';
import { CompraService } from 'src/app/Servicios/compra.service';
import { ProductoService } from 'src/app/Servicios/producto.service';
import { ProveedorService } from 'src/app/Servicios/proveedor.service';
import { ProductoFormComponent } from '../producto-form/producto-form.component';
import { MatDialog } from '@angular/material/dialog';
import { ModalProductoService } from 'src/app/Servicios/modalproductoservice.service';
import { Impuesto } from 'src/app/Modelos/impuesto';
import { ImpuestoService } from 'src/app/Servicios/impuesto.service';
import { Persona } from 'src/app/Modelos/persona';
import { TIPO_PERSONA } from 'src/app/Constantes/tipo-persona.const';
import { PersonaService } from 'src/app/Servicios/persona.service';

@Component({
  selector: 'app-compra-form',
  templateUrl: './compra-form.component.html',
  styleUrls: ['./compra-form.component.css']
})
export class CompraFormComponent {

 /**
   * Compra recibida desde el componente padre
   * - Si viene con datos → modo edición
   * - Si es null → modo creación
   */
  @Input() compra: Compra | null = null;

  /**
   * Evento emitido cuando se guarda correctamente
   */
  @Output() formGuardado = new EventEmitter<void>();

  /**
   * Modelo principal del formulario
   */
  formModel: Compra = new Compra();

  /**
   * Catálogos
   */
  productos: Producto[] = [];
  impuestos: Impuesto[] = [];
  // Personas que tienen el rol de proveedor
  listaProveedores: Persona[] = [];

  /**
   * Variables para el detalle de la compra
   */
  productoSeleccionado: Producto | null = null;
  impuestoSeleccionado: Impuesto | null = null;

  cantidad: number = 0;
  precio: number = 0;

  /**
   * Detalle de la compra (líneas)
   */
  detalleCompra: any[] = [];
  total: number = 0;

  /**
   * Control de edición de ítems
   */
  indiceEditando: number | null = null;

  /**
   * Control de modo solo lectura
   * Se activa cuando la compra ya fue confirmada, pagada o cancelada
   */
  modoSoloLectura: boolean = false;

   // ============================
    // AUTOCOMPLETE / BÚSQUEDA
    // ============================
    searchTerm: string = '';          // lo que escribe el usuario
    personaSeleccionada: Persona | null = null; // selección real
    mostrarResultados: boolean = false; // dropdown abierto/cerrado
    textoBusqueda: string = "";
    personasFiltradas: Persona[] = [];
     // Persona seleccionada (solo para mostrar información)
    proveedorSeleccionado: Persona | null = null;
    readonly TIPO_PERSONA_NATURAL = TIPO_PERSONA.NATURAL;
    readonly TIPO_PERSONA_JURIDICA = TIPO_PERSONA.JURIDICA;

  constructor(
    private compraService: CompraService,
    private toastr: ToastrService,
    private proveedorService: ProveedorService,
    private productoService: ProductoService,
    private modalProductoService: ModalProductoService,
    private impuestoService: ImpuestoService,
    private personaService: PersonaService
  ) {}

  /**
   * Inicialización del componente
   * - Carga catálogos
   * - Inicializa el total de la compra
   */
  ngOnInit(): void {
    this.cargarListas();
    this.formModel.totalCompra = 0;
  }

  /**
   * Detecta cambios en el @Input compra
   */
  ngOnChanges(changes: SimpleChanges): void {
    if (changes['compra'] && this.compra) {
      this.asignarDatosAlFormulario();
    }

    if (changes['compra'] && !this.compra) {
      this.formModel = new Compra();
      this.detalleCompra = [];
      this.total = 0;
    }
  }

  // ============================
  // CARGA DE CATÁLOGOS
  // ============================
  private cargarListas(): void {
    forkJoin({
      
      productos: this.productoService.obtenerListaProductos(),
      impuestos: this.impuestoService.listarImpuestos(),
      listaProveedores: this.personaService.obtenerPersonasConRolProveedor()
    }).subscribe({
      next: data => {
        //this.proveedores = data.proveedores;
        this.productos = data.productos;
        this.impuestos = data.impuestos;
        this.listaProveedores = data.listaProveedores;
        if (this.compra) {
          this.asignarDatosAlFormulario();
        }
      }
    });
  }

  /**
   * Asigna los datos de la compra al formulario
   * y carga los detalles en la tabla
   */
  private asignarDatosAlFormulario(): void {
    if (!this.compra) return;

    this.formModel = {
      ...this.compra,
      idProveedor: this.compra.idProveedor ?? null
    };

    // Cargar detalle de la compra
    this.detalleCompra = this.compra.detalles?.map(d => ({
      idProducto: d.idProducto,
      nombreProducto: d.nombreProducto,
      cantidad: d.cantidad,
      precioUnitario: d.precioUnitario,
      subtotalLinea: d.subtotalLinea,
      impuestoLinea: d.impuestoLinea,
      totalLinea: d.totalLinea
    })) || [];

    this.calcularTotal();

    // Activar modo solo lectura según estado
    this.modoSoloLectura =
      this.formModel.estado === 'CONFIRMADA' ||
      this.formModel.estado === 'PAGADA' ||
      this.formModel.estado === 'CANCELADA';

    if (this.modoSoloLectura) {
      this.toastr.info(
        'Esta compra ya fue registrada. No es posible modificar detalles.',
        'Modo solo lectura'
      );
    }
  }

  // ============================
  // MANEJO DEL DETALLE
  // ============================
  agregarItem(): void {
    if (!this.productoSeleccionado || this.cantidad <= 0) {
      this.toastr.warning('Complete todos los campos del detalle');
      return;
    }

    // Usar precio manual o precio del producto
    const precioUnitario =
      this.precio > 0
        ? this.precio
        : this.productoSeleccionado.precioCompra ?? 0;

    if (precioUnitario <= 0) {
      this.toastr.warning('El producto no tiene precio válido');
      return;
    }

    // Obtener impuesto del producto
    this.impuestoSeleccionado =
      this.obtenerImpuestoPorProducto(this.productoSeleccionado);

    const impuestoPct = this.impuestoSeleccionado?.porcentaje ?? 0;

    // Cálculos
    const subtotalLinea = precioUnitario * this.cantidad;
    const impuestoLinea = subtotalLinea * impuestoPct;
    const totalLinea = subtotalLinea + impuestoLinea;

    // Editar ítem existente
    if (this.indiceEditando !== null) {
      this.detalleCompra[this.indiceEditando] = {
        idProducto: this.productoSeleccionado.idProducto,
        codigoProducto: this.productoSeleccionado.codigoProducto,
        nombreProducto: this.productoSeleccionado.nombreProducto,
        cantidad: this.cantidad,
        precioUnitario,
        impuestoPct,
        subtotalLinea,
        impuestoLinea,
        totalLinea
      };
      this.indiceEditando = null;
      this.toastr.success('Ítem actualizado');
    } else {
      // Agregar nuevo ítem
      this.detalleCompra.push({
        idProducto: this.productoSeleccionado.idProducto,
        codigoProducto: this.productoSeleccionado.codigoProducto,
        nombreProducto: this.productoSeleccionado.nombreProducto,
        cantidad: this.cantidad,
        precioUnitario,
        impuestoPct,
        subtotalLinea,
        impuestoLinea,
        totalLinea
      });
    }

    this.calcularTotal();

    // Limpiar campos
    this.productoSeleccionado = null;
    this.cantidad = 0;
    this.precio = 0;
  }

  eliminarItem(i: number): void {
    this.detalleCompra.splice(i, 1);
    this.calcularTotal();
  }

  calcularTotal(): void {
    this.total = this.detalleCompra.reduce(
      (sum, item) => sum + item.totalLinea,
      0
    );
  }

  // ============================
  // MAPEO A REQUEST BACKEND
  // ============================
  private mapCompraToRequest(): any {
    return {
      idCompra: this.formModel.idCompra ?? null,
      fechaCompra: this.formModel.fechaCompra ?? null,
      idProveedor: Number(this.formModel.idProveedor),
      detalles: this.detalleCompra.map(item => ({
        cantidad: item.cantidad,
        precioUnitario: item.precioUnitario,
        idProducto: item.idProducto
      }))
    };
  }

  // ============================
  // GUARDAR COMPRA
  // ============================
  onSubmit(formCompra: NgForm): void {
    if (formCompra.invalid) {
      this.toastr.warning('Complete todos los campos requeridos');
      return;
    }

    if (this.detalleCompra.length === 0) {
      this.toastr.warning('Debe agregar al menos un producto');
      return;
    }

    const request = this.mapCompraToRequest();

    // CREAR
    this.compraService.agregarCompra(request).subscribe({
      next: () => {
        this.toastr.success('Compra agregada correctamente', 'Éxito');
        this.formGuardado.emit();
        this.limpiarFormulario(formCompra);
        console.log('Id proveedor que llega de la vista ', this.formModel.idProveedor)
      },
      error: err => {
        console.error(err);
        this.toastr.error(err.mensaje, 'Error');
      }
    });
  }

  /**
   * Limpia formulario y detalle
   */
  limpiarFormulario(formCompra?: NgForm): void {
    this.formModel = new Compra();
    this.compra = null;
    this.detalleCompra = [];
    this.total = 0;
    formCompra?.resetForm();
  }

  /**
   * Edita un ítem del detalle
   */
  editarItem(i: number): void {
    const item = this.detalleCompra[i];
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
  }

  /**
   * Abre modal para crear producto
   */
  abrirModalProducto(): void {
    this.modalProductoService.abrirModal().subscribe(productoCreado => {
      if (productoCreado) {
        this.productoService.obtenerListaProductos().subscribe(data => {
          this.productos = data;
        });
      }
    });
  }

  /**
   * Obtiene el impuesto asociado a un producto
   */
  obtenerImpuestoPorProducto(producto: Producto | null): Impuesto | null {
    if (!producto || producto.idImpuesto == null) return null;

    return (
      this.impuestos.find(i => i.idImpuesto === producto.idImpuesto) ?? null
    );
  }

  /**
   * Cuando se selecciona un producto,
   * se asigna automáticamente su precio de compra
   */
  onProductoSeleccionado(): void {
    if (this.productoSeleccionado) {
      this.precio = this.productoSeleccionado.precioCompra ?? 0;
    }
  }

  // ============================
  // AUTOCOMPLETE / FILTRADO
  // ============================
onBuscar(valor: string) {
  console.log("Lista proveedores:", this.listaProveedores);
  console.log("Valor buscado:", valor);
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
  this.proveedorSeleccionado = persona;
  this.formModel.idProveedor = persona.idPersona;
  // Mostrar texto correcto en el input
  this.searchTerm = this.obtenerNombrePersona(persona);
   // Cerrar dropdown
  this.mostrarResultados = false;
  // Limpiar lista
  this.personasFiltradas = [];
}



}

