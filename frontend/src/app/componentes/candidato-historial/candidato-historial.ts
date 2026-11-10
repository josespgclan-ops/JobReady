import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SimulacionResumen } from '../../modelos/api-modelos';
import { SimulacionesServicio } from '../../servicios/simulaciones-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-historial',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterLink, DatePipe, CandidatoLayoutComponente],
  templateUrl: './candidato-historial.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-historial.css'],
})
export class CandidatoHistorialComponente implements OnInit {
  private readonly simulacionesServicio = inject(SimulacionesServicio);

  protected simulaciones: SimulacionResumen[] = [];
  protected cargando = true;
  protected error = '';

  ngOnInit(): void {
    this.simulacionesServicio.listarMisSimulaciones().subscribe({
      next: (simulaciones) => {
        this.simulaciones = simulaciones;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudo cargar tu historial. Comprueba la conexión con el servidor.';
        this.cargando = false;
      },
    });
  }

  /** Solo una simulación terminada puede tener informe del evaluador. */
  protected estaFinalizada(simulacion: SimulacionResumen): boolean {
    return (simulacion.estado ?? '').toLowerCase() === 'finalizada';
  }

  protected textoEstado(simulacion: SimulacionResumen): string {
    const estado = (simulacion.estado ?? '').toLowerCase();
    if (estado === 'finalizada') return 'Finalizada';
    if (estado === 'iniciada') return 'En curso';
    return simulacion.estado || 'Sin estado';
  }

  protected duracion(simulacion: SimulacionResumen): string {
    if (!simulacion.duracionSegundos) return '—';
    const minutos = Math.round(simulacion.duracionSegundos / 60);
    return `${minutos} min`;
  }
}
