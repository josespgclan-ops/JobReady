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
  /** Solo en evaluaciones anteriores a la rúbrica de tres porcentajes. */
  nivelComunicacion?: string | null;
  tiempoPromedioRespuesta: number;
  /** Seguridad al responder, de 0 a 1. */
  seguridadCalificacion: number;
  /** Comentario de evaluaciones antiguas; las nuevas usan oportunidades de mejora y recomendaciones. */
  resumenFeedback?: string | null;
  nombreEspecialista: string;
  /** Fecha en que el evaluador envió los resultados. */
  fechaEvaluacion?: string | null;
  /** PDF de retroalimentación generado por el sistema al enviar la evaluación. */
  idArchivoPdf?: number | null;
  nombreCandidato?: string | null;
  /** Claridad de respuestas (%). */
  claridadRespuestas?: number | null;
  /** Dominio del tema (%). */
  dominioTema?: number | null;
  oportunidadesMejora?: string | null;
  recomendaciones?: string | null;
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
  /** PDF de retroalimentación del evaluador. */
  idArchivoPdf?: number | null;
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
  nivelComunicacion?: string | null;
  tiempoPromedioRespuesta: number;
  seguridadCalificacion: number;
  claridadRespuestas?: number | null;
  dominioTema?: number | null;
  oportunidadesMejora?: string | null;
  recomendaciones?: string | null;
  feedbackResumen?: string | null;
  fechaEvaluacion?: string | null;
  especialista?: UsuarioResumen | null;
}

/** Rúbrica del evaluador: el puntaje general lo calcula el backend (promedio de los tres porcentajes). */
export interface EvaluacionRequest {
  idSimulacion: number;
  seguridadRespuestas: number;
  claridadRespuestas: number;
  dominioTema: number;
  oportunidadesMejora: string;
  recomendaciones: string;
}

export interface CarreraRequest {
  nombre: string;
  descripcion: string;
  activo: boolean;
}

/* ---------- Datos propios del candidato ---------- */
// Se declaran como `type` para que encajen en los métodos genéricos de ApiRecurso (RegistroApi).

/** Carrera elegida por el candidato (GET /api/usuarios-carreras). */
export type UsuarioCarrera = {
  id: { idUsuario: number; idCarrera: number };
  carrera: Carrera;
  fechaSeleccion?: string | null;
};

/** Sesión de práctica que organiza el candidato. */
export type SesionProgramada = {
  idSesion: number;
  carrera?: Carrera | null;
  fechaProgramada: string;
  horaProgramada: string;
  estado?: string | null;
  recordatorioEnviado?: boolean | null;
};

export type SesionProgramadaRequest = {
  idCarrera: number | null;
  fechaProgramada: string;
  horaProgramada: string;
};

export type CalificacionEncuesta = 'Excelente' | 'Bueno' | 'Regular' | 'Malo';

export type EncuestaSatisfaccion = {
  idEncuesta: number;
  satisfaccionGeneral: number;
  calificacionServicio: CalificacionEncuesta;
  calidadProducto: CalificacionEncuesta;
  comentarioAdicional?: string | null;
  fechaEnvio?: string | null;
};

export type EncuestaSatisfaccionRequest = {
  satisfaccionGeneral: number;
  calificacionServicio: CalificacionEncuesta;
  calidadProducto: CalificacionEncuesta;
  comentarioAdicional: string;
};
