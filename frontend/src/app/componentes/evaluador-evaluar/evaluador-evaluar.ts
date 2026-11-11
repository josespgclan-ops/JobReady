import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { RespuestaEntrevista, SimulacionDetalle } from '../../modelos/api-modelos';
import { EvaluadorServicio } from '../../servicios/evaluador-servicio';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

/**
 * US05 y US06: el evaluador lee las respuestas de una práctica y registra la rúbrica.
 * El tiempo promedio se calcula a partir de la duración de las respuestas.
 */
@Component({
  selector: 'app-evaluador-evaluar',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [PanelRolLayout, RouterLink, FormsModule, DatePipe],
  templateUrl: './evaluador-evaluar.html',
  styleUrls: ['../panel-rol-layout/panel-contenido.css', './evaluador-evaluar.css'],
})
export class EvaluadorEvaluar implements OnInit {
  private readonly servicio = inject(EvaluadorServicio);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly niveles = ['Básico', 'Intermedio', 'Avanzado'];

  protected idSimulacion = 0;
  protected simulacion: SimulacionDetalle | null = null;
  protected respuestas: RespuestaEntrevista[] = [];
  protected cargando = true;
  protected enviando = false;
  protected error = '';
  protected intentoEnviar = false;

  // PDF opcional con indicaciones para el candidato.
  protected pdf: File | null = null;
  protected errorPdf = '';

  // Rúbrica
  protected puntaje: number | null = null;
  protected nivel = '';
  protected seguridad: number | null = null;
  protected feedback = '';

  ngOnInit(): void {
    this.idSimulacion = Number(this.route.snapshot.paramMap.get('id'));
    forkJoin({
      respuestas: this.servicio.listarRespuestas(this.idSimulacion),
      simulaciones: this.servicio.listarSimulaciones(),
    }).subscribe({
      next: ({ respuestas, simulaciones }) => {
        this.respuestas = respuestas;
        this.simulacion = simulaciones.find((s) => s.idSimulacion === this.idSimulacion) ?? null;
        this.cargando = false;
      },
      error: () => { this.error = 'No se pudo cargar la práctica.'; this.cargando = false; },
    });
  }

  protected seleccionarPdf(evento: Event): void {
    const entrada = evento.target as HTMLInputElement;
    const archivo = entrada.files?.[0] ?? null;
    this.errorPdf = '';
    if (archivo && (archivo.type !== 'application/pdf' && !archivo.name.toLowerCase().endsWith('.pdf'))) {
      this.errorPdf = 'Selecciona un archivo PDF.';
      entrada.value = '';
      this.pdf = null;
      return;
    }
    if (archivo && archivo.size > 10 * 1024 * 1024) {
      this.errorPdf = 'El PDF no puede superar los 10 MB.';
      entrada.value = '';
      this.pdf = null;
      return;
    }
    this.pdf = archivo;
  }

  /** Promedio en segundos de las respuestas (se envía en la evaluación). */
  protected get tiempoPromedio(): number {
    const tiempos = this.respuestas.map((r) => r.duracionSegundos ?? 0).filter((t) => t > 0);
    return tiempos.length ? Math.round((tiempos.reduce((a, b) => a + b, 0) / tiempos.length) * 10) / 10 : 0;
  }

  protected get puntajeInvalido(): boolean {
    return this.puntaje === null || this.puntaje < 0 || this.puntaje > 100;
  }
  protected get seguridadInvalida(): boolean {
    return this.seguridad === null || this.seguridad < 0 || this.seguridad > 100;
  }
  protected get feedbackInvalido(): boolean {
    return this.feedback.trim().length < 20;
  }

  protected enviar(): void {
    this.intentoEnviar = true;
    this.error = '';
    // US06 escenario 2: si falta un criterio, no se envía y se resalta.
    if (this.puntajeInvalido || this.seguridadInvalida || !this.nivel || this.feedbackInvalido) {
      this.error = 'Completa todos los criterios de la rúbrica antes de enviar.';
      return;
    }
    this.enviando = true;
    this.servicio.registrarEvaluacion({
      idSimulacion: this.idSimulacion,
      puntajeGeneral: Math.round(this.puntaje!),
      nivelComunicacion: this.nivel,
      tiempoPromedioRespuesta: this.tiempoPromedio,
      seguridadCalificacion: Math.round(this.seguridad!) / 100,
      feedbackResumen: this.feedback.trim(),
    }, this.pdf).subscribe({
      next: () => void this.router.navigate(['/evaluador/evaluaciones'], { queryParams: { registrada: this.idSimulacion } }),
      error: (respuesta: HttpErrorResponse) => {
        this.enviando = false;
        this.error = respuesta.status === 409
          ? 'Esta práctica ya fue evaluada por otro especialista.'
          : respuesta.error?.message || 'No se pudo registrar la evaluación. Inténtalo otra vez.';
      },
    });
  }
}
