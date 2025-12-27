import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { EstadoPago } from 'src/app/Modelos/estado-pago';
import { FormaPago } from 'src/app/Modelos/forma-pago';
import { Venta } from 'src/app/Modelos/venta';
import { VentaPago } from 'src/app/Modelos/venta-pago';
import { FormaPagoService } from 'src/app/Servicios/forma-pago.service';
import { VentaPagoService } from 'src/app/Servicios/venta-pago.service';
import { VentaService } from 'src/app/Servicios/venta.service';

@Component({
  selector: 'app-venta-pago-form',
  templateUrl: './venta-pago-form.component.html',
  styleUrls: ['./venta-pago-form.component.css']
})
export class VentaPagoFormComponent{

  @Input() idVenta!: number;

  pagos: VentaPago[] = [];
  monto = 0;
  estadoPago: EstadoPago = EstadoPago.PENDIENTE_CONFIRMACION;

  EstadoPago = EstadoPago;
  totalVenta : number = 0;
  venta : Venta[] = [];
  estado : string ="";
  codigoVenta ?: number = 0;
  formaPagos : FormaPago[] = [];
  idFormaPago: number | null = null;

  constructor(private ventaPagoService: VentaPagoService,
    private ventaService : VentaService,
    private formaPagoService : FormaPagoService,
    private toastr : ToastrService
  ) {}

ngOnChanges(changes: SimpleChanges): void {
    if (changes['idVenta'] && this.idVenta) {
      this.cargarPagos();
      this.cargarVenta();
    }
    this.cargarFormaPago();
  }

  cargarPagos():void{
    this.ventaPagoService
  .listarPagosPorVenta(this.idVenta)
  .subscribe(data => {
    this.pagos = data ?? [];
  });
  } 

  cargarFormaPago(): void{
    this.formaPagoService.obtenerListaFormaPago().subscribe(
      data =>{
        this.formaPagos = data;
      }
    );
  }

  cargarVenta():void{
    this.ventaService.obtenerVentaPorId(this.idVenta).subscribe(data =>{
      console.log('VENTA:', data);
      this.totalVenta = data.totalVenta;
      this.estado = data.estado;
      this.codigoVenta = data.idVenta;
    })
  }

  registrarPago(): void {
    if (this.monto <= 0) return;

    this.ventaPagoService.registrarPago(this.idVenta, {
      monto: this.monto,
      estadoPago: this.estadoPago,
      idFormaPago : this.idFormaPago,
      
    }).subscribe(() => {
      this.monto = 0;
      this.toastr.success('Se agrego registro correctamente', 'Éxito');
      console.log('IdFormaPago recibido es :', this.idFormaPago)
      this.cargarPagos();
    });
  }

  confirmarPago(idPago: number): void {
    this.ventaPagoService.confirmarPago(idPago).subscribe(() => {
      this.cargarPagos();
    });
  }

  rechazarPago(idPago: number): void {
    this.ventaPagoService.rechazarPago(idPago).subscribe(() => {
      this.cargarPagos();
    });
  }

  get totalPagado(): number {
  if (!Array.isArray(this.pagos)) return 0;
  return this.pagos
    .filter(p => p.estadoPago === 'CONFIRMADO')
    .reduce((sum, p) => sum + p.monto, 0);
}

get saldoPendiente(): number {
  return this.totalVenta - this.totalPagado;
}

trackByPago(index: number, pago: any): number {
  return pago.idVentaPago;
}

}
