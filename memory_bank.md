# Memory Bank - artefacto Android-Kotlin: BiblioRed

Este documento recoge de forma estructurada los componentes centrales del artefacto Android-Kotlin del proyecto BiblioRed, con foco en modelado de datos, interacción entre capas y flujos de negocio clave.

## Resumen ejecutivo
- Propósito: Proporcionar una referencia técnica consolidada para el artefacto Android-Kotlin, facilitando mantenimiento, onboarding y futuras evoluciones.
- Alcance: Modelos de dominio, capa de datos y red, DTOs/adapters, capas de presentación (UI), ViewModels, persistencia de sesión y flujos de negocio relevantes.
- Público objetivo: Desarrolladores actuales y futuros del proyecto, QA y equipo de producto.

## Arquitectura y capas
- Capas principales:
  - Dominio (modelos de datos): clases data Kotlin que representan entidades del dominio (Autor, Libro, PortadaUrl, Usuario, Session).
  - Datos y red: repositorios, servicios y proveedores (API, Retrofit), mapeadores y DTOs.
  - Presentación (UI): pantallas, navegación y componentes de UI.
  - Lógica de negocio y estado: ViewModels (AuthViewModel, LibraryViewModel, LibroViewModel).
  - Persistencia de sesión y preferencias: SessionPrefs.
- Patrones clave:
  - Data classes para modelos de dominio.
  - Repositorio-Provider para abstracciones de datos y red.
  - Mapeadores entre DTOs/entidades cuando aplique.
  - Gestión de sesión basada en preferencias locales.

## Modelos de dominio (clases principales)
A continuación se listan las entidades fundamentales con su firma y significado.

- Autor
  - FQCN: com.example.bibliored.model.Autor
  - Campos:
    - id: Long? = 0
    - nombre: String
  - Descripción: Representa un autor de libro. Un Libro puede tener múltiples autores.

- Libro
  - FQCN: com.example.bibliored.model.Libro
  - Campos:
    - isbn10: String?
    - isbn13: String?
    - titulo: String
    - autores: List<Autor>
    - descripcion: String?
    - portada: PortadaUrl?
    - workKey: String?
    - editionKey: String?
  - Descripción: Representa un libro con metadatos mínimos y referencias a autores y portada.

- PortadaUrl
  - FQCN: com.example.bibliored.model.PortadaUrl
  - Campos:
    - small: String?
    - medium: String?
    - large: String?
  - Descripción: URLs de imágenes de la portada en diferentes resoluciones.

- Usuario
  - FQCN: com.example.bibliored.model.Usuario
  - Campos:
    - id: Long? = 0
    - nombre: String
    - apellido: String
    - correo: String
    - contrasena: String
  - Descripción: Representa un usuario registrado o autenticado en la app.

- Session
  - FQCN: com.example.bibliored.model.Session
  - Campos:
    - isLoggedIn: Boolean
    - userId: Long? = null
    - userName: String? = null
    - userEmail: String? = null
  - Descripción: Estado de sesión del usuario actual.

Notas:
- Relaciones: Libro.autores es una lista de Autor; Libro.portada enlaza a PortadaUrl; Session puede contener información de usuario para facilitar la experiencia.

## Capas de datos y red
- ApiService.kt: Interfaz de servicios de red para endpoints de la API de Open Library y/o servicios internos.
- RetrofitProvider.kt: Proveedor/configuración de Retrofit para las llamadas HTTP.
- OpenLibraryRepository.kt: Capa de repositorio para operaciones de búsqueda y obtención de libros desde Open Library.
- AuthRepository.kt: Manejo de autenticación y sesión de usuario.
- OpenLibraryService.kt: Servicio específico para operaciones con Open Library.
- DTOs y mapeadores:
  - DescriptionAdapter.kt
  - GenericKeyDto.kt
  - OpenLibraryEditionDto.kt

Es importante entender que estos componentes trabajan en conjunto para abstraer la fuente de datos (API/local) y exponer objetos de dominio simples para la capa de presentación.

## DTOs y mapeadores
- DescriptionAdapter: probable adaptador para descripciones de libros desde respuestas JSON.
- GenericKeyDto: DTO genérico para llaves/identificadores de recursos.
- OpenLibraryEditionDto: DTO para detalles de edición de Open Library, mapeando a entidades de dominio cuando corresponde.

## UI y navegación
- Navigation.kt: componente de navegación entre pantallas.
- Screens presentes (según archivos del proyecto): SplashScreen.kt, LoginScreen.kt, HomeScreen.kt, AddBookScreen.kt, Portada.kt, etc.
- Enfoque: separar claramente vistas (UI) de lógica (ViewModels) y mantener navegación declarativa.

## ViewModels y lógica de negocio
- AuthViewModel.kt: lógica de autenticación y gestión de sesión.
- LibraryViewModel.kt: lógica de manejo de colecciones de libros, búsqueda y filtrado.
- LibroViewModel.kt: lógica específica de operaciones sobre Libro (crear/editar/consultar).

## Persistencia de sesión y preferencias
- SessionPrefs.kt: manejo de preferencias locales para conservar la sesión, tokens o indicadores de estado entre ejecuciones.

## Interacciones API y flujo general
- Inicio de sesión: usa AuthRepository para autenticar y almacenar Session en SessionPrefs.
- Búsqueda y obtención de libros: OpenLibraryRepository/OpenLibraryService a través de ApiService y RetrofitProvider.
- Creación/edición de libros locales: interacción desde LibroViewModel con repositorios de datos locales o remotos.

## Ejemplos de uso (casos de negocio)
- Caso 1: Inicio de sesión exitoso
  -Entrada: credenciales de Usuario
  -Salida: Session.isLoggedIn = true, Session.userId/Name/Email poblados
- Caso 2: Búsqueda de libro por título o ISBN
  -Entrada: cadena de búsqueda
  -Salida: Lista<Libro> con autores enlazados y portadas disponibles (PortadaUrl)
- Caso 3: Añadir libro a la colección local
  -Entrada: Libro con título, autores y portadas
  -Salida: Libro agregado a la colección en memoria o en base de datos local

## Notas de mantenimiento y evolución
- Mantener sincronía entre dominio y DTOs/servicios de red.
- Añadir pruebas unitarias para modelos de dominio (data classes) y para la transformación de DTOs a entidades.
- Documentar las dependencias entre repositorios y proveedores para facilitar refactorings.

## Glosario
- DTO: Data Transfer Object, objeto que transporta datos entre capas.
- API/Proveedor Retrofit: mecanismos para consumir APIs REST desde Android.
- PortadaUrl: representación de las URL de las imágenes de la portada.
- SessionPrefs: almacenamiento de estado de sesión en preferencias locales.

## Anexo: Referencias y archivos relevantes
- Modelos:
  - app/src/main/java/com/example/bibliored/model/Autor.kt
  - app/src/main/java/com/example/bibliored/model/Libro.kt
  - app/src/main/java/com/example/bibliored/model/PortadaUrl.kt
  - app/src/main/java/com/example/bibliored/model/Usuario.kt
  - app/src/main/java/com/example/bibliored/model/Session.kt
- UI y navegación:
  - app/src/main/java/com/example/bibliored/view/Navigation.kt
  - app/src/main/java/com/example/bibliored/view/*.kt (SplashScreen, LoginScreen, HomeScreen, AddBookScreen, Portada)
- Datos y red:
  - app/src/main/java/com/example/bibliored/api/ApiService.kt
  - app/src/main/java/com/example/bibliored/network/RetrofitProvider.kt
  - app/src/main/java/com/example/bibliored/network/OpenLibraryService.kt
  - app/src/main/java/com/example/bibliored/api/OpenLibraryRepository.kt
  - app/src/main/java/com/example/bibliored/api/AuthRepository.kt
  - app/src/main/java/com/example/bibliored/data/SessionPrefs.kt
- DTOs:
  - app/src/main/java/com/example/bibliored/network/dto/DescriptionAdapter.kt
  - app/src/main/java/com/example/bibliored/network/dto/GenericKeyDto.kt
  - app/src/main/java/com/example/bibliored/network/dto/OpenLibraryEditionDto.kt

## Cómo usar este memory bank
- Consulta rápida para comprender las entidades y su relación con la arquitectura.
- Sirve como guía de implementación para nuevas características o refactorings.
- Facilita onboarding de nuevos desarrolladores con un mapa claro de componentes y sus responsabilidades.

Notas finales:
- Este memory bank cubre las clases vistas hasta ahora (Autor, Libro, PortadaUrl, Usuario, Session) y archivos relacionados en las capas de datos, red, DTOs y UI. Si se agregan más archivos (p. ej., SessionPrefs con detalles de implementación o endpoints de API), se pueden ampliar estas secciones fácilmente.

¿Deseas que genere también memory_bank.json con una representación estructurada de estas entidades para consumo automático, y que lo coloque en la raíz del proyecto? Si es así, confirma y lo creo en una siguiente iteración.
