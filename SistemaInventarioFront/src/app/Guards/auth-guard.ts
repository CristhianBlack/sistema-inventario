import { Injectable } from "@angular/core";
import { CanActivate, Router } from "@angular/router";
import { AutenticationService } from "../Servicios/autentication.service";

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

  constructor(
    private router: Router,
    private authService: AutenticationService
  ) {}

  canActivate(): boolean {

    if (!this.authService.isLogged()) {
      this.router.navigate(['/login']);
      return false;
    }
    return true;
  }
}
