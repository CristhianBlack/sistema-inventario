import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ResetPasswordService } from 'src/app/Servicios/reset-password.service';

@Component({
  selector: 'app-password-recovery',
  templateUrl: './password-recovery.component.html',
  styleUrls: ['./password-recovery.component.css'],
})
export class PasswordRecoveryComponent {
  step = 1;
  mensaje = '';
  tokenGenerado = '';
  mostrarPassword = false;

  formUsuario: FormGroup;
  formReset: FormGroup;

  passwordStrength: 'Débil' | 'Media' | 'Fuerte' = 'Débil';
  passwordStrengthClass = 'text-danger';

  constructor(
    private fb: FormBuilder,
    private passwordResetService: ResetPasswordService,
  ) {
    this.formUsuario = this.fb.group({
      username: ['', Validators.required],
    });

    this.formReset = this.fb.group({
      nuevaPassword: [
        '',
        [
          Validators.required,
          Validators.minLength(8),
          Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/),
        ],
      ],
    });

    this.formReset.get('nuevaPassword')?.valueChanges.subscribe((value) => {
      this.evaluarFortaleza(value || '');
    });
  }

  generarToken() {
    if (this.formUsuario.invalid) return;
    console.log('USERNAME ENVIADO:', this.formUsuario.value.username);
    this.passwordResetService
      .solicitarToken(this.formUsuario.value.username)
      .subscribe({
        next: (res) => {
          this.tokenGenerado = res; // token oculto
          this.mensaje =
            'Por favor ingresa tu nueva contraseña con minimo 8 digitos alfanumericos y mayusculas.';
          //this.mensaje = 'Si el usuario existe, se enviaron instrucciones';
          this.step = 2;
        },
        error: () => (this.mensaje = 'Usuario no encontrado'),
      });
  }

  resetearPassword() {
    this.ocultar();
    if (this.formReset.invalid) return;

    this.passwordResetService
      .resetearPassword(
        this.tokenGenerado, // token interno si formuloario
        this.formReset.value.nuevaPassword,
      )
      .subscribe({
        next: (res) => {
          this.mensaje = res || 'Contraseña actualizada correctamente';
          this.step = 3;
        },
        error: () => (this.mensaje = 'Token inválido o expirado'),
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

  evaluarFortaleza(password: string) {
    let fuerza = 0;

    if (password.length >= 8) fuerza++;
    if (/[a-z]/.test(password)) fuerza++;
    if (/[A-Z]/.test(password)) fuerza++;
    if (/\d/.test(password)) fuerza++;
    if (/[^a-zA-Z0-9]/.test(password)) fuerza++;

    if (fuerza <= 2) {
      this.passwordStrength = 'Débil';
      this.passwordStrengthClass = 'text-danger';
    } else if (fuerza <= 4) {
      this.passwordStrength = 'Media';
      this.passwordStrengthClass = 'text-warning';
    } else {
      this.passwordStrength = 'Fuerte';
      this.passwordStrengthClass = 'text-success';
    }
  }
}
