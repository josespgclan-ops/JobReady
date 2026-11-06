import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Carrera } from '../../modelos/api-modelos';
import { CarrerasServicio } from '../../servicios/carreras-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-carreras',
  imports: [FormsModule, RouterLink, CandidatoLayoutComponente],
  templateUrl: './candidato-carreras.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-carreras.css'],
})
export class CandidatoCarrerasComponente implements OnInit {
  private readonly carrerasServicio = inject(CarrerasServicio);
  protected carreras: Carrera[] = [];
  protected busqueda = '';
  protected cargando = true;
  protected error = '';

  get carrerasFiltradas(): Carrera[] {
    const termino = this.busqueda.trim().toLocaleLowerCase('es');
    return this.carreras.filter((carrera) => carrera.activo !== false && carrera.nombre.toLocaleLowerCase('es').includes(termino));
  }

  ngOnInit(): void {
    this.carrerasServicio.listar().subscribe({
      next: (carreras) => { this.carreras = carreras; this.cargando = false; },
      error: () => { this.error = 'No se pudieron cargar las carreras. Comprueba la conexión con el servidor.'; this.cargando = false; },
    });
  }
}
