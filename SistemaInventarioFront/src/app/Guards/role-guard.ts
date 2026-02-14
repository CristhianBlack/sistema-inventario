import { Injectable } from "@angular/core";
import { CanActivate, Router } from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {

  constructor(private router: Router) {}

  canActivate(): boolean {
    const rol = localStorage.getItem('rol');

    if (rol === 'ADMIN_SISTEMA') {
      return true;
    }

    this.router.navigate(['/dashboard']);
    return false;
  }
}

