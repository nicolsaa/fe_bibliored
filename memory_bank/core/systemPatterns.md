# System Patterns

- Arquitectura y capas:
  - Dominio (modelos de datos)
  - Datos y red (repositorios y proveedores)
  - Presentación (UI)
  - Lógica de negocio y estado (ViewModels)
  - Persistencia de sesión y preferencias (SessionPrefs)

- Patrones clave:
  - Data classes para modelos de dominio
  - Repository/Provider para abstracciones de datos y red
  - Mapeadores entre DTOs/entidades cuando aplique
  - MVVM para UI con ViewModels
  - Gestión de sesión persistente

- Relaciones y caminos de implementación críticos:
  - Repositorio <-> Proveedor <-> API/Red
  - DTOs <-> Entidades de dominio
  - UI vinculada a ViewModels para estado y flujos de negocio
