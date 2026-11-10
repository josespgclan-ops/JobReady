import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection } from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { jwtInterceptor } from './servicios/jwt-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    // Los componentes actualizan propiedades normales al recibir datos de la API;
    // zone.js hace que Angular vuelva a dibujar la vista cuando llega cada respuesta.
    provideZoneChangeDetection(),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideRouter(routes),
  ],
};
