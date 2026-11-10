import { Routes } from '@angular/router';
import { HomeComponente } from './componentes/home-componente/home-componente';
import { PreguntasComponente } from './componentes/preguntas-componente/preguntas-componente';
import { IngresarComponente } from './componentes/ingresar-componente/ingresar-componente';
import { RegistroComponente } from './componentes/registro-componente/registro-componente';
import { HistorialComponent } from './componentes/historial-component/historial-component';
import { NosotrosComponente } from './componentes/nosotros-componente/nosotros-componente';
import { PlanesComponente } from './componentes/planes-componente/planes-componente';
import { NovedadesComponente } from './componentes/novedades-componente/novedades-componente';
import { ContactoComponente } from './componentes/contacto-componente/contacto-componente';
import { CandidatoPanelComponente } from './componentes/candidato-panel/candidato-panel';
import { CandidatoHistorialComponente } from './componentes/candidato-historial/candidato-historial';
import { CandidatoComparacionesComponente } from './componentes/candidato-comparaciones/candidato-comparaciones';
import { CandidatoResultadosComponente } from './componentes/candidato-resultados/candidato-resultados';
import { CandidatoOrganizarComponente } from './componentes/candidato-organizar/candidato-organizar';
import { CandidatoCarrerasComponente } from './componentes/candidato-carreras/candidato-carreras';
import { CandidatoSatisfaccionComponente } from './componentes/candidato-satisfaccion/candidato-satisfaccion';
import { EntrevistaComponente } from './componentes/entrevista-componente/entrevista-componente';
import { AdministracionComponente } from './componentes/administracion-componente/administracion-componente';
import { EvaluadorComponente } from './componentes/evaluador-componente/evaluador-componente';
import { AccesoDenegadoComponente } from './componentes/acceso-denegado-componente/acceso-denegado-componente';
import { MiPanelComponente } from './componentes/mi-panel-componente/mi-panel-componente';
import { EvaluadorPendientes } from './componentes/evaluador-pendientes/evaluador-pendientes';
import { EvaluadorEvaluar } from './componentes/evaluador-evaluar/evaluador-evaluar';
import { EvaluadorEvaluaciones } from './componentes/evaluador-evaluaciones/evaluador-evaluaciones';
import { AdminPreguntas } from './componentes/admin-preguntas/admin-preguntas';
import { AdminCarreras } from './componentes/admin-carreras/admin-carreras';
import { AdminUsuarios } from './componentes/admin-usuarios/admin-usuarios';
import { AdminPendientes } from './componentes/admin-pendientes/admin-pendientes';
import { requiereRol, requiereSesion } from './servicios/rol-guard';

export const routes: Routes = [
  { path: '', component: HomeComponente },
  { path: 'home', component: HomeComponente },
  { path: 'preguntas', component: PreguntasComponente },
  { path: 'ingresar', component: IngresarComponente },
  { path: 'registro', component: RegistroComponente },
  { path: 'nosotros', component: NosotrosComponente },
  { path: 'planes', component: PlanesComponente },
  { path: 'novedades', component: NovedadesComponente },
  { path: 'contacto', component: ContactoComponente },
  { path: 'mi-panel', component: MiPanelComponente, canActivate: [requiereSesion] },
  { path: 'panel', component: CandidatoPanelComponente, canActivate: [requiereRol('CANDIDATO')] },
  { path: 'entrevista', component: EntrevistaComponente, canActivate: [requiereRol('CANDIDATO')] },
  { path: 'historial', component: CandidatoHistorialComponente, canActivate: [requiereRol('CANDIDATO')] },
  { path: 'historial-prototipo', component: HistorialComponent },
  { path: 'comparaciones', component: CandidatoComparacionesComponente, canActivate: [requiereRol('CANDIDATO')] },
  { path: 'resultados', component: CandidatoResultadosComponente, canActivate: [requiereRol('CANDIDATO')] },
  { path: 'organizar', component: CandidatoOrganizarComponente, canActivate: [requiereRol('CANDIDATO')] },
  { path: 'carreras', component: CandidatoCarrerasComponente, canActivate: [requiereRol('CANDIDATO')] },
  { path: 'satisfaccion', component: CandidatoSatisfaccionComponente, canActivate: [requiereRol('CANDIDATO')] },
  // Administrador: gestión de la plataforma.
  { path: 'administracion', component: AdministracionComponente, canActivate: [requiereRol('ADMIN')] },
  { path: 'administracion/preguntas', component: AdminPreguntas, canActivate: [requiereRol('ADMIN')] },
  { path: 'administracion/carreras', component: AdminCarreras, canActivate: [requiereRol('ADMIN')] },
  { path: 'administracion/usuarios', component: AdminUsuarios, canActivate: [requiereRol('ADMIN')] },
  { path: 'administracion/pendientes', component: AdminPendientes, canActivate: [requiereRol('ADMIN')] },
  // Evaluador: revisión de prácticas.
  { path: 'evaluador', component: EvaluadorComponente, canActivate: [requiereRol('EVALUADOR')] },
  { path: 'evaluador/pendientes', component: EvaluadorPendientes, canActivate: [requiereRol('EVALUADOR')] },
  { path: 'evaluador/evaluar/:id', component: EvaluadorEvaluar, canActivate: [requiereRol('EVALUADOR')] },
  { path: 'evaluador/evaluaciones', component: EvaluadorEvaluaciones, canActivate: [requiereRol('EVALUADOR')] },
  { path: 'acceso-denegado', component: AccesoDenegadoComponente },
  { path: '**', redirectTo: '' }
];
