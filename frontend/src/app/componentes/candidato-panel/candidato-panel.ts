import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { EnvioEntrevista, ResultadoSimulacion, SesionProgramada, UsuarioCarrera } from '../../modelos/api-modelos';
import { CuentasServicio } from '../../servicios/cuentas-servicio';
import { SimulacionesServicio } from '../../servicios/simulaciones-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-panel',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterLink, DatePipe, CandidatoLayoutComponente],
  templateUrl: './candidato-panel.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-panel.css'],
})
export class CandidatoPanelComponente implements OnInit {
  private readonly simulacionesServicio = inject(SimulacionesServicio);
  private readonly cuentasServicio = inject(CuentasServicio);

  protected cargando = true;
  protected error = '';
  protected envios: EnvioEntrevista[] = [];
  protected misCarreras: UsuarioCarrera[] = [];
  protected proximaSesion: SesionProgramada | null = null;
  protected ultimoResultado: ResultadoSimulacion | null = null;

  protected get enviadas(): number { return this.envios.filter((e) => e.idArchivoVideo).length; }
  protected get pendientes(): number { return this.envios.filter((e) => e.estadoRevision === 'PENDIENTE').length; }
  protected get revisadas(): number { return this.envios.filter((e) => e.estadoRevision === 'REVISADA').length; }
  protected get nombresCarreras(): string {
    const nombres = this.misCarreras.slice(0, 3).map((c) => c.carrera.nombre).join(', ');
    return this.misCarreras.length > 3 ? `${nombres}…` : nombres;
  }

  ngOnInit(): void {
    forkJoin({
      envios: this.simulacionesServicio.listarMisEnvios(),
      carreras: this.cuentasServicio.listarUsuariosCarreras<UsuarioCarrera>(),
      sesiones: this.simulacionesServicio.listarSesiones<SesionProgramada>(),
    }).subscribe({
      next: ({ envios, carreras, sesiones }) => {
        this.envios = envios;
        this.misCarreras = carreras;
        this.proximaSesion = sesiones.find((s) => s.estado === 'programada' && this.esFutura(s)) ?? null;
        this.cargando = false;
        this.cargarUltimoResultado();
      },
      error: () => {
        this.error = 'No se pudo cargar tu resumen. Comprueba la conexión con el servidor.';
        this.cargando = false;
      },
    });
  }

  /** El historial viene ordenado del envío más reciente al más antiguo. */
  private cargarUltimoResultado(): void {
    const revisada = this.envios.find((e) => e.estadoRevision === 'REVISADA');
    if (!revisada) return;
    this.simulacionesServicio.obtenerResultado(revisada.idSimulacion)
      .pipe(catchError(() => of(null)))
      .subscribe((resultado) => this.ultimoResultado = resultado);
  }

  private esFutura(sesion: SesionProgramada): boolean {
    return new Date(`${sesion.fechaProgramada}T${sesion.horaProgramada}`).getTime() >= Date.now();
  }

  protected fechaSesion(sesion: SesionProgramada): Date {
    return new Date(`${sesion.fechaProgramada}T${sesion.horaProgramada}`);
  }
}
