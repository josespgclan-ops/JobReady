import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

@Component({
  selector: 'app-administracion-componente',
  imports: [PanelRolLayout, RouterLink],
  templateUrl: './administracion-componente.html',
  styleUrl: './administracion-componente.css',
})
export class AdministracionComponente {}
