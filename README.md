# MercadoLibre Mobile Candidate

Aplicación desarrollada como parte del challenge técnico, consumiendo la API de **Space Flight News** para listar artículos, ver su detalle y navegar entre pantallas con estado persistente.

## Objetivo

El foco principal de la implementación está en el módulo de **Reportes**.  
Los módulos de **Articles** y **Blogs** se agregaron reutilizando la misma base de UI, estado y flujo de navegación, ya que las respuestas de la API eran equivalentes y quedaban fuera del scope principal del desafío. La intención fue mantener consistencia visual y de comportamiento sin duplicar lógica innecesaria.

## Estructura del proyecto

### Raíz
- `MainActivity.kt`: punto de entrada de la app.
- `SpaceNewsApp.kt`: setup general de la aplicación.

### `data`
Capa de datos, responsable del acceso a red, modelos y repositorios.

- `models/`: modelos de dominio utilizados por la UI.
- `remote/`: cliente web service, constantes y DTOs de respuesta.
- `repository/`: abstracción y implementación del acceso a datos.

### `di`
- Configuración de dependencias (networking)

### `navigation`
- Manejo de navegación interna de la app.
- Estado de navegación por seccion y pantallas de detalle.

### `ui`
Contiene toda la interfaz de usuario.

- `articles/`: pantalla, estado y ViewModel de artículos.
- `blogs/`: variante reutilizando la misma lógica base.
- `reports/`: módulo principal del challenge.
- `articledetail/`: detalle del artículo y visualización webview.
- `components/`: componentes reutilizables de UI.
- `theme/`: colores, tipografía y tema general.

## Decisiones de navegación

No se utilizó `Navigation Component` porque la navegación principal de la app se resolvió mejor con un enfoque completamente controlado desde Compose, permitiendo:

- mantener el estado de cada pestaña,
- conservar la posición del scroll,
- manejar transiciones animadas entre listado, detalle y web view,
- simplificar la coordinación entre tabs sin perder estado al rotar la pantalla.

La navegación se implementó con `rememberSaveable`, `SharedTransitionLayout`, `AnimatedContent` y estados explícitos por sección.

## Estado y rotación
Se usan `ViewModel`, `StateFlow`, `rememberSaveable` y `LazyListState` para preservar el comportamiento de la interfaz.

## Manejo de errores

Se contemplan errores desde dos perspectivas:

- **Developer**: estructura consistente, separación por capas y manejo explícito de fallos de red o HTTP.
- **Usuario**: feedback visual mediante pantallas de error, loading y reintentos, evitando romper el flujo de navegación.

## Arquitectura

La app sigue una arquitectura simple por capas:

- **UI**: pantallas, componentes y estado visual.
- **ViewModel**: lógica de presentación y manejo de estado.
- **Repository**: acceso a datos.
- **Remote/Data**: consumo de API y mapeo de respuestas.

## Observación final

Aunque la app expone tres módulos visuales, el desarrollo está centrado en **Reports**, que representa el caso principal del desafío.  
Los módulos restantes se incluyeron para mostrar reutilización de componentes, consistencia de UX y escalabilidad de la solución.
