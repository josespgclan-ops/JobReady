import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgTemplateOutlet } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { PreguntasServicio } from '../../servicios/preguntas-servicio';
import { AuthService } from '../../servicios/auth-service';
import { PreguntaEntrevista } from '../../modelos/api-modelos';
import { Carrera } from '../../modelos/api-modelos';
import { CarrerasServicio } from '../../servicios/carreras-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

interface PreguntaMuestra {
  categoria: string;
  dificultad: string;
  texto: string;
}

@Component({
  selector: 'app-preguntas-componente',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [FormsModule, RouterLink, NgTemplateOutlet, CandidatoLayoutComponente, PanelRolLayout],
  templateUrl: './preguntas-componente.html',
  styleUrl: './preguntas-componente.css',
})
export class PreguntasComponente implements OnInit {
  private readonly preguntasServicio = inject(PreguntasServicio);
  private readonly carrerasServicio = inject(CarrerasServicio);
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  protected preguntas: PreguntaMuestra[] = [
    {
      categoria: 'Técnica',
      dificultad: 'Intermedio',
      texto: 'Describe un proyecto de software en el que resolviste un problema complejo.',
    },
    {
      categoria: 'Comunicación',
      dificultad: 'Básico',
      texto: '¿Cómo priorizas tareas cuando tienes varias entregas con plazos cercanos?',
    },
    {
      categoria: 'Experiencia',
      dificultad: 'Intermedio',
      texto: 'Describe un reto relacionado con tu carrera y cómo lo resolverías.',
    },
  ];
  protected cargando = false;
  protected mensaje = '';
  protected tieneSesion = false;
  protected carreras: Carrera[] = [];
  protected carreraSeleccionada = '';

  /** Qué marco usar: el menú del candidato, el panel del evaluador/admin o la página pública. */
  protected vista: 'candidato' | 'panel' | 'publica' = 'publica';
  protected nombreRol = '';

  ngOnInit(): void {
    const carrera = this.route.snapshot.queryParamMap.get('carrera');
    if (carrera && /^\d+$/.test(carrera)) this.carreraSeleccionada = carrera;
    this.tieneSesion = this.authService.estaAutenticado();
    const roles = this.authService.obtenerRoles();
    if (this.tieneSesion && roles.includes('ROLE_CANDIDATO')) {
      this.vista = 'candidato';
    } else if (this.tieneSesion && (roles.includes('ROLE_EVALUADOR') || roles.includes('ROLE_ADMIN'))) {
      this.vista = 'panel';
      this.nombreRol = roles.includes('ROLE_ADMIN') ? 'Administración' : 'Evaluador';
    }
    if (!this.tieneSesion) return;

    // Con sesión no se muestran las preguntas de ejemplo: se espera a las reales del backend.
    this.preguntas = [];
    this.cargando = true;
    this.carrerasServicio.listar().subscribe({
      next: (carreras) => this.carreras = carreras,
      error: () => this.carreras = [],
    });
    this.cargarPreguntas();
  }

  protected filtrarPorCarrera(): void {
    this.cargarPreguntas();
  }

  private cargarPreguntas(): void {
    this.cargando = true;
    this.mensaje = '';
    const solicitud = this.carreraSeleccionada
      ? this.preguntasServicio.listarPorCarrera(Number(this.carreraSeleccionada))
      : this.preguntasServicio.listar();
    solicitud.subscribe({
      next: (preguntas) => {
        // US23: las preguntas desactivadas por el administrador ya no se muestran.
        this.preguntas = preguntas.filter((pregunta) => pregunta.activo !== false).map((pregunta: PreguntaEntrevista) => ({
          categoria: pregunta.categoria ?? 'General',
          dificultad: pregunta.nivelDificultad ?? 'Sin nivel',
          texto: pregunta.textoPregunta,
        }));
        this.cargando = false;
      },
      error: () => {
        this.mensaje = 'No se pudo conectar con el banco del backend. Mostramos preguntas de ejemplo.';
        this.cargando = false;
      },
    });
  }
}
