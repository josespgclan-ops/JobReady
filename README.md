# JobReady

Aplicación para la preparación de entrevistas profesionales. Este repositorio reúne el backend Spring Boot y el frontend Angular.

```text
JobReady/
  backend/                 API en Java y Spring Boot, scripts de base de datos y documentación
  frontend/                Aplicación Angular y su documentación
  .github/workflows/       Publicación del frontend en GitHub Pages
```

## Para trabajar en equipo

Clona este repositorio una vez. Abre `backend/` en IntelliJ IDEA y `frontend/` en WebStorm, o usa el editor que prefieras. Ambos directorios pertenecen al mismo repositorio Git: realiza los cambios desde la raíz y coordina con tu equipo el uso de ramas y revisiones.

### Frontend

Necesitas Node.js 24 y npm. En la terminal, desde `frontend/`:

```powershell
npm ci
npm start
```

Abre `http://localhost:4200/`. Encontrarás la estructura y más detalles en [frontend/README.md](frontend/README.md).

### Backend

Necesitas Java 21, Maven o el Maven Wrapper y una base de datos PostgreSQL accesible. Usa [application-example.properties](backend/src/main/resources/application-example.properties) como referencia para configurar la conexión y las variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` y `FRONTEND_URL` en tu entorno local. No publiques contraseñas ni el archivo local `application.properties`.

Desde `backend/`, ejecuta `./mvnw spring-boot:run` en macOS/Linux o `.\mvnw.cmd spring-boot:run` en PowerShell. El frontend Angular todavía no está conectado a la API. Para futuras llamadas desde `http://localhost:4200`, configura `FRONTEND_URL` con ese origen; para el sitio publicado usa el origen HTTPS correspondiente.

## Publicación

GitHub Actions compila y publica el frontend cuando se actualiza `main`, si el origen de GitHub Pages está configurado como **GitHub Actions**. El sitio previsto es https://josespgclan-ops.github.io/JobReady/. GitHub Pages solo aloja el frontend; el backend y la base de datos necesitan ejecutarse o desplegarse por separado.

Los historiales de frontend y backend se incorporaron al historial de este repositorio unificado.
