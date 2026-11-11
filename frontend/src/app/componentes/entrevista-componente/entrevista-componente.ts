import { ChangeDetectionStrategy, Component, ElementRef, OnDestroy, OnInit, ViewChild, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Carrera } from '../../modelos/api-modelos';
import { CarrerasServicio } from '../../servicios/carreras-servicio';
import { SimulacionesServicio } from '../../servicios/simulaciones-servicio';
import { CandidatoLayoutComponente } from '../candidato-layout/candidato-layout';

@Component({
  selector: 'app-entrevista-componente',
  // Angular 22 usa OnPush por defecto; esta vista cambia al llegar datos de la API.
  changeDetection: ChangeDetectionStrategy.Eager,
  imports: [CandidatoLayoutComponente, FormsModule, RouterLink],
  templateUrl: './entrevista-componente.html',
  styleUrl: './entrevista-componente.css',
})
export class EntrevistaComponente implements OnInit, OnDestroy {
  private readonly carrerasServicio = inject(CarrerasServicio);
  private readonly simulacionesServicio = inject(SimulacionesServicio);
  private readonly route = inject(ActivatedRoute);
  @ViewChild('vistaPrevia') private vistaPrevia?: ElementRef<HTMLVideoElement>;
  @ViewChild('confirmarFin') private dialogoConfirmar?: ElementRef<HTMLDialogElement>;
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
  private blobGrabado?: Blob;
  private inicioGrabacion = 0;
  private duracionGrabada = 0;
  protected carreras: Carrera[] = [];
  protected idCarrera: number | null = null;
  /** La grabación terminó y espera que el candidato confirme el fin de la simulación. */
  protected confirmandoFin = false;
  /** El candidato confirmó el fin de la simulación: ya puede revisar y enviar el video. */
  protected finalizada = false;
  protected enviando = false;
  /** Una grabación enviada ya no puede reenviarse; se desbloquea al grabar otra. */
  protected enviado = false;
  protected mensajeEnvio = '';
  protected errorEnvio = '';
  protected mensaje = 'Comprueba tu cámara y micrófono antes de comenzar.';

  protected get tiempoRestante(): string {
    return this.formatearTiempo(this.segundosRestantes);
  }

  protected get duracionTexto(): string {
    return this.formatearTiempo(this.duracionGrabada);
  }

  private formatearTiempo(totalSegundos: number): string {
    const minutos = Math.floor(totalSegundos / 60).toString().padStart(2, '0');
    const segundos = (totalSegundos % 60).toString().padStart(2, '0');
    return `${minutos}:${segundos}`;
  }

  ngOnInit(): void {
    const carrera = this.route.snapshot.queryParamMap.get('carrera');
    this.carrerasServicio.listar().subscribe({
      next: (carreras) => {
        this.carreras = carreras.filter((c) => c.activo !== false);
        const preseleccionada = Number(carrera);
        if (carrera && this.carreras.some((c) => c.idCarrera === preseleccionada)) this.idCarrera = preseleccionada;
      },
      error: () => this.errorEnvio = 'No se pudieron cargar las carreras. Comprueba la conexión con el servidor.',
    });
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
    this.blobGrabado = undefined;
    this.confirmandoFin = false;
    this.finalizada = false;
    this.enviado = false;
    this.mensajeEnvio = '';
    this.errorEnvio = '';
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
          // El tipo se guarda sin parámetros de códec (p. ej. "video/webm"), que es lo que valida el backend.
          const tipo = (this.grabador?.mimeType || 'video/webm').split(';')[0];
          this.blobGrabado = new Blob(this.fragmentos, { type: tipo });
          this.videoGrabado = URL.createObjectURL(this.blobGrabado);
          this.duracionGrabada = Math.min(180, Math.max(1, Math.round((Date.now() - this.inicioGrabacion) / 1000)));
          this.mensaje = 'Grabación detenida. Confirma si deseas finalizar la simulación.';
          this.abrirConfirmacion();
        } else {
          this.mensaje = 'No se pudo guardar la grabación. Vuelve a comprobar la cámara e inténtalo de nuevo.';
        }
      };
      this.grabador.start();
      this.inicioGrabacion = Date.now();
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

  /**
   * Detiene la grabación y apaga cámara y micrófono en el acto. MediaRecorder entrega los
   * fragmentos ya capturados en su evento "stop", así que apagar las pistas aquí no pierde datos.
   */
  protected detenerGrabacion(): void {
    if (!this.grabando) return;
    this.grabando = false;
    this.limpiarReloj();
    if (this.grabador && this.grabador.state !== 'inactive') this.grabador.stop();
    this.detenerDispositivos();
    this.mensaje = 'Procesando la grabación…';
  }

  private abrirConfirmacion(): void {
    this.confirmandoFin = true;
    const dialogo = this.dialogoConfirmar?.nativeElement;
    if (dialogo && !dialogo.open) dialogo.showModal();
  }

  private cerrarConfirmacion(): void {
    this.confirmandoFin = false;
    const dialogo = this.dialogoConfirmar?.nativeElement;
    if (dialogo?.open) dialogo.close();
  }

  /** Esc no cierra la confirmación: el candidato debe elegir finalizar o descartar. */
  protected impedirCierre(evento: Event): void {
    evento.preventDefault();
  }

  protected confirmarFinalizacion(): void {
    if (!this.blobGrabado) return;
    this.cerrarConfirmacion();
    this.finalizada = true;
    this.mensaje = 'Simulación finalizada. Revisa tu grabación y pulsa "Enviar video" para que un evaluador la revise.';
  }

  /** Descarta la grabación sin enviarla; el candidato puede volver a comprobar la cámara y grabar otra. */
  protected descartarGrabacion(): void {
    this.cerrarConfirmacion();
    if (this.videoGrabado) URL.revokeObjectURL(this.videoGrabado);
    this.videoGrabado = '';
    this.blobGrabado = undefined;
    this.duracionGrabada = 0;
    this.finalizada = false;
    this.segundosRestantes = 180;
    this.mensaje = 'Grabación descartada. Comprueba la cámara y el micrófono para grabar de nuevo.';
  }

  protected descargarGrabacion(): void {
    if (!this.videoGrabado) return;
    const enlace = document.createElement('a');
    enlace.href = this.videoGrabado;
    enlace.download = `jobready-practica-${new Date().toISOString().slice(0, 10)}.webm`;
    enlace.click();
  }

  protected get puedeEnviar(): boolean {
    return this.finalizada && !!this.blobGrabado && !!this.idCarrera && !this.enviando && !this.enviado && !this.grabando;
  }

  protected enviarGrabacion(): void {
    if (!this.finalizada || !this.blobGrabado || this.enviando || this.enviado) return;
    if (!this.idCarrera) {
      this.errorEnvio = 'Selecciona la carrera de la entrevista antes de enviarla.';
      return;
    }
    this.enviando = true;
    this.errorEnvio = '';
    this.mensajeEnvio = '';
    this.simulacionesServicio.enviarEntrevista(this.blobGrabado, this.idCarrera, this.duracionGrabada).subscribe({
      next: () => {
        this.enviando = false;
        this.enviado = true;
        this.mensajeEnvio = 'Entrevista enviada. Un evaluador la revisará y verás el estado en tu historial.';
      },
      error: (respuesta: HttpErrorResponse) => {
        this.enviando = false;
        if (respuesta.status === 409) {
          // El backend ya tiene este mismo video: se considera enviado.
          this.enviado = true;
          this.mensajeEnvio = 'Esta grabación ya había sido enviada. Puedes ver su estado en tu historial.';
          return;
        }
        this.errorEnvio = respuesta.status === 413
          ? 'El video es demasiado grande para enviarse.'
          : respuesta.error?.message || 'No se pudo enviar la entrevista. Inténtalo de nuevo.';
      },
    });
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
    this.cerrarConfirmacion();
    if (this.grabador?.state === 'recording') this.grabador.stop();
    this.limpiarReloj();
    this.detenerDispositivos();
    if (this.videoGrabado) URL.revokeObjectURL(this.videoGrabado);
  }
}
