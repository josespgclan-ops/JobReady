import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { PercentPipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { EvaluacionSimulacion } from '../../modelos/api-modelos';
import { EvaluadorServicio } from '../../servicios/evaluador-servicio';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

/** US21: historial de evaluaciones registradas por el evaluador conectado. */
@Component({
  selector: 'app-evaluador-evaluaciones',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [PanelRolLayout, RouterLink, PercentPipe],
  templateUrl: './evaluador-evaluaciones.html',
  styleUrls: ['../panel-rol-layout/panel-contenido.css'],
})
export class EvaluadorEvaluaciones implements OnInit {
  private readonly servicio = inject(EvaluadorServicio);
  private readonly route = inject(ActivatedRoute);

  protected evaluaciones: EvaluacionSimulacion[] = [];
  protected cargando = true;
  protected error = '';
  /** Mensaje de éxito cuando se llega aquí después de registrar una evaluación. */
  protected recienRegistrada = this.route.snapshot.queryParamMap.get('registrada');

  ngOnInit(): void {
    this.servicio.listarMisEvaluaciones().subscribe({
      next: (lista) => { this.evaluaciones = lista; this.cargando = false; },
      error: () => { this.error = 'No se pudo cargar tu historial de evaluaciones.'; this.cargando = false; },
    });
  }
}
