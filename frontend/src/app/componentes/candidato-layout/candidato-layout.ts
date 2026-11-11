import { Component, Input, OnInit, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

@Component({
  selector: 'app-candidato-layout',
  imports: [RouterLink],
  templateUrl: './candidato-layout.html',
  styleUrl: './candidato-layout.css',
})
export class CandidatoLayoutComponente implements OnInit {
  @Input() active = '';
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  /** Nombre de la cuenta conectada; es una señal porque el layout usa detección OnPush. */
  protected readonly nombreUsuario = signal('');

  ngOnInit(): void {
    this.auth.obtenerUsuarioActual().subscribe({
      next: (usuario) => this.nombreUsuario.set(usuario.nombreCompleto || usuario.username),
      error: () => this.nombreUsuario.set(''),
    });
  }

  cerrarSesion(): void {
    this.auth.cerrarSesion();
    void this.router.navigateByUrl('/');
  }
}
