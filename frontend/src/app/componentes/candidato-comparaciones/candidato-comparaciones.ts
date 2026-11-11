import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe, DecimalPipe, PercentPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { EnvioEntrevista, ResultadoSimulacion } from '../../modelos/api-modelos';
import { SimulacionesServicio } from '../../servicios/simulaciones-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-comparaciones',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterLink, FormsModule, DatePipe, DecimalPipe, PercentPipe, CandidatoLayoutComponente],
  templateUrl: './candidato-comparaciones.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-comparaciones.css'],
})
export class CandidatoComparacionesComponente implements OnInit {
  private readonly simulacionesServicio = inject(SimulacionesServicio);

  /** Solo las prácticas con evaluación publicada se pueden comparar. */
  protected evaluadas: EnvioEntrevista[] = [];
  protected cargando = true;
  protected error = '';

  protected idPrimera: number | null = null;
  protected idSegunda: number | null = null;
  protected primera: ResultadoSimulacion | null = null;
  protected segunda: ResultadoSimulacion | null = null;
  protected comparando = false;
  protected errorComparacion = '';

  ngOnInit(): void {
    this.simulacionesServicio.listarMisEnvios().subscribe({
      next: (envios) => {
        // Del más antiguo al más reciente para que la comparación natural sea "antes → después".
        this.evaluadas = envios.filter((e) => e.estadoRevision === 'REVISADA').reverse();
        if (this.evaluadas.length >= 2) {
          this.idPrimera = this.evaluadas[this.evaluadas.length - 2].idSimulacion;
          this.idSegunda = this.evaluadas[this.evaluadas.length - 1].idSimulacion;
          this.comparar();
        }
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar tus prácticas evaluadas.';
        this.cargando = false;
      },
    });
  }

  protected comparar(): void {
    this.primera = null;
    this.segunda = null;
    this.errorComparacion = '';
    if (this.idPrimera === null || this.idSegunda === null) return;
    if (this.idPrimera === this.idSegunda) {
      this.errorComparacion = 'Elige dos prácticas diferentes.';
      return;
    }
    this.comparando = true;
    forkJoin([
      this.simulacionesServicio.obtenerResultado(this.idPrimera),
      this.simulacionesServicio.obtenerResultado(this.idSegunda),
    ]).subscribe({
      next: ([primera, segunda]) => {
        this.primera = primera;
        this.segunda = segunda;
        this.comparando = false;
      },
      error: () => {
        this.errorComparacion = 'No se pudieron cargar los resultados de las prácticas.';
        this.comparando = false;
      },
    });
  }

  protected etiqueta(envio: EnvioEntrevista): string {
    const fecha = envio.fechaEnvio ? new Date(envio.fechaEnvio).toLocaleDateString('es') : '';
    return `${envio.nombreCarrera ?? 'Práctica'} · n.º ${envio.numeroSimulacion ?? '—'}${fecha ? ' · ' + fecha : ''}`;
  }

  protected diferencia(antes: number, despues: number, decimales = 0): string {
    const valor = Number((despues - antes).toFixed(decimales));
    if (valor === 0) return '=';
    return valor > 0 ? `+${valor}` : `${valor}`;
  }

  /** En el tiempo de respuesta, bajar es mejorar. */
  protected mejora(antes: number, despues: number, menorEsMejor = false): boolean | null {
    if (despues === antes) return null;
    return menorEsMejor ? despues < antes : despues > antes;
  }
}
