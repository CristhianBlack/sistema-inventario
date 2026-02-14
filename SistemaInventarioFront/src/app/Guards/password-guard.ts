import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AutenticationService } from '../Servicios/autentication.service';

@Injectable({
  providedIn: 'root'
})
export class PasswordGuard implements CanActivate {

  constructor(
    private router: Router,
    private authService: AutenticationService
  ) {}

  canActivate(): boolean {

    const user = this.authService.getUsuario();

    if (user?.debeCambiarPassword) {
      this.router.navigate(['/cambiar-password']);
      return false;
    }

    return true;
  }
}
