import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { Carrera, PreguntaEntrevista, PreguntaEntrevistaRequest } from '../../modelos/api-modelos';
import { AdminServicio } from '../../servicios/admin-servicio';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

const FORMULARIO_VACIO: PreguntaEntrevistaRequest = {
  idCarrera: 0, textoPregunta: '', categoria: '', nivelDificultad: '', activo: true,
};

/** US02 y US23: el administrador crea, edita, desactiva y reactiva preguntas del banco. */
@Component({
  selector: 'app-admin-preguntas',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [PanelRolLayout, FormsModule],
  templateUrl: './admin-preguntas.html',
  styleUrls: ['../panel-rol-layout/panel-contenido.css'],
})
export class AdminPreguntas implements OnInit {
  private readonly servicio = inject(AdminServicio);

  protected readonly categorias = ['Tecnica', 'Comunicacion', 'Experiencia', 'Conductual'];
  protected readonly niveles = ['Basico', 'Intermedio', 'Avanzado'];

  protected preguntas: PreguntaEntrevista[] = [];
  protected carreras: Carrera[] = [];
  protected cargando = true;
  protected guardando = false;
  protected mensaje = '';
  protected error = '';

  // Filtros de la tabla
  protected filtroCarrera = '';
  protected filtroEstado: 'todas' | 'activas' | 'inactivas' = 'activas';
  protected busqueda = '';

  // Formulario (crear o editar)
  protected formulario: PreguntaEntrevistaRequest = { ...FORMULARIO_VACIO };
  protected editandoId: number | null = null;
  protected intentoGuardar = false;

  ngOnInit(): void {
    forkJoin({ preguntas: this.servicio.listarPreguntas(), carreras: this.servicio.listarCarreras() }).subscribe({
      next: ({ preguntas, carreras }) => {
        this.preguntas = preguntas;
        this.carreras = carreras.filter((c) => c.activo !== false);
        this.cargando = false;
      },
      error: () => { this.error = 'No se pudo cargar el banco de preguntas.'; this.cargando = false; },
    });
  }

  protected get preguntasFiltradas(): PreguntaEntrevista[] {
    const texto = this.busqueda.trim().toLowerCase();
    return this.preguntas.filter((p) =>
      (!this.filtroCarrera || String(p.carrera?.idCarrera) === this.filtroCarrera) &&
      (this.filtroEstado === 'todas' || (this.filtroEstado === 'activas') === (p.activo !== false)) &&
      (!texto || p.textoPregunta.toLowerCase().includes(texto)));
  }

  protected get formularioInvalido(): boolean {
    const f = this.formulario;
    return !f.idCarrera || f.textoPregunta.trim().length < 10 || !f.categoria || !f.nivelDificultad;
  }

  protected editar(p: PreguntaEntrevista): void {
    this.editandoId = p.idPregunta;
    this.intentoGuardar = false;
    this.formulario = {
      idCarrera: p.carrera?.idCarrera ?? 0,
      textoPregunta: p.textoPregunta,
      categoria: p.categoria ?? '',
      nivelDificultad: p.nivelDificultad ?? '',
      activo: p.activo !== false,
    };
    document.getElementById('form-pregunta')?.scrollIntoView({ behavior: 'smooth' });
  }

  protected cancelarEdicion(): void {
    this.editandoId = null;
    this.intentoGuardar = false;
    this.formulario = { ...FORMULARIO_VACIO };
  }

  protected guardar(): void {
    this.intentoGuardar = true;
    this.mensaje = '';
    this.error = '';
    if (this.formularioInvalido) {
      this.error = 'Completa la carrera, la categoría, el nivel y una pregunta de al menos 10 caracteres.';
      return;
    }
    const datos = { ...this.formulario, idCarrera: Number(this.formulario.idCarrera), textoPregunta: this.formulario.textoPregunta.trim() };
    const solicitud = this.editandoId
      ? this.servicio.actualizarPregunta(this.editandoId, datos)
      : this.servicio.crearPregunta(datos);
    this.guardando = true;
    solicitud.subscribe({
      next: (guardada) => {
        this.reemplazar(guardada);
        this.mensaje = this.editandoId ? 'Pregunta actualizada.' : 'Pregunta registrada y activa en el catálogo.';
        this.guardando = false;
        this.cancelarEdicion();
      },
      error: (e: HttpErrorResponse) => this.fallo(e, 'No se pudo guardar la pregunta.'),
    });
  }

  /** US23: desactivación lógica; la pregunta deja de mostrarse a los candidatos sin borrar el historial. */
  protected desactivar(p: PreguntaEntrevista): void {
    if (!confirm(`¿Desactivar la pregunta "${p.textoPregunta}"? Los candidatos ya no la verán.`)) return;
    this.servicio.desactivarPregunta(p.idPregunta).subscribe({
      next: (actualizada) => { this.reemplazar(actualizada); this.mensaje = 'Pregunta desactivada.'; },
      error: (e: HttpErrorResponse) => this.fallo(e, 'No se pudo desactivar la pregunta.'),
    });
  }

  protected reactivar(p: PreguntaEntrevista): void {
    this.servicio.actualizarPregunta(p.idPregunta, {
      idCarrera: p.carrera?.idCarrera ?? 0, textoPregunta: p.textoPregunta,
      categoria: p.categoria ?? '', nivelDificultad: p.nivelDificultad ?? '', activo: true,
    }).subscribe({
      next: (actualizada) => { this.reemplazar(actualizada); this.mensaje = 'Pregunta reactivada.'; },
      error: (e: HttpErrorResponse) => this.fallo(e, 'No se pudo reactivar la pregunta.'),
    });
  }

  private reemplazar(pregunta: PreguntaEntrevista): void {
    // El backend puede devolver la carrera sin datos; se completa con el catálogo local.
    if (pregunta.carrera && !pregunta.carrera.nombre) {
      pregunta.carrera = this.carreras.find((c) => c.idCarrera === pregunta.carrera!.idCarrera) ?? pregunta.carrera;
    }
    const i = this.preguntas.findIndex((x) => x.idPregunta === pregunta.idPregunta);
    if (i >= 0) this.preguntas[i] = pregunta; else this.preguntas.unshift(pregunta);
    this.preguntas = [...this.preguntas];
  }

  private fallo(e: HttpErrorResponse, texto: string): void {
    this.guardando = false;
    this.error = e.error?.message || texto;
  }
}
