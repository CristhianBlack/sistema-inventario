import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AutenticationService } from 'src/app/Servicios/autentication.service';

@Component({
  selector: 'app-cambiar-password',
  templateUrl: './cambiar-password.component.html',
  styleUrls: ['./cambiar-password.component.css'],
})
export class CambiarPasswordComponent {
  /**
   * Formulario reactivo para el cambio de contraseña
   */
  form: FormGroup;

  /**
   * Indica si la operación está en proceso
   * Se usa para deshabilitar el botón o mostrar un loader
   */
  loading = false;

  /**
   * Mensaje auxiliar (no obligatorio, pero útil para feedback)
   */
  mensaje = '';
  mostrarPassword = false;

  passwordStrength: 'Débil' | 'Media' | 'Fuerte' = 'Débil';
  passwordStrengthClass = 'text-danger';
  private passwordFuertePattern =/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&._-])[A-Za-z\d@$!%*?&._-]{8,}$/;

  constructor(
    private fb: FormBuilder,
    private authService: AutenticationService,
    private router: Router,
    private toastr: ToastrService,
  ) {
    /**
     * Inicialización del formulario
     * - username: se podrá obtener más adelante desde el token
     * - passwordActual: contraseña actual del usuario
     * - passwordNueva: nueva contraseña con validación mínima
     */
    this.form = this.fb.group({
      username: [''], // luego se puede obtener automáticamente del token
      passwordActual: ['', Validators.required],
      passwordNueva: ['', [Validators.required, Validators.minLength(8), Validators.pattern(this.passwordFuertePattern),]],
    });

    this.form.get('passwordNueva')?.valueChanges.subscribe((value) => {
  this.calcularFortaleza(value);
});
  }

  /**
   * Envía la solicitud para cambiar la contraseña del usuario
   * - Valida el formulario
   * - Llama al servicio de autenticación
   * - Cierra la sesión tras el cambio exitoso
   */
  cambiarPassword(): void {
    // Validación del formulario
    if (this.form.invalid) {
      return;
    }

    this.loading = true;

    this.authService.cambiarPassword(this.form.value).subscribe({
      next: () => {
        console.log('Contraseña cambiada correctamente');

        this.toastr.success(
          'Contraseña actualizada. Inicia sesión nuevamente.',
        );

        // Cierra la sesión y limpia el almacenamiento
        this.authService.logout();

        // Redirige al login
        this.router.navigate(['/login']);

        this.loading = false;
      },
      error: (err) => {
        // Logs útiles para depuración
        console.log('ERROR COMPLETO:', err);
        console.log('STATUS:', err.status);
        console.log('BODY:', err.error);

        // Muestra el mensaje enviado desde el backend
        this.toastr.error(err.error);

        this.loading = false;
      },
    });
  }

  togglePassword() {
    this.mostrarPassword = !this.mostrarPassword;
  }

  // Cuando presiona
  mostrar() {
    this.mostrarPassword = true;
  }

  // Cuando suelta / sale
  ocultar() {
    this.mostrarPassword = false;
  }

  calcularFortaleza(password: string) {
  if (!password) {
    this.passwordStrength = 'Débil';
    this.passwordStrengthClass = 'text-danger';
    return;
  }

  let score = 0;

  if (password.length >= 8) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/[a-z]/.test(password)) score++;
  if (/\d/.test(password)) score++;
  if (/[@$!%*?&._-]/.test(password)) score++;

  if (score <= 2) {
    this.passwordStrength = 'Débil';
    this.passwordStrengthClass = 'text-danger';
  } else if (score <= 4) {
    this.passwordStrength = 'Media';
    this.passwordStrengthClass = 'text-warning';
  } else {
    this.passwordStrength = 'Fuerte';
    this.passwordStrengthClass = 'text-success';
  }
}
}
