import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CuentaContableService {

  URL = `${environment.apiUrl}/contabilidad/cuentas`

  constructor(private httPClient : HttpClient) { }

  cargarCuentas():Observable<any[]>{
    return this.httPClient.get<any>(this.URL);
  }
}
