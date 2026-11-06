import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PublicoLayout } from '../publico-layout/publico-layout';

@Component({
  selector: 'app-planes-componente',
  imports: [PublicoLayout, RouterLink],
  templateUrl: './planes-componente.html',
  styleUrl: './planes-componente.css',
})
export class PlanesComponente {}
