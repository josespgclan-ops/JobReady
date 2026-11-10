import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { AdminServicio } from '../../servicios/admin-servicio';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

/** US18: métricas generales de la plataforma para el administrador. */
@Component({
  selector: 'app-administracion-componente',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [PanelRolLayout, RouterLink, DecimalPipe],
  templateUrl: './administracion-componente.html',
  styleUrls: ['../panel-rol-layout/panel-contenido.css'],
})
export class AdministracionComponente implements OnInit {
  private readonly servicio = inject(AdminServicio);

  protected cargando = true;
  protected error = '';
  protected metricas = {
    candidatos: 0, evaluadores: 0, suspendidos: 0,
    simulaciones: 0, evaluaciones: 0, pendientes: 0,
    preguntasActivas: 0, carrerasActivas: 0, promedio: null as number | null,
  };

  ngOnInit(): void {
    forkJoin({
      usuarios: this.servicio.listarUsuarios(),
      simulaciones: this.servicio.listarSimulaciones(),
      evaluaciones: this.servicio.listarEvaluaciones(),
      pendientes: this.servicio.listarPendientes(),
      preguntas: this.servicio.listarPreguntas(),
      carreras: this.servicio.listarCarreras(),
    }).subscribe({
      next: (d) => {
        const rol = (nombre: string) => d.usuarios.filter((u) => u.rol?.nombre === nombre).length;
        this.metricas = {
          candidatos: rol('CANDIDATO'),
          evaluadores: rol('EVALUADOR'),
          suspendidos: d.usuarios.filter((u) => u.estado === 'inactivo').length,
          simulaciones: d.simulaciones.length,
          evaluaciones: d.evaluaciones.length,
          pendientes: d.pendientes.length,
          preguntasActivas: d.preguntas.filter((p) => p.activo !== false).length,
          carrerasActivas: d.carreras.filter((c) => c.activo !== false).length,
          promedio: d.evaluaciones.length
            ? d.evaluaciones.reduce((s, e) => s + e.puntajeGeneral, 0) / d.evaluaciones.length : null,
        };
        this.cargando = false;
      },
      error: () => { this.error = 'No se pudieron cargar las métricas.'; this.cargando = false; },
    });
  }
}
