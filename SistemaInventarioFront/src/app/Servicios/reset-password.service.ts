import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {

   Url = `${environment.apiUrl}/auth`;

  constructor(private httpClient: HttpClient) {}

  solicitarToken(username: string): Observable<any> {
    return this.httpClient.post(`${this.Url}/forgot-password`, { username }, { responseType: 'text' });
  }

  resetearPassword(token: string, nuevaPassword: string): Observable<any> {
    return this.httpClient.post(`${this.Url}/reset-password`, {
      token,nuevaPassword}, {responseType: 'text'});
  }
}
