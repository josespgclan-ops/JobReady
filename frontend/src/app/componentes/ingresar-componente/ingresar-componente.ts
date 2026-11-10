import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

@Component({
  selector: 'app-ingresar-componente',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, RouterLink],
  templateUrl: './ingresar-componente.html',
  styleUrl: './ingresar-componente.css',
})
export class IngresarComponente {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  protected username = '';
  protected password = '';
  protected cargando = false;
  protected error = '';
  protected mostrarPassword = false;

  ingresar(): void {
    if (this.cargando || !this.username.trim() || !this.password) return;
    this.cargando = true;
    this.error = '';
    this.auth.ingresar({ username: this.username.trim(), password: this.password }).subscribe({
      next: (respuesta) => {
        const roles = respuesta.roles;
        const destino = roles.includes('ROLE_ADMIN') ? '/administracion'
          : roles.includes('ROLE_EVALUADOR') ? '/evaluador'
          : roles.includes('ROLE_CANDIDATO') ? '/panel' : '/acceso-denegado';
        void this.router.navigateByUrl(destino);
      },
      error: () => {
        this.error = 'No fue posible iniciar sesión. Comprueba tus datos y que el servidor esté disponible.';
        this.cargando = false;
      },
    });
  }
}
