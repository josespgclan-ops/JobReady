import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Carrera, SesionProgramada, SesionProgramadaRequest, UsuarioCarrera } from '../../modelos/api-modelos';
import { CarrerasServicio } from '../../servicios/carreras-servicio';
import { CuentasServicio } from '../../servicios/cuentas-servicio';
import { SimulacionesServicio } from '../../servicios/simulaciones-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-organizar',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [CandidatoLayoutComponente, FormsModule, DatePipe],
  templateUrl: './candidato-organizar.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-organizar.css'],
})
export class CandidatoOrganizarComponente implements OnInit {
  private readonly simulacionesServicio = inject(SimulacionesServicio);
  private readonly carrerasServicio = inject(CarrerasServicio);
  private readonly cuentasServicio = inject(CuentasServicio);

  /** Primero las carreras elegidas por el candidato; si no eligió ninguna, todo el catálogo activo. */
  protected carreras: Carrera[] = [];
  protected sesiones: SesionProgramada[] = [];
  protected cargando = true;
  protected error = '';

  protected fecha = '';
  protected hora = '';
  protected idCarrera: number | null = null;
  protected guardando = false;
  protected intento = false;
  protected mensaje = '';
  protected errorForm = '';
  protected cancelando: number | null = null;

  protected readonly hoy = this.formatoFecha(new Date());

  protected get proximas(): SesionProgramada[] {
    return this.sesiones.filter((s) => s.estado === 'programada' && this.instante(s) >= Date.now());
  }

  protected get anteriores(): SesionProgramada[] {
    return this.sesiones.filter((s) => !this.proximas.includes(s)).reverse();
  }

  protected get fechaInvalida(): boolean {
    if (!this.fecha || !this.hora) return true;
    return new Date(`${this.fecha}T${this.hora}`).getTime() < Date.now();
  }

  ngOnInit(): void {
    forkJoin({
      catalogo: this.carrerasServicio.listar(),
      mias: this.cuentasServicio.listarUsuariosCarreras<UsuarioCarrera>(),
      sesiones: this.simulacionesServicio.listarSesiones<SesionProgramada>(),
    }).subscribe({
      next: ({ catalogo, mias, sesiones }) => {
        const activas = catalogo.filter((c) => c.activo !== false);
        const elegidas = new Set(mias.map((m) => m.carrera.idCarrera));
        this.carreras = elegidas.size ? activas.filter((c) => elegidas.has(c.idCarrera)) : activas;
        this.sesiones = sesiones;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar tus sesiones. Comprueba la conexión con el servidor.';
        this.cargando = false;
      },
    });
  }

  protected programar(): void {
    this.intento = true;
    this.mensaje = '';
    this.errorForm = '';
    if (this.fechaInvalida) {
      this.errorForm = 'Elige una fecha y una hora futuras.';
      return;
    }
    if (this.guardando) return;
    this.guardando = true;
    const datos: SesionProgramadaRequest = { idCarrera: this.idCarrera, fechaProgramada: this.fecha, horaProgramada: this.hora };
    this.simulacionesServicio.crearSesion<SesionProgramadaRequest>(datos).subscribe({
      next: () => {
        this.guardando = false;
        this.intento = false;
        this.fecha = '';
        this.hora = '';
        this.idCarrera = null;
        this.mensaje = 'Sesión programada.';
        this.recargar();
      },
      error: (respuesta: HttpErrorResponse) => {
        this.guardando = false;
        this.errorForm = respuesta.error?.message || 'No se pudo programar la sesión. Inténtalo de nuevo.';
      },
    });
  }

  protected cancelar(sesion: SesionProgramada): void {
    if (this.cancelando !== null) return;
    this.cancelando = sesion.idSesion;
    this.simulacionesServicio.cancelarSesion<SesionProgramada>(sesion.idSesion).subscribe({
      next: () => { this.cancelando = null; this.recargar(); },
      error: () => { this.cancelando = null; this.errorForm = 'No se pudo cancelar la sesión.'; },
    });
  }

  protected instanteFecha(sesion: SesionProgramada): Date {
    return new Date(`${sesion.fechaProgramada}T${sesion.horaProgramada}`);
  }

  protected textoEstado(sesion: SesionProgramada): string {
    if (sesion.estado === 'cancelada') return 'Cancelada';
    return this.instante(sesion) < Date.now() ? 'Vencida' : 'Programada';
  }

  private recargar(): void {
    this.simulacionesServicio.listarSesiones<SesionProgramada>().subscribe({
      next: (sesiones) => this.sesiones = sesiones,
      error: () => this.error = 'No se pudieron actualizar tus sesiones.',
    });
  }

  private instante(sesion: SesionProgramada): number {
    return this.instanteFecha(sesion).getTime();
  }

  private formatoFecha(fecha: Date): string {
    const dos = (n: number) => n.toString().padStart(2, '0');
    return `${fecha.getFullYear()}-${dos(fecha.getMonth() + 1)}-${dos(fecha.getDate())}`;
  }
}
