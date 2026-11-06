import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-resultados',
  imports: [RouterLink, CandidatoLayoutComponente],
  templateUrl: './candidato-resultados.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-resultados.css'],
})
export class CandidatoResultadosComponente {}
