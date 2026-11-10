import { Component, Input, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../servicios/auth-service';

interface OpcionMenu {
  clave: string;
  texto: string;
  ruta: string;
  icono: string;
}

/** Menú de cada rol. El evaluador revisa prácticas; el administrador gestiona la plataforma. */
const MENU_EVALUADOR: OpcionMenu[] = [
  { clave: 'panel', texto: 'Mi panel', ruta: '/evaluador', icono: '⌂' },
  { clave: 'pendientes', texto: 'Pendientes de evaluar', ruta: '/evaluador/pendientes', icono: '◷' },
  { clave: 'evaluaciones', texto: 'Mis evaluaciones', ruta: '/evaluador/evaluaciones', icono: '✓' },
  { clave: 'preguntas', texto: 'Banco de preguntas', ruta: '/preguntas', icono: '?' },
];

const MENU_ADMIN: OpcionMenu[] = [
  { clave: 'panel', texto: 'Mi panel', ruta: '/administracion', icono: '⌂' },
  { clave: 'gestion-preguntas', texto: 'Gestionar preguntas', ruta: '/administracion/preguntas', icono: '▤' },
  { clave: 'carreras', texto: 'Carreras', ruta: '/administracion/carreras', icono: '▣' },
  { clave: 'usuarios', texto: 'Usuarios', ruta: '/administracion/usuarios', icono: '◉' },
  { clave: 'revisiones', texto: 'Revisiones pendientes', ruta: '/administracion/pendientes', icono: '◷' },
];

@Component({
  selector: 'app-panel-rol-layout',
  imports: [RouterLink],
  templateUrl: './panel-rol-layout.html',
  styleUrl: './panel-rol-layout.css',
})
export class PanelRolLayout {
  /** Texto de la etiqueta superior; si no se indica, se deduce del rol de la sesión. */
  @Input() rol = '';
  /** Clave de la opción del menú que se marca como actual. */
  @Input() active = 'panel';

  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly esAdmin = this.auth.obtenerRoles().includes('ROLE_ADMIN');
  protected readonly menu = this.esAdmin ? MENU_ADMIN : MENU_EVALUADOR;

  protected get etiquetaRol(): string {
    return this.rol || (this.esAdmin ? 'Administración' : 'Evaluador');
  }

  cerrarSesion(): void {
    this.auth.cerrarSesion();
    void this.router.navigateByUrl('/ingresar');
  }
}
