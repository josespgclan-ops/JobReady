import { AfterViewInit, Component, ElementRef, OnDestroy } from '@angular/core';

@Component({
  selector: 'app-home-componente',
  templateUrl: './home-componente.html',
  styleUrl: './home-componente.css',
})
export class HomeComponente implements AfterViewInit, OnDestroy {
  menuAbierto = false;
  private observador?: IntersectionObserver;

  constructor(private elemento: ElementRef<HTMLElement>) {}

  ngAfterViewInit(): void {
    const elementos = this.elemento.nativeElement.querySelectorAll('.reveal');
    if (typeof IntersectionObserver === 'undefined') {
      elementos.forEach((elemento) => elemento.classList.add('visible'));
      return;
    }
    this.observador = new IntersectionObserver((entradas, observador) => {
      entradas.forEach((entrada) => {
        if (entrada.isIntersecting) {
          entrada.target.classList.add('visible');
          observador.unobserve(entrada.target);
        }
      });
    }, { threshold: 0.12 });
    elementos.forEach((elemento) => this.observador!.observe(elemento));
  }

  ngOnDestroy(): void {
    this.observador?.disconnect();
  }
}
