import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-historial',
  imports: [RouterLink, CandidatoLayoutComponente],
  templateUrl: './candidato-historial.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-historial.css'],
})
export class CandidatoHistorialComponente {}
