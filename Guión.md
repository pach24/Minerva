# Guion de defensa — Minerva

**Proyecto Final · 2º DAM · IES Los Montecillos**
Francisco Pacheco Gómez · Daniel Flores Jiménez

> Duración orientativa: **~14–17 min** de exposición (incluida la demo) + turno de preguntas.
> 15 diapositivas + **demo en vivo de ~2 min** (entre la slide 12 y la 13).
> Tiempo sugerido por bloque indicado entre paréntesis.
> Consejo: una idea por diapositiva. No leas la slide — la slide es el apoyo, tú eres el mensaje.

> **Antes de empezar la defensa:** haz una petición al servidor 2–3 min antes para **calentar Render** (P-02, cold start de 50–60 s) y ten el **CSV de ejemplo** y el **plan B grabado** ya abiertos en el escritorio.

---

## Diapositiva 1 · Portada *(~30 s)*

**Qué se ve:** logo, "Minerva.", el acrónimo (Modelo INteligente de Evaluación de Rendimiento y Valoración Académica), píldoras y vuestros nombres.

**Qué decir:**
- Buenos días. Somos Francisco Pacheco y Daniel Flores y presentamos **Minerva**, nuestro proyecto final de 2º DAM.
- Minerva es una plataforma que **predice el riesgo de fracaso académico con machine learning**, disponible en web y Android.
- *(Opcional)* El nombre es un acrónimo: Modelo Inteligente de Evaluación de Rendimiento y Valoración Académica.

---

## Diapositiva 2 · El problema *(~1 min)*

**Qué se ve:** tres tarjetas — seguimiento reactivo, datos dispersos, sin capacidad predictiva.

**Qué decir:**
- El problema de partida: **la detección del riesgo académico llega tarde.**
- Hoy el seguimiento es **reactivo** — cuando las notas parciales revelan el problema, el margen para intervenir ya es mínimo.
- Además los datos (calificaciones, asistencia, participación) están **dispersos**, sin un análisis conjunto.
- Y el profesorado **no tiene herramientas que anticipen** qué alumnos van a necesitar ayuda.

---

## Diapositiva 3 · La solución *(~1 min)*

**Qué se ve:** titular "Minerva anticipa el riesgo, el profesor decide", **QR de descarga de la app** y los cuatro niveles de riesgo (Bajo / Medio / Alto / Crítico).

**Qué decir:**
- Nuestra propuesta: **Minerva anticipa el riesgo y el profesor decide.** Apoya el criterio pedagógico, nunca lo sustituye.
- El modelo predice la **probabilidad de aprobado** de cada alumno y la traduce a un **nivel de riesgo accionable**: bajo, medio, alto o crítico.
- *(Señalar el QR)* La app Android está publicada — podéis escanear este **QR para descargarla** (Android 8.0+).

---

## Diapositiva 4 · Objetivos *(~45 s)*

**Qué se ve:** seis objetivos numerados.

**Qué decir:**
- Nos marcamos seis objetivos: un **modelo de predicción ML**, un **generador de datos sintéticos** basado en los RAs oficiales, **predicciones dinámicas** que se actualizan, una **interfaz comprensible** sin jerga, **métricas de patrones** y **acceso multiplataforma** web + Android sobre una misma API.

---

## Diapositiva 5 · Arquitectura *(~1 min)*

**Qué se ve:** diagrama "un backend, dos clientes" — Clientes → Backend (FastAPI) → Datos (Supabase) y el flujo inferior.

**Qué decir:**
- La arquitectura es sencilla: **un backend, dos clientes.**
- Una **API REST en FastAPI** (Python) sirve tanto a la web como a la app Android, con el modelo cacheado en memoria.
- Los datos y la autenticación viven en **Supabase**: PostgreSQL, Auth con JWT y Row Level Security.
- El flujo: el usuario hace login, obtiene un token, llama a `/api/predecir` y recibe la **predicción en menos de un segundo**, que se guarda en el histórico.

---

## Diapositiva 6 · Stack tecnológico *(~30 s)*

**Qué se ve:** seis tarjetas de tecnologías por capa.

**Qué decir:**
- Todo el stack es **open source y de coste cero**.
- Backend en Python/FastAPI, ML con scikit-learn, base de datos Supabase, web con HTML/CSS/JS + Jinja2, Android con Kotlin y Jetpack Compose, e infraestructura desplegada en Render con GitFlow.
- *(No leer chip a chip — basta una pasada visual.)*

---

## Diapositiva 7 · Datos sintéticos *(~1 min)*

**Qué se ve:** dos tarjetas (privacidad / 1.000 alumnos) y la regla de etiquetado.

**Qué decir:**
- Una decisión de diseño clave: usamos **datos sintéticos**.
- **Privacidad primero:** los datos reales son datos sensibles de menores, con todo lo que implica el RGPD. Los sintéticos dejan el proyecto fuera del ámbito de la norma.
- Generamos **1.000 alumnos con NumPy**, reproducibles con semilla fija, con correlaciones realistas y ~30 % de valores ausentes para simular cuestionarios sin responder.
- Los etiquetamos según la **normativa de FP** (≥5 de media + ≥75 % asistencia + ≥60 % de RAs superados). **Importante: el modelo nunca ve esta regla.**

---

## Diapositiva 8 · Modelo ML *(~1 min)*

**Qué se ve:** pipeline de 5 pasos a la izquierda y las 23 features agrupadas a la derecha.

**Qué decir:**
- El modelo es un **RandomForest sobre 23 características**.
- El pipeline: generación sintética → preprocesado (split estratificado, imputación por mediana, escalado) → optimización con GridSearchCV y validación cruzada → **entrenamiento dual** (RandomForest + SGDClassifier) → autoevaluación PASS/FAIL.
- Las 23 features se agrupan en notas de RA, académicas base, derivadas calculadas y contexto opcional.
- Detalle interesante: el **SGDClassifier permite actualizar el modelo de forma incremental** con `partial_fit`, sin reentrenar desde cero.

---

## Diapositiva 9 · Métricas *(~1 min)*

**Qué se ve:** tres stats grandes (99,5 % accuracy, F1 0,985, Kappa 0,985), gráfico de importancia de características y el hueco para tu gráfico.

**Qué decir:**
- Resultados: **99,5 % de accuracy**, F1 de 0,985 en validación cruzada y un Kappa de Cohen de 0,985 — acuerdo casi perfecto.
- Lo más relevante es **qué aprendió**: la variable más determinante es la **asistencia**, justo el corte del 75 % que el modelo nunca vio. Esto **valida que el pipeline aprende la regla por sí solo.**
- *(Honestidad)* Son métricas sobre datos sintéticos; con datos reales esperamos un 75–85 %.

---

## Diapositiva 10 · Polarización (P-06) *(~1 min)*

**Qué se ve:** histograma en "U" (probabilidades concentradas en los extremos) y comparación datos sintéticos uniformes vs datos reales en campana.

**Qué decir:**
- Una pregunta honesta que nos hizo el propio modelo: **¿por qué nunca duda?**
- Las probabilidades salen polarizadas — casi siempre **≈0 % o ≈100 %**, con la zona intermedia vacía.
- La causa: los **datos sintéticos reparten las notas de forma uniforme**, sin "casos frontera". Sin ambigüedad, el RandomForest separa las clases sin esfuerzo.
- Con **datos reales** las notas formarían una **campana en torno al 5** y al límite del 75 % de asistencia: esos casos ambiguos obligarán al modelo a dudar y **calibrarán las probabilidades** (mejora M-04).

---

## Diapositiva 11 · Aplicación web *(~45 s)*

**Qué se ve:** ventana de navegador con la captura del dashboard.

**Qué decir:**
- El **primer cliente es la web.**
- El profesor **carga un CSV** en el navegador — el parser tolera distintos formatos de columnas y de asistencia.
- Se **predice todo el grupo** de golpe, con tabla coloreada por riesgo, y se puede abrir el **detalle por alumno** con el desglose de los 9 RAs.
- Flujo completo en menos de 3 pasos y diseño responsive.

---

## Diapositiva 12 · App Android *(~45 s)*

**Qué se ve:** dos capturas de móvil de la app nativa.

**Qué decir:**
- El **segundo cliente es la app Android nativa.**
- Construida con **Clean Architecture + MVVM**, Kotlin 2.0, Jetpack Compose y Material 3.
- Cuida la **resiliencia de red**: renueva el token automáticamente y maneja el cold start del servidor.
- Está **publicada como v0.1.0-alpha** en GitHub Releases — el APK que se descarga desde el QR de antes.

---

## ▶ DEMO EN VIVO *(~1,5–2 min · máx. 2 min)*

> **Cuándo:** justo aquí, después de enseñar los dos clientes (slide 12) y antes de hablar de los problemas (slide 13). Cambia de la presentación a la pantalla de la app web.

**Flujo a demostrar (un único recorrido limpio, sin improvisar):**
1. **Login** *(~15 s)* — el servidor ya está caliente, entra directo.
2. **Cargar el CSV de ejemplo** *(~30 s)* — que aparezca la predicción de todo el grupo, con la tabla coloreada por riesgo.
3. **Abrir un alumno crítico** *(~30 s)* — enseñar el desglose de los 9 RAs, asistencia y observaciones.
4. *(Opcional, si hay tiempo y red)* enseñar la **app Android** abriendo la misma predicción en el móvil *(~30 s)*.

**Si algo va lento o falla:** no esperes — pasa al **plan B** (vídeo/capturas) y sigue. "Os enseño el flujo grabado para no perder tiempo."

---

## Diapositiva 13 · Problemas encontrados *(~1 min)*

**Qué se ve:** tres problemas con su decisión (P-06 polarización, P-02 cold start, P-01 NLP básico).

**Qué decir:**
- No queremos esconder los problemas: cada uno es **una decisión documentada.**
- **P-06:** la polarización que acabamos de ver — se calibrará con datos reales.
- **P-02:** el plan gratuito de Render duerme el servidor, así que la primera petición tarda hasta un minuto. Lo resolvimos con timeouts holgados e indicador de carga.
- **P-01:** el análisis del feedback es por palabras clave y genera falsos neutros; lo aceptamos por rendimiento y planeamos migrarlo a transformers.

---

## Diapositiva 14 · Roadmap *(~45 s)*

**Qué se ve:** tres columnas de versiones (v0.2.0, v0.3.0, v1.0.0).

**Qué decir:**
- Tenemos una **hoja de ruta priorizada** en tres hitos.
- **v0.2.0:** informes PDF/CSV, tests y documentación.
- **v0.3.0:** explicabilidad con SHAP/LIME, integración con Séneca/Moodle y evolución temporal.
- **v1.0.0:** el gran paso — **validación con datos reales**, publicación en Google Play y dashboard de administrador.

---

## Diapositiva 15 · Cierre *(~30 s)*

**Qué se ve:** "Listo para el piloto.", enlace al repositorio y "Gracias. ¿Preguntas?".

**Qué decir:**
- En resumen: un sistema **funcional, seguro y documentado**, listo para validarse con datos reales en un centro.
- La predicción **apoya al docente; la decisión sigue siendo suya.**
- **Gracias. ¿Tenéis alguna pregunta?**

---

## Preguntas probables del tribunal — respuestas rápidas

- **¿Por qué 99,5 % de accuracy? ¿No es sobreajuste?** → Es alto porque los datos sintéticos son separables (sin casos frontera). No es overfitting clásico: lo confirma la validación cruzada estable (±0,009). Con datos reales bajará a un rango realista de 75–85 %.
- **¿Por qué no usasteis datos reales?** → Por privacidad: son datos sensibles de menores (RGPD/LOPDGDD), fuera del alcance de un proyecto académico. Lo dejamos preparado para integrarlos (M-04).
- **¿Cómo se actualiza el modelo?** → El SGDClassifier admite `partial_fit`, aprendizaje incremental sin reentrenar todo.
- **¿Reemplaza al profesor?** → No. Da una probabilidad y un nivel de riesgo; la decisión pedagógica es siempre del docente.
- **¿Por qué FastAPI / Supabase / Render?** → Open source, coste cero y rápidos de desplegar para un MVP. La limitación conocida es el cold start del plan gratuito (P-02).
