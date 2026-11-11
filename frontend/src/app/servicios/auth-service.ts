import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, tap } from 'rxjs';
import { API_BASE_URL } from './api-configuracion';
import { AuthRequest, AuthResponse, RegistroCandidato, UsuarioActual } from '../modelos/api-modelos';

const TOKEN_KEY = 'jobready.jwt';
const ROLES_KEY = 'jobready.roles';
const USUARIO_KEY = 'jobready.usuario';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  ingresar(datos: AuthRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API_BASE_URL}/authenticate`, datos).pipe(
      tap((respuesta) => {
        sessionStorage.setItem(TOKEN_KEY, respuesta.jwt);
        sessionStorage.setItem(ROLES_KEY, JSON.stringify(respuesta.roles));
        sessionStorage.removeItem(USUARIO_KEY);
      }),
    );
  }

  registrar(datos: RegistroCandidato): Observable<unknown> {
    return this.http.post(`${API_BASE_URL}/registro`, datos);
  }

  cerrarSesion(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(ROLES_KEY);
    sessionStorage.removeItem(USUARIO_KEY);
  }

  /** Cuenta del candidato conectado; se guarda en la sesión para no consultarla en cada pantalla. */
  obtenerUsuarioActual(): Observable<UsuarioActual> {
    const guardado = sessionStorage.getItem(USUARIO_KEY);
    if (guardado) {
      try {
        return of(JSON.parse(guardado) as UsuarioActual);
      } catch {
        sessionStorage.removeItem(USUARIO_KEY);
      }
    }
    return this.http.get<UsuarioActual>(`${API_BASE_URL}/usuario/actual`).pipe(
      tap((usuario) => sessionStorage.setItem(USUARIO_KEY, JSON.stringify(usuario))),
    );
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
