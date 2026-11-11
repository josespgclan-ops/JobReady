import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable, forkJoin } from 'rxjs';
import { Carrera, UsuarioCarrera } from '../../modelos/api-modelos';
import { CarrerasServicio } from '../../servicios/carreras-servicio';
import { CuentasServicio } from '../../servicios/cuentas-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-carreras',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, RouterLink, CandidatoLayoutComponente],
  templateUrl: './candidato-carreras.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-carreras.css'],
})
export class CandidatoCarrerasComponente implements OnInit {
  private readonly carrerasServicio = inject(CarrerasServicio);
  private readonly cuentasServicio = inject(CuentasServicio);
  protected carreras: Carrera[] = [];
  /** Ids de las carreras que el candidato tiene guardadas en la BD. */
  protected elegidas = new Set<number>();
  /** Carrera cuya selección se está guardando (evita dobles clics). */
  protected guardando: number | null = null;
  protected soloMias = false;
  protected busqueda = '';
  protected cargando = true;
  protected error = '';
  protected errorGuardar = '';

  get carrerasFiltradas(): Carrera[] {
    const termino = this.busqueda.trim().toLocaleLowerCase('es');
    return this.carreras.filter((carrera) => carrera.activo !== false
      && (!this.soloMias || this.elegidas.has(carrera.idCarrera))
      && carrera.nombre.toLocaleLowerCase('es').includes(termino));
  }

  ngOnInit(): void {
    forkJoin({
      carreras: this.carrerasServicio.listar(),
      mias: this.cuentasServicio.listarUsuariosCarreras<UsuarioCarrera>(),
    }).subscribe({
      next: ({ carreras, mias }) => {
        this.carreras = carreras;
        this.elegidas = new Set(mias.map((m) => m.carrera.idCarrera));
        this.cargando = false;
      },
      error: () => { this.error = 'No se pudieron cargar las carreras. Comprueba la conexión con el servidor.'; this.cargando = false; },
    });
  }

  protected alternar(carrera: Carrera): void {
    if (this.guardando !== null) return;
    const elegida = this.elegidas.has(carrera.idCarrera);
    this.guardando = carrera.idCarrera;
    this.errorGuardar = '';
    const solicitud: Observable<unknown> = elegida
      ? this.cuentasServicio.quitarCarrera(carrera.idCarrera)
      : this.cuentasServicio.asociarCarrera<{ idCarrera: number }>({ idCarrera: carrera.idCarrera });
    solicitud.subscribe({
      next: () => {
        if (elegida) this.elegidas.delete(carrera.idCarrera); else this.elegidas.add(carrera.idCarrera);
        this.guardando = null;
      },
      error: () => {
        this.errorGuardar = `No se pudo actualizar "${carrera.nombre}". Inténtalo de nuevo.`;
        this.guardando = null;
      },
    });
  }
}
