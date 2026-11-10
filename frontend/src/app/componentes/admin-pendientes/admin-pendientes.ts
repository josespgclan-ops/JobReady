import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
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
  private readonly ahora = Date.now();

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
