import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api-configuracion';
import { ApiRecurso, RegistroApi } from './api-recurso';
import { ResultadoSimulacion, SimulacionResumen } from '../modelos/api-modelos';

@Injectable({ providedIn: 'root' })
export class SimulacionesServicio {
  private readonly http = inject(HttpClient);
  private recurso<T extends RegistroApi>(coleccion: string, singular: string): ApiRecurso<T> {
    return new ApiRecurso<T>(this.http, coleccion, singular);
  }

  listarSimulaciones<T extends RegistroApi>(): Observable<T[]> { return this.recurso<T>('simulaciones', 'simulacion').listar(); }
  crearSimulacion<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.recurso<T>('simulaciones', 'simulacion').crear(datos); }
  actualizarSimulacion<T extends RegistroApi>(id: number, datos: Partial<T>): Observable<T> { return this.recurso<T>('simulaciones', 'simulacion').actualizar(id, datos); }
  eliminarSimulacion<T extends RegistroApi>(id: number): Observable<void> { return this.recurso<T>('simulaciones', 'simulacion').eliminar(id); }

  listarRespuestas<T extends RegistroApi>(): Observable<T[]> { return this.recurso<T>('respuestas-entrevista', 'respuesta-entrevista').listar(); }
  listarRespuestasDe<T extends RegistroApi>(idSimulacion: number): Observable<T[]> {
    return this.http.get<T[]>(`${API_BASE_URL}/respuestas-entrevista/simulacion/${idSimulacion}`);
  }
  crearRespuesta<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.recurso<T>('respuestas-entrevista', 'respuesta-entrevista').crear(datos); }
  actualizarRespuesta<T extends RegistroApi>(id: number, datos: Partial<T>): Observable<T> { return this.recurso<T>('respuestas-entrevista', 'respuesta-entrevista').actualizar(id, datos); }
  eliminarRespuesta<T extends RegistroApi>(id: number): Observable<void> { return this.recurso<T>('respuestas-entrevista', 'respuesta-entrevista').eliminar(id); }

  listarEvaluaciones<T extends RegistroApi>(): Observable<T[]> { return this.recurso<T>('evaluaciones-simulacion', 'evaluacion-simulacion').listar(); }
  crearEvaluacion<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.recurso<T>('evaluaciones-simulacion', 'evaluacion-simulacion').crear(datos); }
  actualizarEvaluacion<T extends RegistroApi>(id: number, datos: Partial<T>): Observable<T> { return this.recurso<T>('evaluaciones-simulacion', 'evaluacion-simulacion').actualizar(id, datos); }
  eliminarEvaluacion<T extends RegistroApi>(id: number): Observable<void> { return this.recurso<T>('evaluaciones-simulacion', 'evaluacion-simulacion').eliminar(id); }
  /** Simulaciones del candidato que inició sesión (el backend lo identifica por el token). */
  listarMisSimulaciones(): Observable<SimulacionResumen[]> {
    return this.http.get<SimulacionResumen[]>(`${API_BASE_URL}/simulaciones/mias`);
  }

  obtenerResultado(idSimulacion: number): Observable<ResultadoSimulacion> {
    return this.http.get<ResultadoSimulacion>(`${API_BASE_URL}/resultado/${idSimulacion}`);
  }

  listarSesiones<T extends RegistroApi>(): Observable<T[]> { return this.recurso<T>('sesiones-programadas', 'sesion-programada').listar(); }
  crearSesion<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.recurso<T>('sesiones-programadas', 'sesion-programada').crear(datos); }
  actualizarSesion<T extends RegistroApi>(id: number, datos: Partial<T>): Observable<T> { return this.recurso<T>('sesiones-programadas', 'sesion-programada').actualizar(id, datos); }
  cancelarSesion<T extends RegistroApi>(id: number): Observable<T> { return this.recurso<T>('sesiones-programadas', 'sesion-programada').accion(id, 'cancelar'); }

  listarArchivos<T extends RegistroApi>(): Observable<T[]> { return this.recurso<T>('archivos-simulacion', 'archivo-simulacion').listar(); }
  crearArchivo<T extends RegistroApi>(datos: Partial<T>): Observable<T> { return this.recurso<T>('archivos-simulacion', 'archivo-simulacion').crear(datos); }
  eliminarArchivo<T extends RegistroApi>(id: number): Observable<void> { return this.recurso<T>('archivos-simulacion', 'archivo-simulacion').eliminar(id); }
}
