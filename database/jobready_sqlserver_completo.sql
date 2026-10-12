/* =============================================================
   JOBREADY - MODELO COMPLETO PARA MICROSOFT SQL SERVER
   Crea la base, las 13 tablas, relaciones, restricciones e indices.
   El script no elimina tablas ni datos existentes.
   ============================================================= */

IF DB_ID(N'jobready_db') IS NULL
BEGIN
    CREATE DATABASE jobready_db;
END;
GO

USE jobready_db;
GO

/* 1. ROLES */
IF OBJECT_ID(N'dbo.roles', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.roles (
        id_rol INT IDENTITY(1,1) NOT NULL,
        nombre NVARCHAR(50) NOT NULL,
        descripcion NVARCHAR(255) NULL,
        CONSTRAINT PK_roles PRIMARY KEY (id_rol),
        CONSTRAINT UQ_roles_nombre UNIQUE (nombre)
    );
END;
GO

/* 2. USUARIOS */
IF OBJECT_ID(N'dbo.usuarios', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.usuarios (
        id_usuario INT IDENTITY(1,1) NOT NULL,
        id_rol INT NOT NULL,
        nombre_completo NVARCHAR(120) NOT NULL,
        email NVARCHAR(120) NOT NULL,
        username NVARCHAR(50) NOT NULL,
        password_hash NVARCHAR(255) NOT NULL,
        telefono NVARCHAR(20) NULL,
        nivel_actual DECIMAL(5,2) NULL CONSTRAINT DF_usuarios_nivel DEFAULT 0.00,
        fecha_registro DATETIME2 NULL CONSTRAINT DF_usuarios_fecha DEFAULT GETDATE(),
        estado NVARCHAR(20) NULL CONSTRAINT DF_usuarios_estado DEFAULT N'activo',
        CONSTRAINT PK_usuarios PRIMARY KEY (id_usuario),
        CONSTRAINT UQ_usuarios_email UNIQUE (email),
        CONSTRAINT UQ_usuarios_username UNIQUE (username),
        CONSTRAINT CK_usuarios_estado CHECK (estado IN (N'activo', N'inactivo', N'bloqueado')),
        CONSTRAINT FK_usuarios_roles FOREIGN KEY (id_rol)
            REFERENCES dbo.roles(id_rol)
    );
END;
GO

/* 3. PLANES */
IF OBJECT_ID(N'dbo.planes', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.planes (
        id_plan INT IDENTITY(1,1) NOT NULL,
        nombre NVARCHAR(50) NOT NULL,
        precio_mensual DECIMAL(8,2) NOT NULL,
        limite_sesiones INT NULL,
        permite_analisis_corporal BIT NULL CONSTRAINT DF_planes_corporal DEFAULT 0,
        permite_analisis_tonal BIT NULL CONSTRAINT DF_planes_tonal DEFAULT 0,
        permite_feedback_humano BIT NULL CONSTRAINT DF_planes_feedback DEFAULT 0,
        CONSTRAINT PK_planes PRIMARY KEY (id_plan),
        CONSTRAINT CK_planes_precio CHECK (precio_mensual >= 0),
        CONSTRAINT CK_planes_limite CHECK (limite_sesiones IS NULL OR limite_sesiones >= 0)
    );
END;
GO

/* 4. SUSCRIPCIONES */
IF OBJECT_ID(N'dbo.suscripciones', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.suscripciones (
        id_suscripcion INT IDENTITY(1,1) NOT NULL,
        id_usuario INT NOT NULL,
        id_plan INT NOT NULL,
        fecha_inicio DATE NOT NULL,
        fecha_fin DATE NOT NULL,
        estado NVARCHAR(20) NULL CONSTRAINT DF_suscripciones_estado DEFAULT N'activa',
        CONSTRAINT PK_suscripciones PRIMARY KEY (id_suscripcion),
        CONSTRAINT CK_suscripciones_fechas CHECK (fecha_fin >= fecha_inicio),
        CONSTRAINT CK_suscripciones_estado CHECK (estado IN (N'activa', N'vencida', N'cancelada')),
        CONSTRAINT FK_suscripciones_usuarios FOREIGN KEY (id_usuario)
            REFERENCES dbo.usuarios(id_usuario),
        CONSTRAINT FK_suscripciones_planes FOREIGN KEY (id_plan)
            REFERENCES dbo.planes(id_plan)
    );
END;
GO

/* 5. CARRERAS */
IF OBJECT_ID(N'dbo.carreras', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.carreras (
        id_carrera INT IDENTITY(1,1) NOT NULL,
        nombre NVARCHAR(100) NOT NULL,
        descripcion NVARCHAR(MAX) NULL,
        activo BIT NULL CONSTRAINT DF_carreras_activo DEFAULT 1,
        CONSTRAINT PK_carreras PRIMARY KEY (id_carrera),
        CONSTRAINT UQ_carreras_nombre UNIQUE (nombre)
    );
END;
GO

/* 6. RELACION USUARIOS - CARRERAS */
IF OBJECT_ID(N'dbo.usuario_carreras', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.usuario_carreras (
        id_usuario INT NOT NULL,
        id_carrera INT NOT NULL,
        fecha_seleccion DATETIME2 NULL CONSTRAINT DF_usuario_carreras_fecha DEFAULT GETDATE(),
        CONSTRAINT PK_usuario_carreras PRIMARY KEY (id_usuario, id_carrera),
        CONSTRAINT FK_usuario_carreras_usuarios FOREIGN KEY (id_usuario)
            REFERENCES dbo.usuarios(id_usuario) ON DELETE CASCADE,
        CONSTRAINT FK_usuario_carreras_carreras FOREIGN KEY (id_carrera)
            REFERENCES dbo.carreras(id_carrera) ON DELETE CASCADE
    );
END;
GO

/* 7. SESIONES PROGRAMADAS */
IF OBJECT_ID(N'dbo.sesiones_programadas', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.sesiones_programadas (
        id_sesion INT IDENTITY(1,1) NOT NULL,
        id_usuario INT NOT NULL,
        id_carrera INT NULL,
        fecha_programada DATE NOT NULL,
        hora_programada TIME NOT NULL,
        estado NVARCHAR(20) NULL CONSTRAINT DF_sesiones_estado DEFAULT N'pendiente',
        recordatorio_enviado BIT NULL CONSTRAINT DF_sesiones_recordatorio DEFAULT 0,
        CONSTRAINT PK_sesiones_programadas PRIMARY KEY (id_sesion),
        CONSTRAINT CK_sesiones_estado CHECK (estado IN (N'pendiente', N'completada', N'cancelada')),
        CONSTRAINT FK_sesiones_usuarios FOREIGN KEY (id_usuario)
            REFERENCES dbo.usuarios(id_usuario),
        CONSTRAINT FK_sesiones_carreras FOREIGN KEY (id_carrera)
            REFERENCES dbo.carreras(id_carrera)
    );
END;
GO

/* 8. SIMULACIONES */
IF OBJECT_ID(N'dbo.simulaciones', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.simulaciones (
        id_simulacion INT IDENTITY(1,1) NOT NULL,
        id_usuario INT NOT NULL,
        id_carrera INT NOT NULL,
        id_sesion INT NULL,
        numero_simulacion INT NOT NULL,
        fecha_inicio DATETIME2 NOT NULL,
        fecha_fin DATETIME2 NULL,
        duracion_segundos INT NULL CONSTRAINT DF_simulaciones_duracion DEFAULT 0,
        estado NVARCHAR(20) NULL CONSTRAINT DF_simulaciones_estado DEFAULT N'grabando',
        CONSTRAINT PK_simulaciones PRIMARY KEY (id_simulacion),
        CONSTRAINT CK_simulaciones_numero CHECK (numero_simulacion > 0),
        CONSTRAINT CK_simulaciones_duracion CHECK (duracion_segundos >= 0),
        CONSTRAINT CK_simulaciones_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),
        CONSTRAINT CK_simulaciones_estado CHECK (estado IN (N'grabando', N'procesando', N'finalizada')),
        CONSTRAINT FK_simulaciones_usuarios FOREIGN KEY (id_usuario)
            REFERENCES dbo.usuarios(id_usuario),
        CONSTRAINT FK_simulaciones_carreras FOREIGN KEY (id_carrera)
            REFERENCES dbo.carreras(id_carrera),
        CONSTRAINT FK_simulaciones_sesiones FOREIGN KEY (id_sesion)
            REFERENCES dbo.sesiones_programadas(id_sesion)
    );
END;
GO

/* 9. EVALUACIONES */
IF OBJECT_ID(N'dbo.evaluaciones_simulacion', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.evaluaciones_simulacion (
        id_evaluacion INT IDENTITY(1,1) NOT NULL,
        id_simulacion INT NOT NULL,
        puntaje_general INT NOT NULL,
        nivel_comunicacion NVARCHAR(20) NOT NULL,
        tiempo_promedio_respuesta DECIMAL(4,1) NOT NULL,
        seguridad_calificacion DECIMAL(3,2) NOT NULL,
        feedback_resumen NVARCHAR(MAX) NULL,
        evaluado_por_especialista BIT NULL CONSTRAINT DF_evaluaciones_especialista DEFAULT 0,
        id_especialista INT NULL,
        CONSTRAINT PK_evaluaciones_simulacion PRIMARY KEY (id_evaluacion),
        CONSTRAINT UQ_evaluaciones_simulacion UNIQUE (id_simulacion),
        CONSTRAINT CK_evaluaciones_puntaje CHECK (puntaje_general BETWEEN 0 AND 100),
        CONSTRAINT CK_evaluaciones_nivel CHECK (nivel_comunicacion IN (N'Basico', N'Intermedio', N'Avanzado')),
        CONSTRAINT FK_evaluaciones_simulacion FOREIGN KEY (id_simulacion)
            REFERENCES dbo.simulaciones(id_simulacion) ON DELETE CASCADE,
        CONSTRAINT FK_evaluaciones_especialista FOREIGN KEY (id_especialista)
            REFERENCES dbo.usuarios(id_usuario)
    );
END;
GO

/* 10. ARCHIVOS DE LAS SIMULACIONES */
IF OBJECT_ID(N'dbo.archivos_simulacion', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.archivos_simulacion (
        id_archivo INT IDENTITY(1,1) NOT NULL,
        id_simulacion INT NOT NULL,
        tipo_archivo NVARCHAR(30) NOT NULL,
        url_almacenamiento NVARCHAR(255) NOT NULL,
        peso_mb DECIMAL(6,2) NULL,
        fecha_creacion DATETIME2 NULL CONSTRAINT DF_archivos_fecha DEFAULT GETDATE(),
        CONSTRAINT PK_archivos_simulacion PRIMARY KEY (id_archivo),
        CONSTRAINT CK_archivos_tipo CHECK (tipo_archivo IN (N'video_grabacion', N'audio', N'informe_pdf')),
        CONSTRAINT CK_archivos_peso CHECK (peso_mb IS NULL OR peso_mb >= 0),
        CONSTRAINT FK_archivos_simulaciones FOREIGN KEY (id_simulacion)
            REFERENCES dbo.simulaciones(id_simulacion) ON DELETE CASCADE
    );
END;
GO

/* 11. ENCUESTAS DE SATISFACCION */
IF OBJECT_ID(N'dbo.encuestas_satisfaccion', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.encuestas_satisfaccion (
        id_encuesta INT IDENTITY(1,1) NOT NULL,
        id_usuario INT NULL,
        satisfaccion_general TINYINT NOT NULL,
        calificacion_servicio NVARCHAR(20) NOT NULL,
        calidad_producto NVARCHAR(20) NOT NULL,
        comentario_adicional NVARCHAR(MAX) NULL,
        nombre_contacto NVARCHAR(100) NULL,
        correo_contacto NVARCHAR(120) NULL,
        fecha_envio DATETIME2 NULL CONSTRAINT DF_encuestas_fecha DEFAULT GETDATE(),
        CONSTRAINT PK_encuestas_satisfaccion PRIMARY KEY (id_encuesta),
        CONSTRAINT CK_encuestas_satisfaccion CHECK (satisfaccion_general BETWEEN 1 AND 5),
        CONSTRAINT CK_encuestas_servicio CHECK (calificacion_servicio IN (N'Excelente', N'Bueno', N'Regular', N'Malo')),
        CONSTRAINT CK_encuestas_producto CHECK (calidad_producto IN (N'Excelente', N'Bueno', N'Regular', N'Malo')),
        CONSTRAINT FK_encuestas_usuarios FOREIGN KEY (id_usuario)
            REFERENCES dbo.usuarios(id_usuario) ON DELETE SET NULL
    );
END;
GO

/* 12. PREGUNTAS DE ENTREVISTA */
IF OBJECT_ID(N'dbo.preguntas_entrevista', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.preguntas_entrevista (
        id_pregunta INT IDENTITY(1,1) NOT NULL,
        id_carrera INT NULL,
        texto_pregunta NVARCHAR(MAX) NOT NULL,
        categoria NVARCHAR(50) NULL,
        nivel_dificultad NVARCHAR(20) NULL,
        activo BIT NULL CONSTRAINT DF_preguntas_activo DEFAULT 1,
        fecha_creacion DATETIME2 NULL CONSTRAINT DF_preguntas_fecha DEFAULT GETDATE(),
        CONSTRAINT PK_preguntas_entrevista PRIMARY KEY (id_pregunta),
        CONSTRAINT CK_preguntas_dificultad CHECK (
            nivel_dificultad IS NULL OR nivel_dificultad IN (N'Basico', N'Intermedio', N'Avanzado')
        ),
        CONSTRAINT FK_preguntas_carreras FOREIGN KEY (id_carrera)
            REFERENCES dbo.carreras(id_carrera)
    );
END;
GO

/* 13. RESPUESTAS DE ENTREVISTA */
IF OBJECT_ID(N'dbo.respuestas_entrevista', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.respuestas_entrevista (
        id_respuesta INT IDENTITY(1,1) NOT NULL,
        id_simulacion INT NOT NULL,
        id_pregunta INT NOT NULL,
        id_archivo INT NULL,
        texto_respuesta NVARCHAR(MAX) NULL,
        duracion_segundos INT NULL,
        fecha_respuesta DATETIME2 NULL CONSTRAINT DF_respuestas_fecha DEFAULT GETDATE(),
        estado NVARCHAR(20) NULL CONSTRAINT DF_respuestas_estado DEFAULT N'enviada',
        CONSTRAINT PK_respuestas_entrevista PRIMARY KEY (id_respuesta),
        CONSTRAINT UQ_respuestas_simulacion_pregunta UNIQUE (id_simulacion, id_pregunta),
        CONSTRAINT CK_respuestas_duracion CHECK (duracion_segundos IS NULL OR duracion_segundos >= 0),
        CONSTRAINT CK_respuestas_estado CHECK (estado IN (N'borrador', N'enviada', N'revisada')),
        CONSTRAINT FK_respuestas_simulaciones FOREIGN KEY (id_simulacion)
            REFERENCES dbo.simulaciones(id_simulacion) ON DELETE CASCADE,
        CONSTRAINT FK_respuestas_preguntas FOREIGN KEY (id_pregunta)
            REFERENCES dbo.preguntas_entrevista(id_pregunta),
        -- Sin ON DELETE SET NULL para evitar multiple cascade paths en SQL Server.
        CONSTRAINT FK_respuestas_archivos FOREIGN KEY (id_archivo)
            REFERENCES dbo.archivos_simulacion(id_archivo)
    );
END;
GO

/* INDICES PARA LAS CLAVES FORANEAS Y CONSULTAS HABITUALES */
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_usuarios_id_rol' AND object_id = OBJECT_ID(N'dbo.usuarios'))
    CREATE INDEX IX_usuarios_id_rol ON dbo.usuarios(id_rol);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_suscripciones_id_usuario' AND object_id = OBJECT_ID(N'dbo.suscripciones'))
    CREATE INDEX IX_suscripciones_id_usuario ON dbo.suscripciones(id_usuario);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_simulaciones_id_usuario' AND object_id = OBJECT_ID(N'dbo.simulaciones'))
    CREATE INDEX IX_simulaciones_id_usuario ON dbo.simulaciones(id_usuario);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_archivos_id_simulacion' AND object_id = OBJECT_ID(N'dbo.archivos_simulacion'))
    CREATE INDEX IX_archivos_id_simulacion ON dbo.archivos_simulacion(id_simulacion);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_preguntas_id_carrera' AND object_id = OBJECT_ID(N'dbo.preguntas_entrevista'))
    CREATE INDEX IX_preguntas_id_carrera ON dbo.preguntas_entrevista(id_carrera);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_respuestas_id_simulacion' AND object_id = OBJECT_ID(N'dbo.respuestas_entrevista'))
    CREATE INDEX IX_respuestas_id_simulacion ON dbo.respuestas_entrevista(id_simulacion);
GO
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE name = N'IX_respuestas_id_pregunta' AND object_id = OBJECT_ID(N'dbo.respuestas_entrevista'))
    CREATE INDEX IX_respuestas_id_pregunta ON dbo.respuestas_entrevista(id_pregunta);
GO

/* CONSULTA DE VERIFICACION */
SELECT
    t.name AS tabla,
    COUNT(c.column_id) AS cantidad_columnas
FROM sys.tables t
INNER JOIN sys.columns c ON c.object_id = t.object_id
WHERE SCHEMA_NAME(t.schema_id) = N'dbo'
GROUP BY t.name
ORDER BY t.name;
GO
