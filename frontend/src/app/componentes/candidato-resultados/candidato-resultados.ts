import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe, DecimalPipe, PercentPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ResultadoSimulacion } from '../../modelos/api-modelos';
import { SimulacionesServicio } from '../../servicios/simulaciones-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-resultados',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterLink, DatePipe, DecimalPipe, PercentPipe, CandidatoLayoutComponente],
  templateUrl: './candidato-resultados.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-resultados.css'],
})
export class CandidatoResultadosComponente implements OnInit {
  private readonly simulacionesServicio = inject(SimulacionesServicio);
  private readonly route = inject(ActivatedRoute);

  protected resultado: ResultadoSimulacion | null = null;
  protected cargando = false;
  protected error = '';

  ngOnInit(): void {
    const idSimulacion = this.route.snapshot.queryParamMap.get('idSimulacion');

    if (idSimulacion != null) {
      this.cargarResultado(Number(idSimulacion));
    }
  }

  cargarResultado(idSimulacion: number): void {
    this.cargando = true;
    this.error = '';

    this.simulacionesServicio.obtenerResultado(idSimulacion).subscribe({
      next: (resultado) => {
        this.resultado = resultado;
        this.cargando = false;
      },

      error: (respuesta: HttpErrorResponse) => {
        this.error = respuesta.status === 404
          ? 'Esta práctica todavía no tiene un informe publicado.'
          : respuesta.status === 403
            ? 'Este informe pertenece a otra cuenta.'
            : 'No se pudo cargar el resultado de la simulación.';
        this.cargando = false;
      },
    });
  }
}
