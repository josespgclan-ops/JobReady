import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  // Angular 22 usa OnPush por defecto; la raíz se revisa siempre para que las páginas
  // que cargan datos de la API (marcadas como Eager) se actualicen al recibirlos.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {}
