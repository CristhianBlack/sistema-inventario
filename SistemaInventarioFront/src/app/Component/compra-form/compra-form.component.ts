/*import { Component, EventEmitter, Input, Output, SimpleChanges } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import { Compra } from 'src/app/Modelos/compra';
import { Proveedor } from 'src/app/Modelos/proveedor';
import { CompraService } from 'src/app/Servicios/compra.service';
import { ProveedorService } from 'src/app/Servicios/proveedor.service';

@Component({
  selector: 'app-compra-form',
  templateUrl: './compra-form.component.html',
  styleUrls: ['./compra-form.component.css']
})
export class CompraFormComponent {
 @Input() compra: Compra | null = null;
 @Output() formGuardado = new EventEmitter<void>();
   
     formModel: Compra = new Compra();
   
     
     proveedores: Proveedor[] = [];
   
   
     constructor(
       private compraService: CompraService,
       private toastr: ToastrService,
       private proveedorService: ProveedorService
     ) {}
   
     ngOnInit(): void {
       this.cargarListas();
     }
   
    
   ngOnChanges(changes: SimpleChanges): void {
     if (changes['compra'] && this.compra) {
       this.asignarDatosAlFormulario(); // ✔ Solo asignar datos
     }
   
     if (changes['compra'] && !this.compra) {
       this.formModel = new Compra(); // ✔ para modo crear
     }
   }
   
   
   
     private cargarListas(): void {
       forkJoin({
         proveedores: this.proveedorService.listarProveedorPersona(),
       }).subscribe({
         next: data => {
           this.proveedores = data.proveedores;
           
   
           if (this.compra) {
             this.asignarDatosAlFormulario();
           }
         }
       });
     }
   
     private asignarDatosAlFormulario(): void {
     if (!this.compra) return;
   
     this.formModel = {
       ...this.compra,
   
       idProveedor: this.compra.idProveedor ?? null,
     };
     console.log("FORM MODEL AL ABRIR EDITAR:", this.formModel);
     console.log("LISTAS:", {
       proveedores: this.proveedores
     });
   }
   
     private mapPersonaToRequest(): any {
     return {
       idCompra: this.formModel.idCompra ?? null,
       fechaCompra: this.formModel.fechaCompra,
       total: this.formModel.total,
 
     // NOMBRES CORRECTOS que espera tu DTO
     idProveedor :  Number(this.formModel.idProveedor) 
       
     };
   }
   
   
   
     onSubmit(formCompra: NgForm): void {
       if (formCompra.invalid) {
         this.toastr.warning('Complete todos los campos requeridos');
         return;
       }
       const request = this.mapPersonaToRequest();
       console.log("JSON que envío:", JSON.stringify(request, null, 2));
   
       // EDITAR
       if (this.formModel.idCompra) {
         this.compraService.editarCompra(this.formModel.idCompra, request)
           .subscribe(() => {
             console.log("Estoy validando donde estoy entrando ");
             this.toastr.success('Compra actualizado');
             this.formGuardado.emit();
             this.limpiarFormulario();
           });
         return;
       }else{
         console.log("Entro al else de crear");
         // CREAR
         this.compraService.agregarCompra(request)
         .subscribe({
           next: () => {
             console.log("Entro al subscribe y al next ");
             this.toastr.success('Compra agregada correctamente', 'Éxito');
             this.formGuardado.emit();
             this.limpiarFormulario(formCompra);
             console.log("Enviando REAL al backend:", JSON.stringify(request, null, 2));
           console.log("JSON FINAL:", request);
           
           },
           error: (err) => {
             console.error("ERROR COMPLETO DEL SERVIDOR:", err);
             this.toastr.error(err.message || 'Error al crear persona', 'Error');
           }
       });
       }   
     }
   
     limpiarFormulario(formCompra?: NgForm): void {
       this.formModel = new Compra();
       formCompra?.resetForm();
         this.compra = null;
     this.formModel = new Compra();
   
     
     this.formModel.idProveedor = null;
   
     }
}*/

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

@Component({
  selector: 'app-compra-form',
  templateUrl: './compra-form.component.html',
  styleUrls: ['./compra-form.component.css']
})
export class CompraFormComponent {

  @Input() compra: Compra | null = null;
  @Output() formGuardado = new EventEmitter<void>();

  formModel: Compra = new Compra();

  proveedores: Proveedor[] = [];

  // ============================
  // NUEVAS VARIABLES DETALLE
  // ============================
  productos: Producto[] = [];         // cargarás tu lista real después
 productoSeleccionado : Producto | null = null;
 impuestoSeleccionado: Impuesto | null = null;

  cantidad: number = 0;
  precio: number = 0;

  detalleCompra: any[] = [];
  total: number = 0;

  indiceEditando: number | null = null;

  products: any[] = [];
  selectedProducto: any;

  modoSoloLectura: boolean = false;

  impuestos : Impuesto[]=[];

  // ============================


  constructor(
    private compraService: CompraService,
    private toastr: ToastrService,
    private proveedorService: ProveedorService,
    private productoService : ProductoService,
    private dialog: MatDialog,
    private modalProductoService : ModalProductoService,
    private impuestoService : ImpuestoService

  ) {}

  ngOnInit(): void {
    this.cargarListas();
    this.formModel.totalCompra = 0;
  }

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
  // CARGAR CATÁLOGOS
  // ============================
  private cargarListas(): void {
    forkJoin({
      proveedores: this.proveedorService.listarProveedorPersona(),
      productos: this.productoService.obtenerListaProductos(),
      impuestos : this.impuestoService.listarImpuestos()
    }).subscribe({
      next: data => {
        this.proveedores = data.proveedores;
        this.productos = data.productos;
        this.impuestos = data.impuestos

        if (this.compra) {
          this.asignarDatosAlFormulario();
        }
      }
    });
  }


  private asignarDatosAlFormulario(): void {
  if (!this.compra) return;

  this.formModel = {
    ...this.compra,
    idProveedor: this.compra.idProveedor ?? null,
  };

  // ←← CARGAR DETALLES EN LA TABLA
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

  //activamos el modo lecutra
    this.modoSoloLectura = this.formModel.estado === 'CONFIRMADA' ||
  this.formModel.estado === 'PAGADA' ||
  this.formModel.estado === 'CANCELADA';

  // mensaje informativo
  if (this.modoSoloLectura) {
    this.toastr.info(
      "Esta compra ya fue registrada. No es posible modificar detalles.",
      "Modo solo lectura"
    );
  }


  console.log("FORM MODEL AL ABRIR EDITAR:", this.formModel);
  console.log("DETALLES CARGADOS:", this.detalleCompra);
}




  // ============================
  // MANEJO DEL DETALLE
  // ============================

  agregarItem() {
    if (!this.productoSeleccionado || this.cantidad <= 0) {
    this.toastr.warning("Complete todos los campos del detalle");
    return;
  }

  // 🔹 Si el usuario no cambia el precio, usar el del producto
  const precioUnitario =
    this.precio > 0
      ? this.precio
      : this.productoSeleccionado.precioCompra ?? 0;

  if (precioUnitario <= 0) {
    this.toastr.warning("El producto no tiene precio válido");
    return;
  }

  // 🔹 Impuesto
  this.impuestoSeleccionado = this.obtenerImpuestoPorProducto(this.productoSeleccionado);
  const impuestoPct = this.impuestoSeleccionado?.porcentaje ?? 0;

  // 🔹 Cálculos
  const subtotalLinea = precioUnitario * this.cantidad;
  const impuestoLinea = subtotalLinea * impuestoPct;
  const totalLinea = subtotalLinea + impuestoLinea;

  // Si estamos editando un ítem
  if (this.indiceEditando !== null) {
    this.detalleCompra[this.indiceEditando] = {
      idProducto: this.productoSeleccionado.idProducto,
      nombreProducto: this.productoSeleccionado.nombreProducto,
      cantidad: this.cantidad,
      precioUnitario,
      subtotalLinea,
      totalLinea
    };

    this.indiceEditando = null;
    this.toastr.success("Ítem actualizado");
  } else {
    // Crear nuevo ítem
    this.detalleCompra.push({
      idProducto: this.productoSeleccionado.idProducto,
      nombreProducto: this.productoSeleccionado.nombreProducto,
      cantidad: this.cantidad,
      precioUnitario,
      subtotalLinea,
      totalLinea
    });
  }

  this.calcularTotal();

    // Limpiar
    this.productoSeleccionado = null;
    this.cantidad = 0;
    this.precio = 0;
  }

  eliminarItem(i: number) {
    this.detalleCompra.splice(i, 1);
    this.calcularTotal();
  }

  calcularTotal() {
    this.total = this.detalleCompra.reduce((sum, item) => sum + item.totalLinea, 0);
    console.log("Total ", this.total);
   // this.formModel.totalCompra = this.total; // enviarlo al backend
  }


  // ============================
  // MAP A REQUEST FINAL
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
  // GUARDAR
  // ============================
  onSubmit(formCompra: NgForm): void {
    if (formCompra.invalid) {
      this.toastr.warning('Complete todos los campos requeridos');
      return;
    }

    if (this.detalleCompra.length === 0) {
      this.toastr.warning("Debe agregar al menos un producto");
      return;
    }

    const request = this.mapCompraToRequest();
    console.log("JSON FINAL:", request);

    // EDITAR
    if (this.formModel.idCompra) {
      this.compraService.obtenerCompraPorId(this.formModel.idCompra)
        .subscribe(() => {
          this.toastr.success('Compra actualizada correctamente');
          this.formGuardado.emit();
          this.limpiarFormulario();
        });
      return;
    }

    // CREAR
    this.compraService.agregarCompra(request).subscribe({
      next: () => {
        this.toastr.success('Compra agregada correctamente', 'Éxito');
        this.formGuardado.emit();
        this.limpiarFormulario(formCompra);
      },
      error: (err) => {
        console.error("ERROR COMPLETO DEL SERVIDOR:", err);
        this.toastr.error(err.message || 'Error al crear compra', 'Error');
      }
    });
  }



  // ============================
  // LIMPIAR FORMULARIO
  // ============================
  limpiarFormulario(formCompra?: NgForm): void {
    this.formModel = new Compra();
    this.compra = null;

    // limpiar detalle
    this.detalleCompra = [];
    this.total = 0;

    formCompra?.resetForm();
  }

  // ============================
  // METODO PARA EDITAR UN ITEM EXISTENTE
  // ============================
  editarItem(i: number) {
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
  this.precio = item.precio;
}

abrirModalProducto() {
    this.modalProductoService.abrirModal().subscribe((productoCreado) => {
      if (productoCreado) {
        // Recargar productos y seleccionar el nuevo
        this.productoService.obtenerListaProductos().subscribe((data) => {
          this.productos = data;
          this.selectedProducto = productoCreado.idProducto;
        });
      }
    });
  }

   obtenerImpuestoPorProducto(producto: Producto | null): Impuesto | null {
  if (!producto || producto.idImpuesto == null) {
    return null;
  }

  return this.impuestos.find(
    i => i.idImpuesto === producto.idImpuesto
  ) ?? null;
}

onProductoSeleccionado() {
  if (this.productoSeleccionado) {
    this.precio = this.productoSeleccionado.precioCompra ?? 0;
  }
}


}

