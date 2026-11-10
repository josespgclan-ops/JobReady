import { ChangeDetectionStrategy, Component, ElementRef, OnDestroy, ViewChild } from '@angular/core';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-entrevista-componente',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [CandidatoLayoutComponente],
  templateUrl: './entrevista-componente.html',
  styleUrl: './entrevista-componente.css',
})
export class EntrevistaComponente implements OnDestroy {
  @ViewChild('vistaPrevia') private vistaPrevia?: ElementRef<HTMLVideoElement>;
  private flujo?: MediaStream;
  private grabador?: MediaRecorder;
  private fragmentos: Blob[] = [];
  private reloj?: ReturnType<typeof setInterval>;
  private destruido = false;
  protected preparado = false;
  protected grabando = false;
  protected procesando = false;
  protected microfonoActivo = true;
  protected camaraActiva = true;
  protected segundosRestantes = 180;
  protected videoGrabado = '';
  protected mensaje = 'Comprueba tu cámara y micrófono antes de comenzar.';

  protected get tiempoRestante(): string {
    const minutos = Math.floor(this.segundosRestantes / 60).toString().padStart(2, '0');
    const segundos = (this.segundosRestantes % 60).toString().padStart(2, '0');
    return `${minutos}:${segundos}`;
  }

  protected async preparar(): Promise<void> {
    if (this.preparado || this.procesando) return;
    if (!navigator.mediaDevices?.getUserMedia || typeof MediaRecorder === 'undefined') {
      this.mensaje = 'Este navegador no permite comprobar la cámara y grabar video.';
      return;
    }
    this.procesando = true;
    try {
      this.flujo = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
      if (this.vistaPrevia) {
        this.vistaPrevia.nativeElement.srcObject = this.flujo;
        await this.vistaPrevia.nativeElement.play();
      }
      this.preparado = true;
      this.microfonoActivo = true;
      this.camaraActiva = true;
      this.mensaje = 'Cámara y micrófono listos. Puedes iniciar una grabación de hasta tres minutos.';
    } catch {
      this.detenerDispositivos();
      this.mensaje = 'No se pudo acceder a cámara y micrófono. Revisa los permisos y vuelve a intentarlo.';
    } finally {
      this.procesando = false;
    }
  }

  protected alternarMicrofono(): void {
    const pista = this.flujo?.getAudioTracks()[0];
    if (!pista) return;
    pista.enabled = !pista.enabled;
    this.microfonoActivo = pista.enabled;
  }

  protected alternarCamara(): void {
    const pista = this.flujo?.getVideoTracks()[0];
    if (!pista) return;
    pista.enabled = !pista.enabled;
    this.camaraActiva = pista.enabled;
  }

  protected iniciarGrabacion(): void {
    if (!this.flujo || this.grabando || !this.preparado) return;
    if (this.videoGrabado) {
      URL.revokeObjectURL(this.videoGrabado);
      this.videoGrabado = '';
    }
    this.fragmentos = [];
    this.segundosRestantes = 180;
    const tipo = ['video/webm;codecs=vp9,opus', 'video/webm;codecs=vp8,opus', 'video/webm']
      .find((formato) => MediaRecorder.isTypeSupported(formato));
    try {
      this.grabador = new MediaRecorder(this.flujo, tipo ? { mimeType: tipo } : undefined);
      this.grabador.ondataavailable = (evento) => {
        if (evento.data.size > 0) this.fragmentos.push(evento.data);
      };
      this.grabador.onstop = () => {
        if (this.destruido) return;
        if (this.fragmentos.length) {
          this.videoGrabado = URL.createObjectURL(new Blob(this.fragmentos, { type: this.grabador?.mimeType || 'video/webm' }));
          this.mensaje = 'Grabación lista para revisar o descargar en este dispositivo. Aún no se envía al backend.';
        } else {
          this.mensaje = 'No se pudo guardar la grabación. Inténtalo de nuevo.';
        }
        this.detenerDispositivos();
      };
      this.grabador.start();
      this.grabando = true;
      this.mensaje = 'Grabando respuesta…';
      this.reloj = setInterval(() => {
        this.segundosRestantes -= 1;
        if (this.segundosRestantes <= 0) this.detenerGrabacion();
      }, 1000);
    } catch {
      this.mensaje = 'No se pudo iniciar la grabación en este navegador.';
    }
  }

  protected detenerGrabacion(): void {
    if (!this.grabando) return;
    this.grabando = false;
    this.limpiarReloj();
    if (this.grabador?.state === 'recording') this.grabador.stop();
  }

  protected descargarGrabacion(): void {
    if (!this.videoGrabado) return;
    const enlace = document.createElement('a');
    enlace.href = this.videoGrabado;
    enlace.download = `jobready-practica-${new Date().toISOString().slice(0, 10)}.webm`;
    enlace.click();
  }

  private limpiarReloj(): void {
    if (this.reloj) clearInterval(this.reloj);
    this.reloj = undefined;
  }

  private detenerDispositivos(): void {
    this.flujo?.getTracks().forEach((pista) => pista.stop());
    this.flujo = undefined;
    this.preparado = false;
    if (this.vistaPrevia) this.vistaPrevia.nativeElement.srcObject = null;
  }

  ngOnDestroy(): void {
    this.destruido = true;
    if (this.grabador?.state === 'recording') this.grabador.stop();
    this.limpiarReloj();
    this.detenerDispositivos();
    if (this.videoGrabado) URL.revokeObjectURL(this.videoGrabado);
  }
}
