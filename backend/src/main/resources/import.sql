-- Datos iniciales de JobReady para PostgreSQL.
-- El script es idempotente: puede ejecutarse en cada arranque sin duplicar registros.

INSERT INTO roles (nombre, descripcion)
VALUES
    ('ADMIN', 'Administra usuarios, roles y preguntas'),
    ('CANDIDATO', 'Realiza simulaciones de entrevistas'),
    ('EVALUADOR', 'Revisa simulaciones y registra feedback')
ON CONFLICT (nombre) DO NOTHING;

-- Los tres usuarios usan inicialmente la clave: Admin123*
-- El valor almacenado es un hash BCrypt, nunca la clave en texto plano.
INSERT INTO usuarios
    (id_rol, nombre_completo, email, username, password_hash, nivel_actual, fecha_registro, estado)
SELECT id_rol, 'Administrador JobReady', 'admin@jobready.com', 'admin',
       '$2a$10$4khQk.sSVNCIdUkF/qiGuehOBz22uuIbKpogIrhcVduZ/HJC2qRHq',
       0.00, CURRENT_TIMESTAMP, 'activo'
FROM roles
WHERE nombre = 'ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO usuarios
    (id_rol, nombre_completo, email, username, password_hash, nivel_actual, fecha_registro, estado)
SELECT id_rol, 'Candidato de Prueba', 'candidato@jobready.com', 'candidato',
       '$2a$10$4khQk.sSVNCIdUkF/qiGuehOBz22uuIbKpogIrhcVduZ/HJC2qRHq',
       0.00, CURRENT_TIMESTAMP, 'activo'
FROM roles
WHERE nombre = 'CANDIDATO'
ON CONFLICT DO NOTHING;

INSERT INTO usuarios
    (id_rol, nombre_completo, email, username, password_hash, nivel_actual, fecha_registro, estado)
SELECT id_rol, 'Evaluador de Prueba', 'evaluador@jobready.com', 'evaluador',
       '$2a$10$4khQk.sSVNCIdUkF/qiGuehOBz22uuIbKpogIrhcVduZ/HJC2qRHq',
       0.00, CURRENT_TIMESTAMP, 'activo'
FROM roles
WHERE nombre = 'EVALUADOR'
ON CONFLICT DO NOTHING;

-- Datos de demostracion: el flujo candidato -> preguntas -> respuestas -> evaluacion.
-- Las inserciones usan claves naturales y NOT EXISTS para no duplicar datos al reiniciar.
INSERT INTO carreras (nombre, descripcion, activo)
VALUES (
    'Ingenieria de Software',
    'Carrera de demostracion para probar el flujo de entrevistas de JobReady.',
    TRUE
)
ON CONFLICT (nombre) DO NOTHING;

-- Catalogo de carreras basado en las areas profesionales con demanda laboral
-- identificadas por el MTPE en sus proyecciones 2025-2026, mas carreras comunes.
INSERT INTO carreras (nombre, descripcion, activo)
VALUES
    ('Sistemas y Computo', 'Desarrollo de software, sistemas, redes y soluciones informaticas.', TRUE),
    ('Gestion y Administracion', 'Organizacion de empresas, operaciones y gestion de equipos.', TRUE),
    ('Contabilidad e Impuestos', 'Registro contable, analisis financiero y cumplimiento tributario.', TRUE),
    ('Construccion e Ingenieria Civil', 'Diseno, ejecucion y supervision de obras e infraestructura.', TRUE),
    ('Ciencias de la Tierra', 'Analisis de recursos naturales, geologia y territorio.', TRUE),
    ('Industria y Productos Transformados', 'Procesos de manufactura, produccion y control de calidad.', TRUE),
    ('Mecanica y Metalurgica', 'Diseno, mantenimiento y procesos de equipos y materiales.', TRUE),
    ('Educacion Primaria', 'Ensenanza y acompanamiento del aprendizaje escolar.', TRUE),
    ('Gestion y Calidad Educativa', 'Planificacion, gestion y mejora de servicios educativos.', TRUE),
    ('Electricidad y Energia', 'Instalaciones, sistemas electricos y gestion energetica.', TRUE),
    ('Ingenieria Industrial y de Procesos', 'Optimizacion de operaciones, calidad y productividad.', TRUE),
    ('Ciencias de la Computacion', 'Algoritmos, programacion, datos y fundamentos computacionales.', TRUE),
    ('Economia', 'Analisis de mercados, indicadores y decisiones economicas.', TRUE),
    ('Arquitectura y Urbanismo', 'Diseno de espacios, edificaciones y planificacion urbana.', TRUE),
    ('Marketing y Publicidad', 'Investigacion de mercados, comunicacion y estrategias comerciales.', TRUE),
    ('Enfermeria', 'Atencion integral, prevencion y cuidado de pacientes.', TRUE),
    ('Hoteleria, Restaurantes y Gastronomia', 'Gestion de servicios hoteleros, restaurantes y alimentos.', TRUE),
    ('Salud y Proteccion Laboral', 'Prevencion de riesgos y seguridad en el trabajo.', TRUE),
    ('Medicina Humana', 'Evaluacion, prevencion y atencion de la salud.', TRUE),
    ('Psicologia', 'Evaluacion y acompanamiento de procesos psicologicos.', TRUE),
    ('Derecho', 'Analisis juridico, asesoramiento y resolucion de casos.', TRUE)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO usuario_carreras (id_usuario, id_carrera, fecha_seleccion)
SELECT u.id_usuario, c.id_carrera, CURRENT_TIMESTAMP
FROM usuarios u
CROSS JOIN carreras c
WHERE u.username = 'candidato'
  AND c.nombre IN (
      'Ingenieria de Software', 'Sistemas y Computo', 'Gestion y Administracion',
      'Contabilidad e Impuestos', 'Construccion e Ingenieria Civil', 'Ciencias de la Tierra',
      'Industria y Productos Transformados', 'Mecanica y Metalurgica', 'Educacion Primaria',
      'Gestion y Calidad Educativa', 'Electricidad y Energia', 'Ingenieria Industrial y de Procesos',
      'Ciencias de la Computacion', 'Economia', 'Arquitectura y Urbanismo', 'Marketing y Publicidad',
      'Enfermeria', 'Hoteleria, Restaurantes y Gastronomia', 'Salud y Proteccion Laboral',
      'Medicina Humana', 'Psicologia', 'Derecho'
  )
ON CONFLICT (id_usuario, id_carrera) DO NOTHING;

INSERT INTO preguntas_entrevista
    (id_carrera, texto_pregunta, categoria, nivel_dificultad, activo, fecha_creacion)
SELECT c.id_carrera,
       'Describe un proyecto de software en el que resolviste un problema complejo.',
       'Tecnica', 'Intermedio', TRUE, CURRENT_TIMESTAMP
FROM carreras c
WHERE c.nombre = 'Ingenieria de Software'
  AND NOT EXISTS (
      SELECT 1
      FROM preguntas_entrevista p
      WHERE p.id_carrera = c.id_carrera
        AND p.texto_pregunta = 'Describe un proyecto de software en el que resolviste un problema complejo.'
  );

INSERT INTO preguntas_entrevista
    (id_carrera, texto_pregunta, categoria, nivel_dificultad, activo, fecha_creacion)
SELECT c.id_carrera,
       'Como priorizas tareas cuando tienes varias entregas con plazos cercanos?',
       'Comunicacion', 'Basico', TRUE, CURRENT_TIMESTAMP
FROM carreras c
WHERE c.nombre = 'Ingenieria de Software'
  AND NOT EXISTS (
      SELECT 1
      FROM preguntas_entrevista p
      WHERE p.id_carrera = c.id_carrera
        AND p.texto_pregunta = 'Como priorizas tareas cuando tienes varias entregas con plazos cercanos?'
  );

-- Dos preguntas de muestra por carrera para probar filtros y consultas en Swagger.
INSERT INTO preguntas_entrevista
    (id_carrera, texto_pregunta, categoria, nivel_dificultad, activo, fecha_creacion)
SELECT c.id_carrera, q.texto_pregunta, q.categoria, q.nivel_dificultad, TRUE, CURRENT_TIMESTAMP
FROM carreras c
CROSS JOIN LATERAL (
    VALUES
        ('Describe un reto relacionado con ' || c.nombre || ' y como lo resolverias.', 'Experiencia', 'Intermedio'),
        ('Que herramientas o conocimientos son importantes en ' || c.nombre || ' y por que?', 'Tecnica', 'Basico')
) AS q(texto_pregunta, categoria, nivel_dificultad)
WHERE c.nombre IN (
      'Ingenieria de Software', 'Sistemas y Computo', 'Gestion y Administracion',
      'Contabilidad e Impuestos', 'Construccion e Ingenieria Civil', 'Ciencias de la Tierra',
      'Industria y Productos Transformados', 'Mecanica y Metalurgica', 'Educacion Primaria',
      'Gestion y Calidad Educativa', 'Electricidad y Energia', 'Ingenieria Industrial y de Procesos',
      'Ciencias de la Computacion', 'Economia', 'Arquitectura y Urbanismo', 'Marketing y Publicidad',
      'Enfermeria', 'Hoteleria, Restaurantes y Gastronomia', 'Salud y Proteccion Laboral',
      'Medicina Humana', 'Psicologia', 'Derecho'
  )
  AND NOT EXISTS (
      SELECT 1
      FROM preguntas_entrevista p
      WHERE p.id_carrera = c.id_carrera
        AND p.texto_pregunta = q.texto_pregunta
  );

INSERT INTO simulaciones
    (id_usuario, id_carrera, numero_simulacion, fecha_inicio, fecha_fin,
     duracion_segundos, estado)
SELECT u.id_usuario, c.id_carrera, 1,
       CURRENT_TIMESTAMP - INTERVAL '15 minutes',
       CURRENT_TIMESTAMP - INTERVAL '5 minutes',
       600, 'finalizada'
FROM usuarios u
CROSS JOIN carreras c
WHERE u.username = 'candidato'
  AND c.nombre = 'Ingenieria de Software'
  AND NOT EXISTS (
      SELECT 1
      FROM simulaciones s
      WHERE s.id_usuario = u.id_usuario
        AND s.id_carrera = c.id_carrera
        AND s.numero_simulacion = 1
  );

-- Simulaciones adicionales: dos finalizadas y una iniciada, para consultas de estados.
INSERT INTO simulaciones
    (id_usuario, id_carrera, numero_simulacion, fecha_inicio, fecha_fin,
     duracion_segundos, estado)
SELECT u.id_usuario, c.id_carrera, 1,
       CURRENT_TIMESTAMP - INTERVAL '20 minutes',
       CASE WHEN c.nombre = 'Contabilidad e Impuestos' THEN NULL
            ELSE CURRENT_TIMESTAMP - INTERVAL '10 minutes' END,
       CASE WHEN c.nombre = 'Contabilidad e Impuestos' THEN NULL ELSE 540 END,
       CASE WHEN c.nombre = 'Contabilidad e Impuestos' THEN 'iniciada' ELSE 'finalizada' END
FROM usuarios u
CROSS JOIN carreras c
WHERE u.username = 'candidato'
  AND c.nombre IN ('Sistemas y Computo', 'Gestion y Administracion', 'Contabilidad e Impuestos')
  AND NOT EXISTS (
      SELECT 1
      FROM simulaciones s
      WHERE s.id_usuario = u.id_usuario
        AND s.id_carrera = c.id_carrera
        AND s.numero_simulacion = 1
  );

INSERT INTO respuestas_entrevista
    (id_simulacion, id_pregunta, texto_respuesta, duracion_segundos,
     fecha_respuesta, estado)
SELECT s.id_simulacion, p.id_pregunta,
       'En un proyecto universitario detecte consultas repetidas a la base de datos. Revise el flujo, optimice la consulta y medi la mejora con pruebas.',
       95, CURRENT_TIMESTAMP - INTERVAL '10 minutes', 'revisada'
FROM simulaciones s
JOIN usuarios u ON u.id_usuario = s.id_usuario
JOIN carreras c ON c.id_carrera = s.id_carrera
JOIN preguntas_entrevista p ON p.id_carrera = c.id_carrera
WHERE u.username = 'candidato'
  AND c.nombre = 'Ingenieria de Software'
  AND s.numero_simulacion = 1
  AND p.texto_pregunta = 'Describe un proyecto de software en el que resolviste un problema complejo.'
ON CONFLICT (id_simulacion, id_pregunta) DO NOTHING;

INSERT INTO respuestas_entrevista
    (id_simulacion, id_pregunta, texto_respuesta, duracion_segundos,
     fecha_respuesta, estado)
SELECT s.id_simulacion, p.id_pregunta,
       'Ordeno las tareas por impacto y fecha limite, estimo el esfuerzo y comunico con anticipacion cualquier riesgo de retraso.',
       72, CURRENT_TIMESTAMP - INTERVAL '8 minutes', 'revisada'
FROM simulaciones s
JOIN usuarios u ON u.id_usuario = s.id_usuario
JOIN carreras c ON c.id_carrera = s.id_carrera
JOIN preguntas_entrevista p ON p.id_carrera = c.id_carrera
WHERE u.username = 'candidato'
  AND c.nombre = 'Ingenieria de Software'
  AND s.numero_simulacion = 1
  AND p.texto_pregunta = 'Como priorizas tareas cuando tienes varias entregas con plazos cercanos?'
ON CONFLICT (id_simulacion, id_pregunta) DO NOTHING;

-- Respuestas sinteticas de las simulaciones adicionales finalizadas.
INSERT INTO respuestas_entrevista
    (id_simulacion, id_pregunta, texto_respuesta, duracion_segundos,
     fecha_respuesta, estado)
SELECT s.id_simulacion, p.id_pregunta,
       CASE
           WHEN c.nombre = 'Sistemas y Computo' AND p.categoria = 'Experiencia'
               THEN 'Delimite el problema y los usuarios afectados, compare alternativas y valide la solucion con pruebas de rendimiento.'
           WHEN c.nombre = 'Sistemas y Computo'
               THEN 'Elegiria herramientas segun seguridad, rendimiento y mantenimiento, y documentaria las pruebas realizadas.'
           WHEN p.categoria = 'Experiencia'
               THEN 'Priorice las tareas por impacto, responsables y plazo, y di seguimiento con indicadores para ajustar recursos.'
           ELSE 'Usaria indicadores de costo, tiempo y calidad para comparar alternativas y justificar la decision.'
       END,
       CASE WHEN p.categoria = 'Experiencia' THEN 110 ELSE 95 END,
       CURRENT_TIMESTAMP - INTERVAL '12 minutes', 'revisada'
FROM simulaciones s
JOIN usuarios u ON u.id_usuario = s.id_usuario
JOIN carreras c ON c.id_carrera = s.id_carrera
JOIN preguntas_entrevista p ON p.id_carrera = c.id_carrera
WHERE u.username = 'candidato'
  AND c.nombre IN ('Sistemas y Computo', 'Gestion y Administracion')
  AND s.numero_simulacion = 1
  AND s.estado = 'finalizada'
  AND p.categoria IN ('Experiencia', 'Tecnica')
  AND p.texto_pregunta IN (
      'Describe un reto relacionado con ' || c.nombre || ' y como lo resolverias.',
      'Que herramientas o conocimientos son importantes en ' || c.nombre || ' y por que?'
  )
ON CONFLICT (id_simulacion, id_pregunta) DO NOTHING;

INSERT INTO evaluaciones_simulacion
    (id_simulacion, puntaje_general, nivel_comunicacion,
     tiempo_promedio_respuesta, seguridad_calificacion, feedback_resumen,
     evaluado_por_especialista, id_especialista)
SELECT s.id_simulacion, 86, 'Intermedio', 83.5, 0.88,
       'Demuestra razonamiento ordenado y comunica decisiones con claridad. Puede fortalecer la explicacion del impacto medible de sus soluciones.',
       TRUE, e.id_usuario
FROM simulaciones s
JOIN usuarios c ON c.id_usuario = s.id_usuario
JOIN carreras ca ON ca.id_carrera = s.id_carrera
JOIN usuarios e ON e.username = 'evaluador'
WHERE c.username = 'candidato'
  AND ca.nombre = 'Ingenieria de Software'
  AND s.numero_simulacion = 1
  AND NOT EXISTS (
      SELECT 1
      FROM evaluaciones_simulacion ev
      WHERE ev.id_simulacion = s.id_simulacion
  );

-- Evaluaciones sinteticas para las dos simulaciones adicionales finalizadas.
INSERT INTO evaluaciones_simulacion
    (id_simulacion, puntaje_general, nivel_comunicacion,
     tiempo_promedio_respuesta, seguridad_calificacion, feedback_resumen,
     evaluado_por_especialista, id_especialista)
SELECT s.id_simulacion,
       CASE WHEN ca.nombre = 'Sistemas y Computo' THEN 92 ELSE 88 END,
       CASE WHEN ca.nombre = 'Sistemas y Computo' THEN 'Avanzado' ELSE 'Intermedio' END,
       102.5,
       CASE WHEN ca.nombre = 'Sistemas y Computo' THEN 0.91 ELSE 0.86 END,
       CASE WHEN ca.nombre = 'Sistemas y Computo'
            THEN 'Explica con orden y propone validar sus decisiones con pruebas. Puede profundizar en seguridad y mantenimiento.'
            ELSE 'Prioriza objetivos y comunica criterios de gestion. Puede ampliar el seguimiento con indicadores.' END,
       TRUE, e.id_usuario
FROM simulaciones s
JOIN usuarios c ON c.id_usuario = s.id_usuario
JOIN carreras ca ON ca.id_carrera = s.id_carrera
CROSS JOIN usuarios e
WHERE c.username = 'candidato'
  AND e.username = 'evaluador'
  AND ca.nombre IN ('Sistemas y Computo', 'Gestion y Administracion')
  AND s.numero_simulacion = 1
  AND s.estado = 'finalizada'
  AND NOT EXISTS (
      SELECT 1
      FROM evaluaciones_simulacion ev
      WHERE ev.id_simulacion = s.id_simulacion
  );
