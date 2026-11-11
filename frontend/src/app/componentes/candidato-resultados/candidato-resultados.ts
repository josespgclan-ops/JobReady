import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
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
export class CandidatoResultadosComponente implements OnInit, OnDestroy {
  private readonly simulacionesServicio = inject(SimulacionesServicio);
  private readonly route = inject(ActivatedRoute);

  protected resultado: ResultadoSimulacion | null = null;
  protected cargando = false;
  protected error = '';

  /** PDF de retroalimentación: se descarga con el token y se ofrece como URL local. */
  protected urlPdf = '';
  protected cargandoPdf = false;
  protected errorPdf = '';

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
        if (resultado.idArchivoPdf) this.cargarPdf(resultado.idArchivoPdf);
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

  private cargarPdf(idArchivo: number): void {
    this.cargandoPdf = true;
    this.simulacionesServicio.descargarArchivo(idArchivo).subscribe({
      next: (blob) => {
        this.urlPdf = URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));
        this.cargandoPdf = false;
      },
      error: () => {
        this.errorPdf = 'No se pudo cargar el PDF de retroalimentación.';
        this.cargandoPdf = false;
      },
    });
  }

  ngOnDestroy(): void {
    if (this.urlPdf) URL.revokeObjectURL(this.urlPdf);
  }
}
