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

export interface SimulacionResumen {
  idSimulacion: number;
  carrera?: Carrera;
  numeroSimulacion?: number;
  fechaInicio?: string;
  fechaFin?: string | null;
  duracionSegundos?: number | null;
  estado?: string;
}

export interface ResultadoSimulacion {
  idSimulacion: number;
  puntajeGeneral: number;
  nivelComunicacion: string;
  tiempoPromedioRespuesta: number;
  seguridadCalificacion: number;
  resumenFeedback: string;
  nombreEspecialista: string;
  /** Fecha en que el evaluador envió los resultados. */
  fechaEvaluacion?: string | null;
  /** PDF con indicaciones de evaluación; null si no se adjuntó. */
  idArchivoPdf?: number | null;
}

/** Cuenta autenticada (GET /api/usuario/actual). */
export interface UsuarioActual {
  username: string;
  nombreCompleto: string;
}

export type EstadoRevision = 'PENDIENTE' | 'REVISADA' | 'EN_CURSO';

/** Fila del historial del candidato (GET /api/simulaciones/mias/envios). */
export interface EnvioEntrevista {
  idSimulacion: number;
  idCarrera?: number | null;
  nombreCarrera?: string | null;
  numeroSimulacion?: number | null;
  fechaEnvio?: string | null;
  duracionSegundos?: number | null;
  estado?: string | null;
  estadoRevision: EstadoRevision;
  idArchivoVideo?: number | null;
  fechaEvaluacion?: string | null;
}

/** Metadatos de un archivo de práctica: video del candidato o PDF del evaluador. */
export interface ArchivoSimulacion {
  idArchivo: number;
  tipoArchivo: 'video' | 'pdf_evaluacion' | string;
  urlAlmacenamiento: string;
  pesoMb?: number | null;
  fechaCreacion?: string | null;
}

/* ---------- Evaluador y administrador ---------- */

export interface UsuarioResumen {
  idUsuario: number;
  nombreCompleto: string;
  email?: string;
  username: string;
  telefono?: string | null;
  estado?: string;
  fechaRegistro?: string;
  rol?: { idRol: number; nombre: string };
}

export interface SimulacionDetalle extends SimulacionResumen {
  usuario?: UsuarioResumen;
}

export interface RespuestaEntrevista {
  idRespuesta: number;
  pregunta?: PreguntaEntrevista;
  textoRespuesta?: string | null;
  duracionSegundos?: number | null;
  fechaRespuesta?: string;
  estado?: string;
}

export interface EvaluacionSimulacion {
  idEvaluacion: number;
  simulacion?: SimulacionDetalle;
  puntajeGeneral: number;
  nivelComunicacion: string;
  tiempoPromedioRespuesta: number;
  seguridadCalificacion: number;
  feedbackResumen?: string;
  especialista?: UsuarioResumen | null;
}

export interface EvaluacionRequest {
  idSimulacion: number;
  puntajeGeneral: number;
  nivelComunicacion: string;
  tiempoPromedioRespuesta: number;
  seguridadCalificacion: number;
  feedbackResumen: string;
}

export interface CarreraRequest {
  nombre: string;
  descripcion: string;
  activo: boolean;
}
