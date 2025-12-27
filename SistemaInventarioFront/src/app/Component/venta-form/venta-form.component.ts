import { Component, EventEmitter, Input, OnInit, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
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
        personas: this.personaService.obtenerListaPersona(),
        productos: this.productoService.obtenerListaProductos(),
        impuestos : this.impuestoService.listarImpuestos()
      }).subscribe({
        next: data => {
          this.personas = data.personas;
          this.productos = data.productos;
          this.impuestos = data.impuestos;
  
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
  
      const precio = this.productoSeleccionado.precioVenta;
      this.impuestoSeleccionado = this.obtenerImpuestoPorProducto(this.productoSeleccionado);

      const impuestoPct = this.impuestoSeleccionado?.porcentaje ?? 0;
      const subtotalLinea = precio * this.cantidad;
      const impuestoValor = subtotalLinea * impuestoPct;
      const totalLinea = subtotalLinea + impuestoValor - this.descuento;
  
    // Si estamos editando un ítem
    if (this.indiceEditando !== null) {
      this.detalleVenta[this.indiceEditando] = {
        idProducto: this.productoSeleccionado.idProducto,
        nombreProducto: this.productoSeleccionado.nombreProducto,
        cantidad: this.cantidad,
        precioVenta: precio,
        impuestoPct,
        impuestoValor,
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
        nombreProducto: this.productoSeleccionado.nombreProducto,
        cantidad: this.cantidad,
        precioUnitario: precio,
        impuestoPct,
        impuestoValor,
        descuento: this.descuento,
        subtotalLinea,
        totalLinea
      });
    }
  
    this.calcularTotal();
  
      // Limpiar
      this.productoSeleccionado = null;
      this.cantidad = 0;
      this.descuento = 0;
    }
  
    eliminarItem(i: number) {
      this.detalleVenta.splice(i, 1);
      this.calcularTotal();
    }
  
    calcularTotal() {
      this.total = this.detalleVenta.reduce((sum, item) => sum + item.totalLinea, 0);
      //this.formModel.totalVenta = this.totalLinea; // enviarlo al backend
    }
  
  
    // ============================
    // MAP A REQUEST FINAL
    // ============================
    private mapDatoToRequest(): any {
    return {
      idVenta: this.formModel.idVenta ?? null,
      fechaVenta: this.formModel.fechaVenta ?? null,
      idPersona: Number(this.formModel.idPersona),
  
      detalles: this.detalleVenta.map(item => ({
        cantidad: item.cantidad,
        idProducto: item.idProducto,
        descuento : item.descuento
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




 
}
