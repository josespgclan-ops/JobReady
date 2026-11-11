import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api-configuracion';
import { ApiRecurso, RegistroApi } from './api-recurso';

@Injectable({ providedIn: 'root' })
export class CuentasServicio {
  private readonly http = inject(HttpClient);
  private recurso<T extends RegistroApi>(coleccion: string, singular: string): ApiRecurso<T> {
    return new ApiRecurso<T>(this.http, coleccion, singular);
  }

  listarUsuarios<T extends RegistroApi>(): Observable<T[]> { return this.recurso<T>('usuarios', 'usuario').listar(); }
  crearUsuario<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.recurso<T>('usuarios', 'usuario').crear(datos); }
  actualizarUsuario<T extends RegistroApi>(id: number, datos: Partial<T>): Observable<T> { return this.recurso<T>('usuarios', 'usuario').actualizar(id, datos); }
  desactivarUsuario<T extends RegistroApi>(id: number): Observable<T> { return this.recurso<T>('usuarios', 'usuario').accion(id, 'desactivar'); }

  listarRoles<T extends RegistroApi>(): Observable<T[]> { return this.recurso<T>('roles', 'rol').listar(); }
  crearRol<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.recurso<T>('roles', 'rol').crear(datos); }
  actualizarRol<T extends RegistroApi>(id: number, datos: Partial<T>): Observable<T> { return this.recurso<T>('roles', 'rol').actualizar(id, datos); }

  listarSuscripciones<T extends RegistroApi>(): Observable<T[]> { return this.recurso<T>('suscripciones', 'suscripcion').listar(); }
  crearSuscripcion<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.recurso<T>('suscripciones', 'suscripcion').crear(datos); }
  actualizarSuscripcion<T extends RegistroApi>(id: number, datos: Partial<T>): Observable<T> { return this.recurso<T>('suscripciones', 'suscripcion').actualizar(id, datos); }
  cancelarSuscripcion<T extends RegistroApi>(id: number): Observable<T> { return this.recurso<T>('suscripciones', 'suscripcion').accion(id, 'cancelar'); }

  listarEncuestas<T extends RegistroApi>(): Observable<T[]> { return this.recurso<T>('encuestas-satisfaccion', 'encuesta-satisfaccion').listar(); }
  crearEncuesta<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.recurso<T>('encuestas-satisfaccion', 'encuesta-satisfaccion').crear(datos); }

  listarUsuariosCarreras<T extends RegistroApi>(): Observable<T[]> { return this.http.get<T[]>(`${API_BASE_URL}/usuarios-carreras`); }
  asociarCarrera<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.http.post<T>(`${API_BASE_URL}/usuario-carrera`, datos); }
  /** Quita una carrera de las elegidas por el candidato conectado. */
  quitarCarrera(idCarrera: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/usuario-carrera/${idCarrera}`);
  }

  /** Encuestas que ya envió el candidato conectado. */
  listarMisEncuestas<T extends RegistroApi>(): Observable<T[]> {
    return this.http.get<T[]>(`${API_BASE_URL}/encuestas-satisfaccion/mias`);
  }
}
