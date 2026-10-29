# JobReady Frontend

Aplicación Angular organizada siguiendo el ejemplo del docente. El backend Spring Boot se encuentra en la carpeta `backend/` del mismo repositorio.

## Ejecutar en WebStorm

Abre `frontend` en WebStorm y ejecuta en su terminal:

```powershell
npm ci
npm start
```

Abre `http://localhost:4200/`. Para comprobar la compilación de producción, ejecuta `npm run build`. El proyecto se verificó con Node.js 24 y npm 11.

## Estructura

```text
frontend/
  angular.json                    Configuración de Angular
  package.json                    Dependencias y comandos
  public/                         Recursos públicos
  src/
    index.html                    Documento de entrada
    main.ts                       Inicio de Angular
    styles.css                    Estilos globales de la landing
    app/
      app.ts, app.html, app.css   Componente raíz
      app.config.ts               Configuración de la aplicación
      app.routes.ts               Rutas y componentes
      componentes/
        home-componente/          Landing page: archivos TS, HTML y CSS
      servicios/                  Futuros servicios de Angular
```

El documento `src/index.html` contiene `<app-root>`, donde Angular monta el componente raíz. `app.html` contiene `<router-outlet>`, que muestra la pantalla elegida por el router. En `app.routes.ts`, `/` y `/home` muestran `HomeComponente`: la landing se edita en `home-componente.html` y su comportamiento en `home-componente.ts`.

Como en el ejemplo del docente, los nombres de carpetas y archivos usan minúsculas y guiones con el sufijo `-componente`; las clases usan PascalCase, por ejemplo `HomeComponente`. Los futuros servicios seguirán el patrón `servicios/nombre-servicio.ts`. Los componentes pueden representar pantallas completas o partes reutilizables.

## Estado de la migración

La landing se adaptó a Angular. El ingreso y el registro aún no están implementados ni conectados al backend. Los archivos originales `index.html`, `styles.css` y `script.js` permanecen en la raíz. La pantalla estática de administración todavía no forma parte de Angular.

## Publicación en GitHub Pages

El flujo `.github/workflows/pages.yml`, situado en la raíz del repositorio, compila Angular con la base `/JobReady/` y publica el resultado cuando se actualiza `main`. El origen de Pages debe ser **GitHub Actions** en Settings → Pages. El enlace previsto es https://josespgclan-ops.github.io/JobReady/.

Para comprobar la compilación destinada a Pages ejecuta `npm run build -- --base-href /JobReady/`. Las rutas abiertas directamente usan una página 404 de respaldo: GitHub Pages puede responder con estado HTTP 404 aunque Angular muestre la pantalla.
