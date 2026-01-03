import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { forkJoin } from 'rxjs';
import { DetalleVenta } from 'src/app/Modelos/detalle-venta';
import { DevolucionRequest } from 'src/app/Modelos/devolucion-request';
import { Impuesto } from 'src/app/Modelos/impuesto';
import { Producto } from 'src/app/Modelos/producto';
import { TipoDevolucion } from 'src/app/Modelos/tipo-devolucion';
import { Venta } from 'src/app/Modelos/venta';
import { DevolucionService } from 'src/app/Servicios/devolucion.service';
import { ImpuestoService } from 'src/app/Servicios/impuesto.service';
import { ProductoService } from 'src/app/Servicios/producto.service';
import { VentaService } from 'src/app/Servicios/venta.service';

@Component({
  selector: 'app-devolucion-form',
  templateUrl: './devolucion-form.component.html',
  styleUrls: ['./devolucion-form.component.css']
})
export class DevolucionFormComponent implements OnInit{

  // 🔹 Listas
  ventas: Venta[] = [];
  detallesVenta: DetalleVenta[] = [];
  productos: Producto[] = [];
  detalles : any[] = [];
  impuestos : Impuesto[]=[];

  // 🔹 Selecciones
  idVentaSeleccionada: number | null = null;
  ventaSeleccionada: Venta | null = null;
  detalleSeleccionado: DetalleVenta | null = null;

  // 🔹 Datos devolución
  cantidadDevuelta: number = 1;
  valorDevuelto: number = 0;
  tipoDevolucion: TipoDevolucion = TipoDevolucion.SALDO_FAVOR;
  idProductoCambio: number | null = null;
  motivo: string = '';

  // 🔹 Enum para HTML
  TipoDevolucion = TipoDevolucion;

  constructor(
    private devolucionService: DevolucionService,
    private ventaService: VentaService,
    private productoService : ProductoService,
    private impuestoService : ImpuestoService
  ) {}

  ngOnInit(): void {
    this.cargarListas();
  }

  // ============================
      // CARGAR CATÁLOGOS
      // ============================
      private cargarListas(): void {
        forkJoin({
          ventas: this.ventaService.listarVentas(),
          productos: this.productoService.obtenerListaProductos(),
          impuestos : this.impuestoService.listarImpuestos()
        }).subscribe({
          next: data => {
            this.ventas = data.ventas;
            this.productos = data.productos;
            this.impuestos = data.impuestos;
          }
        });
      }

  onVentaChange() {
     console.log('Venta seleccionada:', this.ventaSeleccionada);
     console.log('Detalles:', this.ventaSeleccionada?.detalles);

    /*this.ventaSeleccionada =
      this.ventas.find(v => v.idVenta === this.idVentaSeleccionada) || null;

    this.detallesVenta = this.ventaSeleccionada?.detalles ?? [];
    this.detalleSeleccionado = null;
    this.valorDevuelto = 0;*/

    /*if (this.ventaSeleccionada?.detalles?.length) {
        this.detalleSeleccionado = this.ventaSeleccionada.detalles[0];
    } else {
        this.detalleSeleccionado = null;
    }*/
   console.log('Venta seleccionada:', this.ventaSeleccionada);

  if (!this.ventaSeleccionada) {
    this.detalles = [];
    return;
  }

  this.detalles = this.ventaSeleccionada.detalles ?? [];
   this.detalleSeleccionado = this.detalles.length > 0 ? this.detalles[0] : null;
  console.log('Detalles:', this.detalles);
  }

  onDetalleChange() {
    this.recalcularDevolucion();
  }

  recalcularDevolucion() {
    if (!this.detalleSeleccionado || !this.cantidadDevuelta || this.cantidadDevuelta <= 0) {
    this.valorDevuelto = 0;
    return;
  }

  const totalLinea = this.detalleSeleccionado.totalLinea;
  const cantidadVendida = this.detalleSeleccionado.cantidad;

  const valorUnitarioReal = totalLinea / cantidadVendida;

  this.valorDevuelto = valorUnitarioReal * this.cantidadDevuelta;

  }

  guardarDevolucion() {
    if (!this.ventaSeleccionada || !this.detalleSeleccionado) {
      alert('Debe seleccionar venta y producto');
      return;
    }

    const request: DevolucionRequest = {
      idVenta: this.ventaSeleccionada.idVenta!,
      idProducto: this.detalleSeleccionado.idProducto!,
      cantidad: this.cantidadDevuelta,
      motivo: this.motivo,
      //valorDevuelto : this.valorDevuelto,
      tipoDevolucion: this.tipoDevolucion,
      idProductoCambio: this.tipoDevolucion === TipoDevolucion.CAMBIO
        ? this.idProductoCambio
        : null
    };

    this.devolucionService.registrarDevolucion(request).subscribe(() => {
      alert('Devolución registrada correctamente');
    });
  }

  // Método para obtener el impuesto del detalle
  getImpuestoDetalle(): Impuesto | null {
  if (!this.detalleSeleccionado || this.detalleSeleccionado.idImpuesto == null) {
    return null;
  }

  return this.impuestos.find(
    i => i.idImpuesto === this.detalleSeleccionado!.idImpuesto
  ) ?? null;
}

}
