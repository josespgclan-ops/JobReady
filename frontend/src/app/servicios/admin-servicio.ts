import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api-configuracion';
import {
  Carrera,
  CarreraRequest,
  EvaluacionSimulacion,
  PreguntaEntrevista,
  PreguntaEntrevistaRequest,
  SimulacionDetalle,
  UsuarioResumen,
} from '../modelos/api-modelos';

/** Operaciones del rol ADMIN (EP01 y EP05: catálogos, usuarios y métricas). */
@Injectable({ providedIn: 'root' })
export class AdminServicio {
  private readonly http = inject(HttpClient);

  // ---- Usuarios (US12) ----
  listarUsuarios(): Observable<UsuarioResumen[]> {
    return this.http.get<UsuarioResumen[]>(`${API_BASE_URL}/usuarios`);
  }
  suspenderUsuario(id: number): Observable<UsuarioResumen> {
    return this.http.patch<UsuarioResumen>(`${API_BASE_URL}/usuario/${id}/desactivar`, {});
  }
  reactivarUsuario(id: number): Observable<UsuarioResumen> {
    return this.http.patch<UsuarioResumen>(`${API_BASE_URL}/usuario/${id}/activar`, {});
  }

  // ---- Banco de preguntas (US02, US23) ----
  listarPreguntas(): Observable<PreguntaEntrevista[]> {
    return this.http.get<PreguntaEntrevista[]>(`${API_BASE_URL}/preguntas-entrevista`);
  }
  crearPregunta(datos: PreguntaEntrevistaRequest): Observable<PreguntaEntrevista> {
    return this.http.post<PreguntaEntrevista>(`${API_BASE_URL}/pregunta-entrevista`, datos);
  }
  actualizarPregunta(id: number, datos: PreguntaEntrevistaRequest): Observable<PreguntaEntrevista> {
    return this.http.put<PreguntaEntrevista>(`${API_BASE_URL}/pregunta-entrevista/${id}`, datos);
  }
  desactivarPregunta(id: number): Observable<PreguntaEntrevista> {
    return this.http.patch<PreguntaEntrevista>(`${API_BASE_URL}/pregunta-entrevista/${id}/desactivar`, {});
  }

  // ---- Carreras (US22) ----
  listarCarreras(): Observable<Carrera[]> {
    return this.http.get<Carrera[]>(`${API_BASE_URL}/carreras`);
  }
  crearCarrera(datos: CarreraRequest): Observable<Carrera> {
    return this.http.post<Carrera>(`${API_BASE_URL}/carrera`, datos);
  }
  actualizarCarrera(id: number, datos: CarreraRequest): Observable<Carrera> {
    return this.http.put<Carrera>(`${API_BASE_URL}/carrera/${id}`, datos);
  }

  // ---- Métricas y reportes (US18, US24) ----
  listarSimulaciones(): Observable<SimulacionDetalle[]> {
    return this.http.get<SimulacionDetalle[]>(`${API_BASE_URL}/simulaciones`);
  }
  listarPendientes(): Observable<SimulacionDetalle[]> {
    return this.http.get<SimulacionDetalle[]>(`${API_BASE_URL}/simulaciones/pendientes`);
  }
  /** Elimina una entrevista pendiente con su video: deja de verse en el historial del candidato y en la cola del evaluador. */
  eliminarEnvioPendiente(idSimulacion: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/simulacion/${idSimulacion}/envio`);
  }
  listarEvaluaciones(): Observable<EvaluacionSimulacion[]> {
    return this.http.get<EvaluacionSimulacion[]>(`${API_BASE_URL}/evaluaciones-simulacion`);
  }
}
