import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { catchError, Observable, throwError } from "rxjs";
import { AutenticationService } from "../Servicios/autentication.service";


@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(
    private authService: AutenticationService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

  const usuarioStr = localStorage.getItem('usuario');

  let authReq = req;

  if (usuarioStr) {
    const usuario = JSON.parse(usuarioStr);
    const token = usuario.token; // AQUÍ ESTÁ EL JWT

    if (token) {
      authReq = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }
  }

  return next.handle(authReq).pipe(
    catchError((error: HttpErrorResponse) => {

      if (error.status === 401) {
        localStorage.clear();
        sessionStorage.clear();
        this.router.navigate(['/login']);
      }

      if (error.status === 403) {
        alert('No tienes permisos para acceder a esta sección');
        this.router.navigate(['/dashboard']);
      }

      return throwError(() => error);
    })
  );
 }
}
