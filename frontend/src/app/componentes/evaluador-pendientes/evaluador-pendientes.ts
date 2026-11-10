import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SimulacionDetalle } from '../../modelos/api-modelos';
import { EvaluadorServicio } from '../../servicios/evaluador-servicio';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

/** US15: cola de prácticas terminadas que esperan evaluación (la más antigua primero). */
@Component({
  selector: 'app-evaluador-pendientes',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [PanelRolLayout, RouterLink, DatePipe],
  templateUrl: './evaluador-pendientes.html',
  styleUrls: ['../panel-rol-layout/panel-contenido.css'],
})
export class EvaluadorPendientes implements OnInit {
  private readonly servicio = inject(EvaluadorServicio);

  protected pendientes: SimulacionDetalle[] = [];
  protected cargando = true;
  protected error = '';

  ngOnInit(): void {
    this.servicio.listarPendientes().subscribe({
      next: (lista) => { this.pendientes = lista; this.cargando = false; },
      error: () => { this.error = 'No se pudo cargar la cola de prácticas.'; this.cargando = false; },
    });
  }
}
