import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-comparaciones',
  imports: [RouterLink, CandidatoLayoutComponente],
  templateUrl: './candidato-comparaciones.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-comparaciones.css'],
})
export class CandidatoComparacionesComponente {}
