import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { CalificacionEncuesta, EncuestaSatisfaccion, EncuestaSatisfaccionRequest } from '../../modelos/api-modelos';
import { CuentasServicio } from '../../servicios/cuentas-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-satisfaccion',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [CandidatoLayoutComponente, FormsModule, DatePipe],
  templateUrl: './candidato-satisfaccion.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-satisfaccion.css'],
})
export class CandidatoSatisfaccionComponente implements OnInit {
  private readonly cuentasServicio = inject(CuentasServicio);

  protected readonly estrellas = [1, 2, 3, 4, 5];
  protected readonly calificaciones: CalificacionEncuesta[] = ['Excelente', 'Bueno', 'Regular', 'Malo'];

  protected satisfaccion = 0;
  protected servicio: CalificacionEncuesta | '' = '';
  protected calidad: CalificacionEncuesta | '' = '';
  protected comentario = '';
  protected enviando = false;
  protected intento = false;
  protected mensaje = '';
  protected error = '';
  protected anteriores: EncuestaSatisfaccion[] = [];

  protected get valido(): boolean {
    return this.satisfaccion >= 1 && !!this.servicio && !!this.calidad && this.comentario.length <= 2000;
  }

  ngOnInit(): void {
    this.cargarAnteriores();
  }

  protected enviar(): void {
    this.intento = true;
    this.mensaje = '';
    this.error = '';
    if (!this.valido) {
      this.error = 'Completa la calificación con estrellas, el servicio y la retroalimentación.';
      return;
    }
    if (this.enviando) return;
    this.enviando = true;
    const datos: EncuestaSatisfaccionRequest = {
      satisfaccionGeneral: this.satisfaccion,
      calificacionServicio: this.servicio as CalificacionEncuesta,
      calidadProducto: this.calidad as CalificacionEncuesta,
      comentarioAdicional: this.comentario.trim(),
    };
    this.cuentasServicio.crearEncuesta<EncuestaSatisfaccionRequest>(datos).subscribe({
      next: () => {
        this.enviando = false;
        this.intento = false;
        this.satisfaccion = 0;
        this.servicio = '';
        this.calidad = '';
        this.comentario = '';
        this.mensaje = '¡Gracias! Tu opinión quedó registrada.';
        this.cargarAnteriores();
      },
      error: (respuesta: HttpErrorResponse) => {
        this.enviando = false;
        this.error = respuesta.error?.message || 'No se pudo enviar la encuesta. Inténtalo de nuevo.';
      },
    });
  }

  private cargarAnteriores(): void {
    this.cuentasServicio.listarMisEncuestas<EncuestaSatisfaccion>().subscribe({
      next: (encuestas) => this.anteriores = encuestas,
      error: () => this.anteriores = [],
    });
  }
}
