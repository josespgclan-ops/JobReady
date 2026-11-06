import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PublicoLayout } from '../publico-layout/publico-layout';

@Component({
  selector: 'app-novedades-componente',
  imports: [PublicoLayout, RouterLink],
  templateUrl: './novedades-componente.html',
  styleUrl: './novedades-componente.css',
})
export class NovedadesComponente {}
