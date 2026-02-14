import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { MayorGeneral } from '../Modelos/mayor-general';
import { MayorGeneralPage } from '../Modelos/mayor-general-page';

@Injectable({
  providedIn: 'root'
})
export class MayorGeneralService {

  URL = `${environment.apiUrl}/contabilidad/mayor-general`;

  constructor(private httpClient : HttpClient) { }

  obtenerMayorPaginado(
      idCuenta: number,
      page: number,
      size: number,
      desde?: string | null,
      hasta?: string | null
  ): Observable<MayorGeneralPage> {

    let params = new HttpParams()
        .set('idCuenta', idCuenta.toString())
        .set('page', page.toString())
        .set('size', size.toString());

    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);

    return this.httpClient.get<MayorGeneralPage>(this.URL, { params });
  }
  exportarExcel(idCuenta: number, desde?: string, hasta?: string) {
    let params = new HttpParams().set('idCuenta', idCuenta);

    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);

    return this.httpClient.get(`${this.URL}/excel`,{ params, responseType: 'blob' }
    );
  }

  exportarPdf(idCuenta: number, desde?: string, hasta?: string) {
    let params = new HttpParams().set('idCuenta', idCuenta);

    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);

    return this.httpClient.get(`${this.URL}/pdf`,{ params, responseType: 'blob' }
    );
  }

  

  
}
