# Planeta Noticias — MercadoLibre Mobile Challenge

App Android desarrollada como parte del challenge técnico, consumiendo la
[Space Flight News API](https://api.spaceflightnewsapi.net/v4/docs/) para
listar y explorar noticias de vuelos espaciales.

## Alcance

El módulo principal del desafío es **Reportes**. Los módulos de **Artículos**
y **Blogs** se incluyeron reutilizando la misma base de UI, lógica de estado
y flujo de navegación, dado que los endpoints de la API devuelven estructuras
equivalentes. La decisión fue mostrar reutilización de componentes y
consistencia de UX sin duplicar lógica.

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Arquitectura | MVVM + Repository pattern |
| Inyección de dependencias | Hilt |
| Networking | Retrofit + OkHttp |
| Imágenes | Coil |
| Navegación | Custom state-based (ver decisiones) |
| Async | Coroutines + StateFlow |

---

## Funcionalidades

- **Listado con paginación**: carga incremental de 10 artículos con infinite
  scroll, activado al acercarse al final de la lista.
- **Búsqueda con debounce**: el campo de búsqueda espera 500ms de inactividad
  antes de lanzar la petición, evitando requests innecesarios.
- **Shared element transitions**: la imagen del artículo se anima entre el
  card en el listado y el header del detalle.
- **Precarga de imagen**: al tocar un card se precarga la imagen antes de
  navegar, eliminando el blink en la transición la primera vez.
- **Estado persistente ante rotación**: posición de scroll, estado de búsqueda
  y pestaña activa se preservan con `rememberSaveable` y `ViewModel`.
- **WebView integrado**: el artículo completo se abre en un WebView interno
  con indicador de progreso de carga.
- **Redes sociales de autores**: se muestran hasta 3 badges directamente; el
  resto se agrupa en un "+N" que abre un diálogo con la lista completa.

---

## Arquitectura
UI (Compose) → ViewModel → Repository → Remote (Retrofit)

- **UI**: pantallas y componentes declarativos, sin lógica de negocio.
- **ViewModel**: expone `StateFlow` con estados sellados (`Loading`, `Success`,
  `Error`). Gestiona paginación y búsqueda.
- **Repository**: abstracción sobre el origen de datos; la implementación
  maneja y loguea excepciones de red o HTTP antes de retornar un `Result`.
- **Remote**: DTOs separados de los modelos de dominio; el mapeo ocurre en la
  capa de datos, sin contaminar la UI.

---

## Navegación

No se utilizó Navigation Component. La navegación se resolvió con un enfoque
state-based controlado desde Compose, lo que permitió:

- Mantener el estado de cada pestaña de forma independiente.
- Conservar la posición del scroll al volver del detalle.
- Coordinar `SharedTransitionLayout` con `AnimatedContent` para las
  transiciones animadas entre pantallas.
- Evitar serialización del modelo `Article` entre destinos (se pasa el objeto
  directamente en memoria).

Cada sección maneja su propio `ScreenNavState` (`List`, `Detail`, `Web`) y
el `BackHandler` global despacha según la pestaña activa.

---

## Componentes destacados

### `StarBackground`
Fondo animado de 150 estrellas generadas con posiciones aleatorias usando
`Canvas`. Las posiciones se calculan una sola vez con `remember` para no
regenerarse en cada recomposición. Incluye una animación de "twinkle"
preparada pero desactivada por ahora, que hace parpadear las estrellas con
una transición infinita de alpha.

### `HeaderSearch`
Barra superior que alterna entre el título de la app y un campo de búsqueda
con `AnimatedContent`. Al activarse, solicita el foco automáticamente con
`FocusRequester`. Al cerrarse, limpia la query y dispara un nuevo fetch.

### `ArticleList`
Lista lazy con detección de scroll para paginación. Usa `derivedStateOf` para
calcular si el último ítem visible está a 3 posiciones del final, minimizando
recomposiciones. El spinner de carga aparece como ítem al final de la lista
sin reemplazar el contenido existente.

### `ArticleCard`
Card con imagen superior y shared element transition coordinada con el detalle.
Al hacer tap, encola la carga de la imagen con Coil antes de navegar, para que
la transición animada no muestre un frame en blanco la primera vez.

### `SocialMediaBadge` / `MoreSocialsBadge`
El detalle del artículo muestra los perfiles sociales de los autores como
badges circulares coloreados por red (X, YouTube, Instagram, LinkedIn,
Mastodon, Bluesky). Si hay más de 3, el excedente se colapsa en un badge "+N"
que abre un `AlertDialog` con la lista completa, cada ítem clickeable hacia
el perfil externo.

### `DetailWebViewScreen`
WebView con `LinearProgressIndicator` que refleja el progreso real de carga
de la página via `WebChromeClient.onProgressChanged`. El botón de back flota
sobre el contenido con fondo semitransparente para mantener visibilidad
independientemente del color de la página cargada.

---

## Manejo de errores

**Developer**
- Errores de red y HTTP se capturan en el `Repository`, se loguean con
  `Log.e` y se retornan como `Result.failure`.
- El `ViewModel` convierte el `Throwable` en un mensaje legible para la UI,
  manteniendo la separación de capas.
- En `loadMore`, un fallo no borra la lista existente; se restaura el estado
  anterior y se loguea silenciosamente.

**Usuario**
- Estado `Loading` con indicador de progreso al inicio y entre páginas.
- Estado `Error` con mensaje descriptivo y botón de reintento.
- Imagen de placeholder y fallback en todos los `AsyncImage`.
- El WebView muestra un `LinearProgressIndicator` mientras carga la página.

---

## Estructura del proyecto

~~~~
├── MainActivity.kt
├── SpaceNewsApp.kt
├── data/
│   ├── models/          # Modelos de dominio
│   ├── remote/          # WebService, DTOs, constantes
│   └── repository/      # Interfaz e implementación
├── di/                  # Módulo de red (Hilt)
├── navigation/          # AppNavigation, ScreenNavState
└── ui/
    ├── articles/        # Pantalla, ViewModel, Estado
    ├── blogs/
    ├── reports/
    ├── articledetail/   # Detalle + WebView
    ├── components/      # Componentes reutilizables
    └── theme/
~~~~

---

## Nota sobre modelos compartidos

`Blogs` y `Reports` reutilizan el modelo `Article` y el mismo `Repository`
que `Articles`, ya que las respuestas de la API son estructuralmente idénticas.
Lo correcto en un proyecto productivo sería tener modelos de dominio separados
por entidad. Esto está documentado como deuda técnica en los comentarios del
repositorio.
