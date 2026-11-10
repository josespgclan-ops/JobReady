import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { UsuarioResumen } from '../../modelos/api-modelos';
import { AdminServicio } from '../../servicios/admin-servicio';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

/** US12: el administrador suspende o reactiva cuentas de candidatos y evaluadores. */
@Component({
  selector: 'app-admin-usuarios',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [PanelRolLayout, FormsModule, DatePipe],
  templateUrl: './admin-usuarios.html',
  styleUrls: ['../panel-rol-layout/panel-contenido.css'],
})
export class AdminUsuarios implements OnInit {
  private readonly servicio = inject(AdminServicio);

  protected usuarios: UsuarioResumen[] = [];
  protected cargando = true;
  protected procesandoId: number | null = null;
  protected mensaje = '';
  protected error = '';

  protected busqueda = '';
  protected filtroRol = '';
  protected filtroEstado: '' | 'activo' | 'inactivo' = '';

  ngOnInit(): void {
    this.servicio.listarUsuarios().subscribe({
      next: (lista) => { this.usuarios = lista; this.cargando = false; },
      error: () => { this.error = 'No se pudo cargar la lista de usuarios.'; this.cargando = false; },
    });
  }

  protected get usuariosFiltrados(): UsuarioResumen[] {
    const texto = this.busqueda.trim().toLowerCase();
    return this.usuarios.filter((u) =>
      (!this.filtroRol || u.rol?.nombre === this.filtroRol) &&
      (!this.filtroEstado || this.estado(u) === this.filtroEstado) &&
      (!texto || `${u.nombreCompleto} ${u.username} ${u.email ?? ''}`.toLowerCase().includes(texto)));
  }

  protected estado(u: UsuarioResumen): 'activo' | 'inactivo' {
    return u.estado === 'inactivo' ? 'inactivo' : 'activo';
  }

  protected textoRol(u: UsuarioResumen): string {
    const nombres: Record<string, string> = { ADMIN: 'Administrador', EVALUADOR: 'Evaluador', CANDIDATO: 'Candidato' };
    return nombres[u.rol?.nombre ?? ''] ?? (u.rol?.nombre || '—');
  }

  /** US12 escenario 1: suspender bloquea el inicio de sesión de la cuenta. */
  protected suspender(u: UsuarioResumen): void {
    if (!confirm(`¿Suspender la cuenta de ${u.nombreCompleto}? No podrá iniciar sesión hasta que la reactives.`)) return;
    this.cambiar(u, 'suspender');
  }

  /** US12 escenario 2: reactivar permite volver a iniciar sesión. */
  protected reactivar(u: UsuarioResumen): void {
    this.cambiar(u, 'reactivar');
  }

  private cambiar(u: UsuarioResumen, accion: 'suspender' | 'reactivar'): void {
    this.mensaje = '';
    this.error = '';
    this.procesandoId = u.idUsuario;
    const solicitud = accion === 'suspender' ? this.servicio.suspenderUsuario(u.idUsuario) : this.servicio.reactivarUsuario(u.idUsuario);
    solicitud.subscribe({
      next: (actualizado) => {
        this.usuarios = this.usuarios.map((x) => (x.idUsuario === actualizado.idUsuario ? actualizado : x));
        this.mensaje = accion === 'suspender' ? `Cuenta de ${u.nombreCompleto} suspendida.` : `Cuenta de ${u.nombreCompleto} reactivada.`;
        this.procesandoId = null;
      },
      error: (e: HttpErrorResponse) => {
        this.error = e.error?.message || 'No se pudo cambiar el estado de la cuenta.';
        this.procesandoId = null;
      },
    });
  }
}
