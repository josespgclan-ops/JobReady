import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { SimulacionDetalle } from '../../modelos/api-modelos';
import { AdminServicio } from '../../servicios/admin-servicio';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

/** US24: reporte de prácticas enviadas que todavía no fueron evaluadas (cuellos de botella). */
@Component({
  selector: 'app-admin-pendientes',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [PanelRolLayout, DatePipe],
  templateUrl: './admin-pendientes.html',
  styleUrls: ['../panel-rol-layout/panel-contenido.css'],
})
export class AdminPendientes implements OnInit {
  private readonly servicio = inject(AdminServicio);

  protected pendientes: SimulacionDetalle[] = [];
  protected cargando = true;
  protected error = '';
  protected mensaje = '';
  /** Id de la entrevista que se está eliminando, para bloquear su botón mientras responde la API. */
  protected eliminando: number | null = null;
  private readonly ahora = Date.now();

  protected eliminar(s: SimulacionDetalle): void {
    if (this.eliminando !== null) return;
    const candidato = s.usuario?.nombreCompleto ?? s.usuario?.username ?? 'el candidato';
    if (!confirm(`¿Eliminar la entrevista de ${candidato} (${s.carrera?.nombre ?? 'sin carrera'}) y su video? `
      + 'Se borrará del historial del candidato y de la cola del evaluador. Esta acción no se puede deshacer.')) return;
    this.eliminando = s.idSimulacion;
    this.error = '';
    this.mensaje = '';
    this.servicio.eliminarEnvioPendiente(s.idSimulacion).subscribe({
      next: () => {
        this.pendientes = this.pendientes.filter((p) => p.idSimulacion !== s.idSimulacion);
        this.eliminando = null;
        this.mensaje = `Se eliminó la entrevista de ${candidato}.`;
      },
      error: (respuesta: HttpErrorResponse) => {
        this.eliminando = null;
        if (respuesta.status === 404) {
          // Ya no existe en la BD: se quita de la lista para no mostrar datos obsoletos.
          this.pendientes = this.pendientes.filter((p) => p.idSimulacion !== s.idSimulacion);
        }
        this.error = respuesta.error?.message || 'No se pudo eliminar la entrevista.';
      },
    });
  }

  ngOnInit(): void {
    this.servicio.listarPendientes().subscribe({
      next: (lista) => { this.pendientes = lista; this.cargando = false; },
      error: () => { this.error = 'No se pudo generar el reporte.'; this.cargando = false; },
    });
  }

  /** Horas que lleva la práctica esperando evaluación. */
  protected horasEsperando(s: SimulacionDetalle): number {
    if (!s.fechaFin) return 0;
    return Math.max(0, Math.floor((this.ahora - new Date(s.fechaFin).getTime()) / 3_600_000));
  }

  /** La hipótesis del proyecto pide evaluar en menos de 48 horas. */
  protected retrasada(s: SimulacionDetalle): boolean {
    return this.horasEsperando(s) >= 48;
  }
}
