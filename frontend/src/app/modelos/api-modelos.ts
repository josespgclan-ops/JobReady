export interface AuthRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  jwt: string;
  roles: string[];
}

export interface RegistroCandidato {
  nombreCompleto: string;
  email: string;
  username: string;
  password: string;
  telefono: string;
}

export interface Carrera {
  idCarrera: number;
  nombre: string;
  descripcion?: string;
  activo?: boolean;
}

export interface PreguntaEntrevista {
  idPregunta: number;
  carrera?: Carrera;
  textoPregunta: string;
  categoria?: string;
  nivelDificultad?: string;
  activo: boolean;
  fechaCreacion?: string;
}

export interface PreguntaEntrevistaRequest {
  idCarrera: number;
  textoPregunta: string;
  categoria: string;
  nivelDificultad: string;
  activo: boolean;
}
