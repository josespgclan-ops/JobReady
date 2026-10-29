import { Routes } from '@angular/router';
import { HomeComponente } from './componentes/home-componente/home-componente';

export const routes: Routes = [
  { path: '', component: HomeComponente },
  { path: 'home', component: HomeComponente },
  { path: '**', redirectTo: '' }
];
