import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { API_BASE_URL } from './api-configuracion';
import { AuthRequest, AuthResponse, RegistroCandidato } from '../modelos/api-modelos';

const TOKEN_KEY = 'jobready.jwt';
const ROLES_KEY = 'jobready.roles';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  ingresar(datos: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_BASE_URL}/authenticate`, datos).pipe(
      tap((respuesta) => {
        sessionStorage.setItem(TOKEN_KEY, respuesta.jwt);
        sessionStorage.setItem(ROLES_KEY, JSON.stringify(respuesta.roles));
      }),
    );
  }

  registrar(datos: RegistroCandidato): Observable<unknown> {
    return this.http.post(`${API_BASE_URL}/registro`, datos);
  }

  cerrarSesion(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(ROLES_KEY);
  }

  estaAutenticado(): boolean {
    return !!sessionStorage.getItem(TOKEN_KEY);
  }

  obtenerRoles(): string[] {
    try {
      return JSON.parse(sessionStorage.getItem(ROLES_KEY) ?? '[]') as string[];
    } catch {
      return [];
    }
  }
}
