# Diagrama de base de datos JobReady

La ampliacion incorpora preguntas de entrevista y las respuestas realizadas dentro de cada simulacion.
Una pregunta puede ser general (`id_carrera` nulo) o estar dirigida a una carrera. Cada simulacion solo
puede contener una respuesta por pregunta. La respuesta puede guardar texto y asociarse a un archivo de
video o audio previamente registrado en `archivos_simulacion`.

```mermaid
erDiagram
    ROLES ||--o{ USUARIOS : asigna
    USUARIOS ||--o{ SUSCRIPCIONES : posee
    PLANES ||--o{ SUSCRIPCIONES : incluye
    USUARIOS ||--o{ USUARIO_CARRERAS : selecciona
    CARRERAS ||--o{ USUARIO_CARRERAS : pertenece
    USUARIOS ||--o{ SESIONES_PROGRAMADAS : agenda
    CARRERAS ||--o{ SESIONES_PROGRAMADAS : orienta
    USUARIOS ||--o{ SIMULACIONES : realiza
    CARRERAS ||--o{ SIMULACIONES : contextualiza
    SESIONES_PROGRAMADAS ||--o{ SIMULACIONES : origina
    SIMULACIONES ||--o{ ARCHIVOS_SIMULACION : contiene
    SIMULACIONES ||--o| EVALUACIONES_SIMULACION : recibe
    USUARIOS ||--o{ EVALUACIONES_SIMULACION : evalua
    USUARIOS ||--o{ ENCUESTAS_SATISFACCION : responde
    CARRERAS ||--o{ PREGUNTAS_ENTREVISTA : clasifica
    SIMULACIONES ||--o{ RESPUESTAS_ENTREVISTA : contiene
    PREGUNTAS_ENTREVISTA ||--o{ RESPUESTAS_ENTREVISTA : responde
    ARCHIVOS_SIMULACION ||--o{ RESPUESTAS_ENTREVISTA : evidencia

    PREGUNTAS_ENTREVISTA {
        int id_pregunta PK
        int id_carrera FK
        text texto_pregunta
        varchar categoria
        varchar nivel_dificultad
        boolean activo
        timestamp fecha_creacion
    }

    RESPUESTAS_ENTREVISTA {
        int id_respuesta PK
        int id_simulacion FK
        int id_pregunta FK
        int id_archivo FK
        text texto_respuesta
        int duracion_segundos
        timestamp fecha_respuesta
        varchar estado
    }
```

## Reglas principales

- Una pregunta puede utilizarse en muchas simulaciones.
- Una simulacion puede responder muchas preguntas.
- No puede existir mas de una respuesta para la misma pregunta dentro de una simulacion.
- Las preguntas se desactivan en lugar de eliminarse para conservar el historial.
- Las respuestas se eliminan automaticamente si se elimina su simulacion.
- Si se elimina un archivo, la respuesta permanece y su referencia al archivo queda nula.
