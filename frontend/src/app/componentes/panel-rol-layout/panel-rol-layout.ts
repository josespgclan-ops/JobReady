import { Component, Input, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

@Component({
  selector: 'app-panel-rol-layout',
  imports: [RouterLink],
  templateUrl: './panel-rol-layout.html',
  styleUrl: './panel-rol-layout.css',
})
export class PanelRolLayout {
  @Input({ required: true }) rol = '';
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  cerrarSesion(): void {
    this.auth.cerrarSesion();
    void this.router.navigateByUrl('/ingresar');
  }
}
