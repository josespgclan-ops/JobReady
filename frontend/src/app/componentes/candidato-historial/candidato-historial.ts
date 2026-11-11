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

  /** Práctica cuyo PDF se está descargando y error de descarga (por práctica). */
  protected descargandoPdf: number | null = null;
  protected errorPdf: { idSimulacion: number; mensaje: string } | null = null;

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
      case 'REVISADA': return 'Evaluada';
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

  /**
   * Descarga el PDF de retroalimentación. Se pide con HttpClient para enviar el token: el backend
   * solo entrega el archivo si la práctica pertenece al candidato conectado.
   */
  protected descargarPdf(envio: EnvioEntrevista): void {
    if (!envio.idArchivoPdf || this.descargandoPdf !== null) return;
    this.descargandoPdf = envio.idSimulacion;
    this.errorPdf = null;
    this.simulacionesServicio.descargarArchivo(envio.idArchivoPdf).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(new Blob([blob], { type: 'application/pdf' }));
        const enlace = document.createElement('a');
        enlace.href = url;
        const carrera = (envio.nombreCarrera ?? 'entrevista').normalize('NFD').replace(/[\u0300-\u036f]/g, '')
          .replace(/[^a-zA-Z0-9]+/g, '-').replace(/^-|-$/g, '').toLowerCase();
        enlace.download = `retroalimentacion-${carrera}-practica-${envio.numeroSimulacion ?? envio.idSimulacion}.pdf`;
        enlace.click();
        setTimeout(() => URL.revokeObjectURL(url), 1000);
        this.descargandoPdf = null;
      },
      error: () => {
        this.errorPdf = { idSimulacion: envio.idSimulacion, mensaje: 'No se pudo descargar el PDF.' };
        this.descargandoPdf = null;
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
