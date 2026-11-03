import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api-configuracion';
import { PreguntaEntrevista, PreguntaEntrevistaRequest } from '../modelos/api-modelos';

@Injectable({ providedIn: 'root' })
export class PreguntasServicio {
  private readonly http = inject(HttpClient);
  private readonly recurso = `${API_BASE_URL}/pregunta-entrevista`;

  listar(): Observable<PreguntaEntrevista[]> {
    return this.http.get<PreguntaEntrevista[]>(`${API_BASE_URL}/preguntas-entrevista`);
  }

  listarPorCarrera(idCarrera: number): Observable<PreguntaEntrevista[]> {
    return this.http.get<PreguntaEntrevista[]>(`${API_BASE_URL}/preguntas-entrevista/carrera/${idCarrera}`);
  }

  crear(pregunta: PreguntaEntrevistaRequest): Observable<PreguntaEntrevista> {
    return this.http.post<PreguntaEntrevista>(this.recurso, pregunta);
  }

  actualizar(id: number, pregunta: PreguntaEntrevistaRequest): Observable<PreguntaEntrevista> {
    return this.http.put<PreguntaEntrevista>(`${this.recurso}/${id}`, pregunta);
  }

  desactivar(id: number): Observable<PreguntaEntrevista> {
    return this.http.patch<PreguntaEntrevista>(`${this.recurso}/${id}/desactivar`, {});
  }
}
