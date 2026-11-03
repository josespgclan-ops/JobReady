import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api-configuracion';
import { ApiRecurso, RegistroApi } from './api-recurso';

@Injectable({ providedIn: 'root' })
export class CatalogosServicio {
  private readonly http = inject(HttpClient);

  listarPlanes<T extends RegistroApi>(): Observable<T[]> { return this.http.get<T[]>(`${API_BASE_URL}/planes`); }
  crearPlan<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.http.post<T>(`${API_BASE_URL}/plan`, datos); }
  actualizarPlan<T extends RegistroApi>(id: number, datos: Partial<T>): Observable<T> { return this.http.put<T>(`${API_BASE_URL}/plan/${id}`, datos); }
  crearCarrera<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.http.post<T>(`${API_BASE_URL}/carrera`, datos); }
  actualizarCarrera<T extends RegistroApi>(id: number, datos: Partial<T>): Observable<T> { return this.http.put<T>(`${API_BASE_URL}/carrera/${id}`, datos); }
  desactivarCarrera<T extends RegistroApi>(id: number): Observable<T> { return this.http.patch<T>(`${API_BASE_URL}/carrera/${id}/desactivar`, {}); }
}
