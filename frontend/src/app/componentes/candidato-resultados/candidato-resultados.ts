import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-resultados',
  imports: [RouterLink, CandidatoLayoutComponente],
  templateUrl: './candidato-resultados.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-resultados.css'],
})
export class CandidatoResultadosComponente {
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

      error: () => {
        this.error = 'No se pudo cargar el resultado de la simulación.';
        this.cargando = false;
      },
    });
  }
}
