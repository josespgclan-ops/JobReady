import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-acceso-denegado-componente',
  imports: [RouterLink],
  template: `<main class="access-page"><section><a routerLink="/" class="brand">Job<span>Ready</span></a><h1>Esta sección pertenece a otro perfil</h1><p>Vuelve a tu panel para continuar con las funciones disponibles para tu cuenta.</p><a routerLink="/mi-panel" class="button">Ir a mi panel</a></section></main>`,
  styles: [':host{display:block;font-family:Arial,sans-serif}.access-page{min-height:100vh;display:grid;place-items:center;background:#dbe0e2;padding:24px}.access-page section{width:min(100%,520px);padding:38px;border-radius:20px;background:#fff;text-align:center;box-shadow:0 15px 40px #15365c22}.brand{font-size:1.7rem;font-weight:800;color:#111;text-decoration:none}.brand span{color:#2b77f2}.access-page h1{font:800 2rem Arial,sans-serif;letter-spacing:normal;margin:28px 0 12px}.access-page p{color:#4c5b6b;line-height:1.5}.button{display:inline-block;margin-top:16px;padding:12px 22px;border-radius:99px;background:#2b77f2;color:#fff;text-decoration:none;font-weight:700}'],
})
export class AccesoDenegadoComponente {}
