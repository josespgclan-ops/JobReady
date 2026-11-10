import { Component, Input, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

@Component({
  selector: 'app-publico-layout',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './publico-layout.html',
  styleUrl: './publico-layout.css',
})
export class PublicoLayout {
  @Input({ required: true }) titulo = '';
  /** Con sesión iniciada se muestra "Mi panel" en lugar de "Iniciar sesión" y "Registrarse". */
  protected readonly tieneSesion = inject(AuthService).estaAutenticado();
}
