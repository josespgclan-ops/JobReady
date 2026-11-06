import { Component, Input, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

@Component({
  selector: 'app-candidato-layout',
  imports: [RouterLink],
  templateUrl: './candidato-layout.html',
  styleUrl: './candidato-layout.css',
})
export class CandidatoLayoutComponente {
  @Input() active = '';
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  cerrarSesion(): void {
    this.auth.cerrarSesion();
    void this.router.navigateByUrl('/');
  }
}
