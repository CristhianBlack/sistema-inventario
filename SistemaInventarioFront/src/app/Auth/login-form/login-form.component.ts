import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AutenticationService } from 'src/app/Servicios/autentication.service';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent {

  form: FormGroup;
  error = '';

  constructor(
    private fb: FormBuilder,
    private authService: AutenticationService,
    private router: Router,
    private toastr : ToastrService
  ) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  login() {
    if (this.form.invalid) return;

    const { username, password } = this.form.value;

    this.authService.login(username, password).subscribe({
      next: (data) => {
        localStorage.setItem('token', data.token);
        localStorage.setItem('rol', data.rol);
        console.log("token ", data.token);
        console.log("userName ", username);
        console.log("rol ", data.rol);
        if(data.debeCambiarPassword){
          this.router.navigate(['/cambiar-password']);
          //this.authService.logout();
          //this.router.navigate(['/login']);
        }else{
          this.router.navigate(['/dashboard']);
        }
      },
      error: () => {
        this.error = 'Usuario o contraseña incorrectos';
        this.toastr.error("Usuario o contraseña incorrectos")
      }
    });
  }

}
