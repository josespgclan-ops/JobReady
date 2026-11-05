import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

interface Simulacion {
  numero: number;
  nota: string;
}

@Component({
  selector: 'app-historial-component',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './historial-component.html',
  styleUrl: './historial-component.css',
})
export class HistorialComponent {
  simulaciones: Simulacion[] = [
    { numero: 1, nota: '10/10' },
    { numero: 2, nota: '07/10' },
    { numero: 3, nota: '05/10' },
    { numero: 4, nota: '06/10' },
    { numero: 5, nota: '08/10' },
    { numero: 6, nota: '02/10' },
  ];

  filasVacias = [1, 2];
}
