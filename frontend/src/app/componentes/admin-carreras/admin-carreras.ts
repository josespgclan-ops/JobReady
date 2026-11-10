import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Carrera, CarreraRequest } from '../../modelos/api-modelos';
import { AdminServicio } from '../../servicios/admin-servicio';
import { PanelRolLayout } from '../panel-rol-layout/panel-rol-layout';

/** US22: el administrador registra carreras nuevas, las edita y las activa o desactiva. */
@Component({
  selector: 'app-admin-carreras',
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [PanelRolLayout, FormsModule],
  templateUrl: './admin-carreras.html',
  styleUrls: ['../panel-rol-layout/panel-contenido.css'],
})
export class AdminCarreras implements OnInit {
  private readonly servicio = inject(AdminServicio);

  protected carreras: Carrera[] = [];
  protected cargando = true;
  protected guardando = false;
  protected mensaje = '';
  protected error = '';
  protected busqueda = '';

  protected formulario: CarreraRequest = { nombre: '', descripcion: '', activo: true };
  protected editandoId: number | null = null;
  protected intentoGuardar = false;

  ngOnInit(): void {
    this.servicio.listarCarreras().subscribe({
      next: (lista) => { this.carreras = lista; this.cargando = false; },
      error: () => { this.error = 'No se pudo cargar el catálogo de carreras.'; this.cargando = false; },
    });
  }

  protected get carrerasFiltradas(): Carrera[] {
    const texto = this.busqueda.trim().toLowerCase();
    return this.carreras.filter((c) => !texto || c.nombre.toLowerCase().includes(texto));
  }

  protected get nombreInvalido(): boolean {
    return this.formulario.nombre.trim().length < 3;
  }

  protected get nombreRepetido(): boolean {
    const nombre = this.formulario.nombre.trim().toLowerCase();
    return this.carreras.some((c) => c.nombre.toLowerCase() === nombre && c.idCarrera !== this.editandoId);
  }

  protected editar(c: Carrera): void {
    this.editandoId = c.idCarrera;
    this.intentoGuardar = false;
    this.formulario = { nombre: c.nombre, descripcion: c.descripcion ?? '', activo: c.activo !== false };
    document.getElementById('form-carrera')?.scrollIntoView({ behavior: 'smooth' });
  }

  protected cancelarEdicion(): void {
    this.editandoId = null;
    this.intentoGuardar = false;
    this.formulario = { nombre: '', descripcion: '', activo: true };
  }

  protected guardar(): void {
    this.intentoGuardar = true;
    this.mensaje = '';
    this.error = '';
    // US22 escenario 1: el sistema valida que los campos no estén vacíos.
    if (this.nombreInvalido || this.nombreRepetido) {
      this.error = this.nombreRepetido ? 'Ya existe una carrera con ese nombre.' : 'El nombre debe tener al menos 3 caracteres.';
      return;
    }
    const datos: CarreraRequest = { ...this.formulario, nombre: this.formulario.nombre.trim(), descripcion: this.formulario.descripcion.trim() };
    const editando = this.editandoId;
    const solicitud = editando ? this.servicio.actualizarCarrera(editando, datos) : this.servicio.crearCarrera(datos);
    this.guardando = true;
    solicitud.subscribe({
      next: (guardada) => {
        this.reemplazar(guardada);
        this.mensaje = editando ? 'Carrera actualizada.' : 'Carrera registrada. Los candidatos ya pueden verla.';
        this.guardando = false;
        this.cancelarEdicion();
      },
      error: (e: HttpErrorResponse) => { this.guardando = false; this.error = e.error?.message || 'No se pudo guardar la carrera.'; },
    });
  }

  protected cambiarEstado(c: Carrera): void {
    const activar = c.activo === false;
    if (!activar && !confirm(`¿Desactivar la carrera "${c.nombre}"? Los candidatos dejarán de verla.`)) return;
    this.servicio.actualizarCarrera(c.idCarrera, { nombre: c.nombre, descripcion: c.descripcion ?? '', activo: activar }).subscribe({
      next: (actualizada) => { this.reemplazar(actualizada); this.mensaje = activar ? 'Carrera reactivada.' : 'Carrera desactivada.'; },
      error: (e: HttpErrorResponse) => { this.error = e.error?.message || 'No se pudo cambiar el estado.'; },
    });
  }

  private reemplazar(carrera: Carrera): void {
    const i = this.carreras.findIndex((x) => x.idCarrera === carrera.idCarrera);
    if (i >= 0) this.carreras[i] = carrera; else this.carreras.unshift(carrera);
    this.carreras = [...this.carreras];
  }
}
