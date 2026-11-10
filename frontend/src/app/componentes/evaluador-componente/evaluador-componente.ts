import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { EvaluadorServicio } from '../../servicios/evaluador-servicio';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

/** Panel principal del evaluador: resumen de su trabajo y accesos a la cola y al historial. */
@Component({
  selector: 'app-evaluador-componente',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [PanelRolLayout, RouterLink, DecimalPipe],
  templateUrl: './evaluador-componente.html',
  styleUrls: ['../panel-rol-layout/panel-contenido.css'],
})
export class EvaluadorComponente implements OnInit {
  private readonly servicio = inject(EvaluadorServicio);

  protected cargando = true;
  protected error = '';
  protected pendientes = 0;
  protected realizadas = 0;
  protected promedio: number | null = null;

  ngOnInit(): void {
    forkJoin({
      pendientes: this.servicio.listarPendientes(),
      mias: this.servicio.listarMisEvaluaciones(),
    }).subscribe({
      next: ({ pendientes, mias }) => {
        this.pendientes = pendientes.length;
        this.realizadas = mias.length;
        this.promedio = mias.length ? mias.reduce((suma, e) => suma + e.puntajeGeneral, 0) / mias.length : null;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudo cargar tu resumen. Comprueba la conexión con el servidor.';
        this.cargando = false;
      },
    });
  }
}
