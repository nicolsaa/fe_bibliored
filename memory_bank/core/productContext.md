# Product Context

- Propósito: Proporcionar una referencia de producto técnica para el artefacto Android-Kotlin de BiblioRed, facilitando mantenimiento, onboarding y futuras evoluciones.

- Problemas que resuelve:
  - Alinea expectativas entre equipo de desarrollo, QA y producto.
  - Documenta decisiones clave de diseño y relaciones entre capas (dominio, datos/red, DTOs/adapters, UI, ViewModels).
  - Sirve como fuente de verdad para futuros refactors y extensiones.

- Cómo debería funcionar:
  - El artefacto ofrece capa de dominio (modelos), capa de datos y red (repositorios, servicios y proveedores), DTOs y mapeadores, UI y navegación, y lógica de negocio en ViewModels.
  - Las operaciones principales incluyen búsqueda/recuperación de libros desde Open Library, autenticación y persistencia de sesión, y gestión de una colección local o remota de libros.
  - Integración entre repositorios y proveedores a través de Retrofit, con mapeos entre DTOs y entidades de dominio.

- Objetivos de experiencia de usuario:
  - Interfaz fluida y sensible a la red (tiempos de respuesta razonables).
  - Navegación clara y consistente entre pantallas (Splash, Login, Portada, Home, AddBook).
  - Disponibilidad de datos de portada (PortadaUrl) y autores enlazados en resultados de búsqueda.
  - Persistencia de sesión para mantener el estado entre ejecuciones.
