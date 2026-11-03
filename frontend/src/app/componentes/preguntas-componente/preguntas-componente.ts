import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { PreguntasServicio } from '../../servicios/preguntas-servicio';
import { AuthService } from '../../servicios/auth-service';
import { PreguntaEntrevista } from '../../modelos/api-modelos';
import { Carrera } from '../../modelos/api-modelos';
import { CarrerasServicio } from '../../servicios/carreras-servicio';

interface PreguntaMuestra {
  categoria: string;
  dificultad: string;
  texto: string;
}

@Component({
  selector: 'app-preguntas-componente',
  imports: [FormsModule, RouterLink],
  templateUrl: './preguntas-componente.html',
  styleUrl: './preguntas-componente.css',
})
export class PreguntasComponente implements OnInit {
  private readonly preguntasServicio = inject(PreguntasServicio);
  private readonly carrerasServicio = inject(CarrerasServicio);
  private readonly authService = inject(AuthService);
  protected preguntas: PreguntaMuestra[] = [
    {
      categoria: 'T?cnica',
      dificultad: 'Intermedio',
      texto: 'Describe un proyecto de software en el que resolviste un problema complejo.',
    },
    {
      categoria: 'Comunicaci?n',
      dificultad: 'B?sico',
      texto: '?C?mo priorizas tareas cuando tienes varias entregas con plazos cercanos?',
    },
    {
      categoria: 'Experiencia',
      dificultad: 'Intermedio',
      texto: 'Describe un reto relacionado con tu carrera y c?mo lo resolver?as.',
    },
  ];
  protected cargando = false;
  protected mensaje = '';
  protected tieneSesion = false;
  protected carreras: Carrera[] = [];
  protected carreraSeleccionada = '';

  ngOnInit(): void {
    this.tieneSesion = this.authService.estaAutenticado();
    if (!this.tieneSesion) return;

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
        this.preguntas = preguntas.map((pregunta: PreguntaEntrevista) => ({
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
