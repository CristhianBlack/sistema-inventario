import { Component, Input, SimpleChanges } from '@angular/core';
import { ToastrService } from 'ngx-toastr';
import { Compra } from 'src/app/Modelos/compra';
import { CompraPagos } from 'src/app/Modelos/compra-pagos';
import { EstadoPago } from 'src/app/Modelos/estado-pago';
import { FormaPago } from 'src/app/Modelos/forma-pago';
import { CompraPagoService } from 'src/app/Servicios/compra-pago.service';
import { CompraService } from 'src/app/Servicios/compra.service';
import { FormaPagoService } from 'src/app/Servicios/forma-pago.service';

@Component({
  selector: 'app-compra-pago',
  templateUrl: './compra-pago.component.html',
  styleUrls: ['./compra-pago.component.css']
})
export class CompraPagoComponent {

  @Input() idCompra!: number;
  
    pagos: CompraPagos[] = [];
    monto = 0;
    estadoPago: EstadoPago = EstadoPago.PENDIENTE_CONFIRMACION;
  
    EstadoPago = EstadoPago;
    totalCompra : number = 0;
    compra : Compra[] = [];
    estado : string ="";
    codigoCompra ?: number = 0;
    formaPagos : FormaPago[] = [];
    idFormaPago: number | null = null;
  
    constructor(private compraPagoService: CompraPagoService,
      private compraService : CompraService,
      private formaPagoService : FormaPagoService,
      private toastr : ToastrService
    ) {}
  
  ngOnChanges(changes: SimpleChanges): void {
      if (changes['idCompra'] && this.idCompra) {
        this.cargarPagos();
        this.cargarCompra();
      }
      this.cargarFormaPago();
    }
  
    cargarPagos():void{
      this.compraPagoService
    .listarPagosPorCompra(this.idCompra)
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
  
    cargarCompra():void{
      this.compraService.obtenerCompraPorId(this.idCompra).subscribe(data =>{
        console.log('Compra:', data);
        this.totalCompra = data.totalCompra;
        this.estado = data.estado;
        this.codigoCompra = data.idCompra;
      })
    }
  
    registrarPago(): void {
      if (this.monto <= 0) return;
  
      this.compraPagoService.registrarPago(this.idCompra, {
        montoCompra: this.monto,
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
      this.compraPagoService.confirmarPago(idPago).subscribe(() => {
        this.cargarPagos();
      });
    }
  
    rechazarPago(idPago: number): void {
      this.compraPagoService.rechazarPago(idPago).subscribe(() => {
        this.cargarPagos();
      });
    }
  
    get totalPagado(): number {
    if (!Array.isArray(this.pagos)) return 0;
    return this.pagos
      .filter(p => p.estadoPago === 'CONFIRMADO')
      .reduce((sum, p) => sum + p.montoCompra, 0);
  }
  
  get saldoPendiente(): number {
    return this.totalCompra - this.totalPagado;
  }
  
  trackByPago(index: number, pago: any): number {
    return pago.idVentaPago;
  }
}
