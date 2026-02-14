import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, Observable, tap, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface ChangePasswordRequest {
  username: string;
  passwordActual: string;
  passwordNueva: string;
}

@Injectable({
  providedIn: 'root'
})
export class AutenticationService {

  URL = `${environment.apiUrl}/auth`;

  constructor(
    private httpClient: HttpClient,
    private router: Router
  ) {}

  login(username: string, password: string): Observable<any> {
    return this.httpClient.post<any>(`${this.URL}/login`, {
      username,
      password
    }).pipe(catchError(this.manejarError),
      tap(response => {
        localStorage.setItem('usuario', JSON.stringify(response));
        //localStorage.setItem('debeCambiarPassword', String(response.debeCambiarPassword));
      })
    );
  }

  getUsuario() {
    const user = localStorage.getItem('usuario');
    return user ? JSON.parse(user) : null;
  }

  getNombreCompleto(): string {
  const user = this.getUsuario();
  return user ? user.nombreCompleto : '';
}

  getRol(): string | null {
    const user = this.getUsuario();
    return user ? user.rol : null;
  }

  isLogged(): boolean {
    return !!localStorage.getItem('usuario');
  }

  logout(): void {
    localStorage.clear();      // limpia TODO
    sessionStorage.clear();    // por seguridad
   // localStorage.removeItem('debeCambiarPassword');
   // this.router.navigate(['/login']);
  }

  cambiarPassword(data: ChangePasswordRequest): Observable<any> {
    console.log("DATA QUE SE ENVIA:", data);
    return this.httpClient.put(`${this.URL}/cambiar-password`, data, {responseType: 'text'}).pipe(catchError(this.manejarError));
  }

  private manejarError(error: HttpErrorResponse) {

  console.log("ERROR REAL DEL BACKEND:", error);

  return throwError(() => error);
}

  /*private manejarError(error: HttpErrorResponse) {
    let mensaje = 'Ocurrió un error';
  
    if (error.error?.mensaje) {
      mensaje = error.error.mensaje;
    } else if (error.status === 404) {
      mensaje = error.error.mensaje;
    } else if (error.status === 500) {
      mensaje = error.error.mensaje;
    }else if(error.status === 400){
      mensaje = error.error.mensaje;
    }
  
    return throwError(() => ({
      status: error.status,
      mensaje
    }));
  }*/

  /*private manejarError(error: HttpErrorResponse) {

  let mensaje =
      error.error?.mensaje ||
      error.error?.message ||
      'Ocurrió un error';

  return throwError(() => ({
    status: error.status,
    mensaje
  }));
}*/
}
