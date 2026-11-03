import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

@Component({
  selector: 'app-ingresar-componente',
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

  ingresar(): void {
    if (this.cargando) return;
    this.cargando = true;
    this.error = '';
    this.auth.ingresar({ username: this.username, password: this.password }).subscribe({
      next: () => void this.router.navigateByUrl('/preguntas'),
      error: () => {
        this.error = 'No fue posible iniciar sesi?n. Comprueba los datos y que el backend est? disponible.';
        this.cargando = false;
      },
    });
  }
}
