import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

@Component({
  selector: 'app-evaluador-componente',
  imports: [PanelRolLayout, RouterLink],
  templateUrl: './evaluador-componente.html',
  styleUrl: './evaluador-componente.css',
})
export class EvaluadorComponente {}
