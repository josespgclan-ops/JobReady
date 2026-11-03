import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

@Component({
  selector: 'app-registro-componente',
  imports: [FormsModule, RouterLink],
  templateUrl: './registro-componente.html',
  styleUrl: './registro-componente.css',
})
export class RegistroComponente {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  protected nombreCompleto = '';
  protected email = '';
  protected username = '';
  protected password = '';
  protected telefono = '';
  protected cargando = false;
  protected mensaje = '';
  protected error = '';

  registrar(): void {
    if (this.cargando) return;
    this.cargando = true;
    this.error = '';
    this.auth.registrar({
      nombreCompleto: this.nombreCompleto,
      email: this.email,
      username: this.username,
      password: this.password,
      telefono: this.telefono,
    }).subscribe({
      next: () => {
        this.mensaje = 'Cuenta creada. Ahora puedes iniciar sesi?n.';
        this.cargando = false;
        void this.router.navigateByUrl('/ingresar');
      },
      error: () => {
        this.error = 'No se pudo crear la cuenta. Comprueba los datos y que el backend est? disponible.';
        this.cargando = false;
      },
    });
  }
}
