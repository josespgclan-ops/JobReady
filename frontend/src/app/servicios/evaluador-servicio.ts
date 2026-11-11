import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api-configuracion';
import {
  EvaluacionRequest,
  EvaluacionSimulacion,
  RespuestaEntrevista,
  SimulacionDetalle,
} from '../modelos/api-modelos';

/** Operaciones del rol EVALUADOR (EP03: cola, revisión y evaluación de prácticas). */
@Injectable({ providedIn: 'root' })
export class EvaluadorServicio {
  private readonly http = inject(HttpClient);

  /** US15: prácticas terminadas que todavía no tienen evaluación. */
  listarPendientes(): Observable<SimulacionDetalle[]> {
    return this.http.get<SimulacionDetalle[]>(`${API_BASE_URL}/simulaciones/pendientes`);
  }

  /** Todas las simulaciones (para mostrar la información de la práctica que se evalúa). */
  listarSimulaciones(): Observable<SimulacionDetalle[]> {
    return this.http.get<SimulacionDetalle[]>(`${API_BASE_URL}/simulaciones`);
  }

  /** US05: respuestas que el candidato dio en una práctica. */
  listarRespuestas(idSimulacion: number): Observable<RespuestaEntrevista[]> {
    return this.http.get<RespuestaEntrevista[]>(`${API_BASE_URL}/respuestas-entrevista/simulacion/${idSimulacion}`);
  }

  /** US06: registra la evaluación; el backend asigna como especialista al evaluador conectado. */
  registrarEvaluacion(datos: EvaluacionRequest, pdf?: File | null): Observable<EvaluacionSimulacion> {
    if (!pdf) return this.http.post<EvaluacionSimulacion>(`${API_BASE_URL}/evaluacion-simulacion`, datos);
    // Con PDF de indicaciones se envía multipart: la evaluación como JSON y el archivo aparte.
    const formulario = new FormData();
    formulario.append('evaluacion', new Blob([JSON.stringify(datos)], { type: 'application/json' }));
    formulario.append('pdf', pdf, pdf.name);
    return this.http.post<EvaluacionSimulacion>(`${API_BASE_URL}/evaluacion-simulacion`, formulario);
  }

  /** US21: evaluaciones hechas por el evaluador conectado. */
  listarMisEvaluaciones(): Observable<EvaluacionSimulacion[]> {
    return this.http.get<EvaluacionSimulacion[]>(`${API_BASE_URL}/evaluaciones-simulacion/mias`);
  }
}
