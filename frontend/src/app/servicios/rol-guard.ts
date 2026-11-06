import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth-service';

export const requiereSesion: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.estaAutenticado() ? true : inject(Router).createUrlTree(['/ingresar']);
};

export function requiereRol(rol: string): CanActivateFn {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    if (!auth.estaAutenticado()) return router.createUrlTree(['/ingresar']);
    return auth.obtenerRoles().includes(`ROLE_${rol}`)
      ? true
      : router.createUrlTree(['/acceso-denegado']);
  };
}
