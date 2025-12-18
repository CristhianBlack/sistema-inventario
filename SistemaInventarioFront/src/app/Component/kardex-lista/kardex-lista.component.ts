import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Kardex } from 'src/app/Modelos/kardex';
import { KardexserviceService } from 'src/app/Servicios/kardexservice.service';

@Component({
  selector: 'app-kardex-lista',
  templateUrl: './kardex-lista.component.html',
  styleUrls: ['./kardex-lista.component.css']
})
export class KardexListaComponent implements OnInit{

  kardex : Kardex[] = [];
 
  productoId!: number;

  saldoActual: number = 0;
  desde!: string;
  hasta!: string;

  constructor(
    private kardexService: KardexserviceService,
    private route : ActivatedRoute) {}

  ngOnInit(): void {
    console.log('ENTRÉ A KARDEX');
  this.route.paramMap.subscribe(params => {
    const id = params.get('id');
    if (id) {
      this.productoId = +id;
      this.cargarKardex();
    }
  });
}

cargarKardex(): void {
  this.kardexService.getKardex(this.productoId)
    .subscribe(data =>{
      console.log('Kardex recibido:', data);
      this.kardex = data.reverse()

      // calcular saldo actual (último registro)
      if (this.kardex.length > 0) {
         this.saldoActual = this.kardex[0].saldo;
      } else {
        this.saldoActual = 0;
      }
    });      
}

filtrar(): void {
  if (!this.desde || !this.hasta) return;

  this.kardexService
    .getKardexPorFechas(this.productoId, this.desde, this.hasta)
    .subscribe(data => {

      this.kardex = data;

      if (this.kardex.length > 0) {
        this.saldoActual = this.kardex[this.kardex.length - 1].saldo;
      } else {
        this.saldoActual = 0;
      }
    });
}

descargarExcel() {
  this.kardexService.exportarExcel(this.productoId, this.desde, this.hasta)
    .subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'kardex.xlsx';
      a.click();
      window.URL.revokeObjectURL(url);
    });
}

descargarPdf() {
  this.kardexService.exportarPdf(this.productoId, this.desde, this.hasta)
    .subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'kardex.pdf';
      a.click();
      window.URL.revokeObjectURL(url);
    });
}


}
