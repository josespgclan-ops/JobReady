import { ChangeDetectionStrategy, Component, OnDestroy, OnInit, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EnvioEntrevista } from '../../modelos/api-modelos';
import { SimulacionesServicio } from '../../servicios/simulaciones-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-candidato-historial',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [RouterLink, DatePipe, CandidatoLayoutComponente],
  templateUrl: './candidato-historial.html',
  styleUrls: ['../candidato-layout/candidato-content.css', './candidato-historial.css'],
})
export class CandidatoHistorialComponente implements OnInit, OnDestroy {
  private readonly simulacionesServicio = inject(SimulacionesServicio);

  protected envios: EnvioEntrevista[] = [];
  protected cargando = true;
  protected error = '';

  /** Video que se está reproduciendo (se descarga con el token y se muestra como URL local). */
  protected videoAbierto: EnvioEntrevista | null = null;
  protected urlVideo = '';
  protected cargandoVideo = false;
  protected errorVideo = '';

  ngOnInit(): void {
    this.simulacionesServicio.listarMisEnvios().subscribe({
      next: (envios) => {
        this.envios = envios;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudo cargar tu historial. Comprueba la conexión con el servidor.';
        this.cargando = false;
      },
    });
  }

  protected textoEstado(envio: EnvioEntrevista): string {
    switch (envio.estadoRevision) {
      case 'REVISADA': return 'Revisada';
      case 'PENDIENTE': return 'Pendiente de revisión';
      default: return 'En curso';
    }
  }

  protected duracion(envio: EnvioEntrevista): string {
    if (!envio.duracionSegundos) return '';
    const minutos = Math.floor(envio.duracionSegundos / 60);
    const segundos = envio.duracionSegundos % 60;
    return minutos ? `${minutos} min ${segundos.toString().padStart(2, '0')} s` : `${segundos} s`;
  }

  protected verVideo(envio: EnvioEntrevista): void {
    if (!envio.idArchivoVideo || this.cargandoVideo) return;
    this.cerrarVideo();
    this.videoAbierto = envio;
    this.cargandoVideo = true;
    this.simulacionesServicio.descargarArchivo(envio.idArchivoVideo).subscribe({
      next: (blob) => {
        this.urlVideo = URL.createObjectURL(blob);
        this.cargandoVideo = false;
      },
      error: () => {
        this.errorVideo = 'No se pudo cargar el video.';
        this.cargandoVideo = false;
      },
    });
  }

  protected cerrarVideo(): void {
    if (this.urlVideo) URL.revokeObjectURL(this.urlVideo);
    this.urlVideo = '';
    this.errorVideo = '';
    this.videoAbierto = null;
  }

  ngOnDestroy(): void {
    if (this.urlVideo) URL.revokeObjectURL(this.urlVideo);
  }
}
