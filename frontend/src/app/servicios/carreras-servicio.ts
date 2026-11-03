import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from './api-configuracion';
import { Carrera } from '../modelos/api-modelos';

@Injectable({ providedIn: 'root' })
export class CarrerasServicio {
  private readonly http = inject(HttpClient);

  listar(): Observable<Carrera[]> {
    return this.http.get<Carrera[]>(`${API_BASE_URL}/carreras`);
  }
}
