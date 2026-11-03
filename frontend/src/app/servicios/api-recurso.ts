import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api-configuracion';

export type RegistroApi = Record<string, unknown>;

/** Operaciones REST compartidas; cada servicio concreto declara las rutas de su controlador. */
export class ApiRecurso<T extends RegistroApi> {
  constructor(
    protected readonly http: HttpClient,
    private readonly rutaColeccion: string,
    private readonly rutaRecurso: string,
  ) {}

  listar(): Observable<T[]> {
    return this.http.get<T[]>(`${API_BASE_URL}/${this.rutaColeccion}`);
  }

  crear(datos: Partial<T>): Observable<T> {
    return this.http.post<T>(`${API_BASE_URL}/${this.rutaRecurso}`, datos);
  }

  actualizar(id: number, datos: Partial<T>): Observable<T> {
    return this.http.put<T>(`${API_BASE_URL}/${this.rutaRecurso}/${id}`, datos);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${API_BASE_URL}/${this.rutaRecurso}/${id}`);
  }

  accion(id: number, nombre: string, datos: object = {}): Observable<T> {
    return this.http.patch<T>(`${API_BASE_URL}/${this.rutaRecurso}/${id}/${nombre}`, datos);
  }
}
