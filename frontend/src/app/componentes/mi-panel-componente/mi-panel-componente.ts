import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

@Component({ selector: 'app-mi-panel-componente', template: '' })
export class MiPanelComponente implements OnInit {
  private readonly router = inject(Router);
  private readonly auth = inject(AuthService);

  ngOnInit(): void {
    const roles = this.auth.obtenerRoles();
    const destino = roles.includes('ROLE_ADMIN') ? '/administracion'
      : roles.includes('ROLE_EVALUADOR') ? '/evaluador'
      : roles.includes('ROLE_CANDIDATO') ? '/panel' : '/acceso-denegado';
    void this.router.navigateByUrl(destino);
  }
}
