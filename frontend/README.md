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
      servicios/                  Servicios HTTP, autenticación y guardias de rutas
```

El documento `src/index.html` contiene `<app-root>`, donde Angular monta el componente raíz. `app.html` contiene `<router-outlet>`, que muestra la pantalla elegida por el router. En `app.routes.ts`, `/` y `/home` muestran `HomeComponente`: la landing se edita en `home-componente.html` y su comportamiento en `home-componente.ts`.

Como en el ejemplo del docente, los nombres de carpetas y archivos usan minúsculas y guiones con el sufijo `-componente`; las clases usan PascalCase, por ejemplo `HomeComponente`. Los futuros servicios seguirán el patrón `servicios/nombre-servicio.ts`. Los componentes pueden representar pantallas completas o partes reutilizables.

## Estado de la migración

La landing conserva su estructura Angular original y usa la paleta y los detalles visuales de `Visual.zip`. Las páginas públicas son `/nosotros`, `/planes`, `/novedades` y `/contacto`. Ingreso y registro usan los endpoints públicos del backend. El registro público crea una cuenta de candidato.

Después del ingreso, el JWT dirige a `/panel` (candidato), `/administracion` (administrador) o `/evaluador` (evaluador). Las rutas del candidato son `/entrevista`, `/carreras`, `/historial`, `/comparaciones`, `/resultados`, `/organizar` y `/satisfaccion`. La pantalla `/preguntas` permite explorar ejemplos sin sesión y carga preguntas reales del backend al iniciar sesión; acepta el filtro `?carrera=ID` desde el catálogo de carreras.

La cabina `/entrevista` comprueba cámara y micrófono y permite grabar y descargar un video local de hasta tres minutos. El backend todavía no acepta la subida del archivo. Historial, resultados y comparaciones muestran un estado vacío hasta disponer de consultas que filtren por candidato. La programación de sesiones y la encuesta muestran una vista previa sin envío hasta vincularlas de forma segura a la cuenta autenticada. Los paneles de administrador y evaluador muestran las funciones disponibles y señalan las que requieren endpoints adicionales. Las guardias Angular ordenan la navegación por rol, pero la autorización de operaciones sensibles también debe aplicarse en el backend.

Los precios de `/planes` siguen el diseño recibido: S/29, S/59 y S/99 al mes. La página indica que aún no procesa compras. Los archivos estáticos originales permanecen en la raíz de `frontend/` como material de referencia.

## Para continuar el frontend

Angular Material ya está instalado y su tema está en `src/material-theme.scss`. La demo independiente `admin-preguntas.html`, `admin-preguntas.css` y `admin-preguntas.js` permite revisar la interfaz de administración de preguntas; usa almacenamiento local del navegador y todavía no está conectada a Angular ni al backend.

El componente `historial-component/` recibido del equipo conserva registros de ejemplo y se puede ver en `/historial-prototipo`. La ruta `/historial` corresponde al historial del candidato autenticado. Los componentes `comparacion-component/`, `iniciar-prueba/` y `organizar-sesion-component/` recibidos del equipo siguen como prototipos sin ruta.

Al ejecutar Angular en `http://localhost:4200`, configura `FRONTEND_URL=http://localhost:4200` para el backend y reinícialo; su valor de ejemplo apunta al puerto 3000. El proxy de Angular envía `/api` a `http://localhost:8080`.

## Publicación en GitHub Pages

El flujo `.github/workflows/pages.yml`, situado en la raíz del repositorio, compila Angular con la base `/JobReady/` y publica el resultado cuando se actualiza `main`. El origen de Pages debe ser **GitHub Actions** en Settings → Pages. El enlace previsto es https://josespgclan-ops.github.io/JobReady/.

Para comprobar la compilación destinada a Pages ejecuta `npm run build -- --base-href /JobReady/`. Las rutas abiertas directamente usan una página 404 de respaldo: GitHub Pages puede responder con estado HTTP 404 aunque Angular muestre la pantalla.
