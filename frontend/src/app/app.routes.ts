import { Routes } from '@angular/router';
import { HomeComponente } from './componentes/home-componente/home-componente';
import { PreguntasComponente } from './componentes/preguntas-componente/preguntas-componente';
import { IngresarComponente } from './componentes/ingresar-componente/ingresar-componente';
import { RegistroComponente } from './componentes/registro-componente/registro-componente';
import { HistorialComponent } from './componentes/historial-component/historial-component';


export const routes: Routes = [
  { path: '', component: HomeComponente },
  { path: 'home', component: HomeComponente },
  { path: 'preguntas', component: PreguntasComponente },
  { path: 'ingresar', component: IngresarComponente },
  { path: 'registro', component: RegistroComponente },
  { path: 'historial', component: HistorialComponent },
  { path: '**', redirectTo: '' }
];
