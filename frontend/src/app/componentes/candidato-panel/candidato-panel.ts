import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-panel',
  imports: [RouterLink, CandidatoLayoutComponente],
  templateUrl: './candidato-panel.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-panel.css'],
})
export class CandidatoPanelComponente {}
