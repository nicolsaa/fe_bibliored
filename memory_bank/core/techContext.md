# Tech Context

- Tecnologías usadas:
  - Kotlin
  - Android
  - Retrofit (y OkHttp)
  - Coroutines (y/o Flow si aplica)
  - MVVM para UI (ViewModel, LiveData/Flow)
  - AndroidX y componentes de UI

- Entorno de desarrollo:
  - Android Studio
  - Gradle (build scripts existentes)

- Dependencias y archivos relevantes:
  - app/src/main/java/com/example/bibliored/api/ApiService.kt
  - app/src/main/java/com/example/bibliored/network/RetrofitProvider.kt
  - app/src/main/java/com/example/bibliored/network/OpenLibraryService.kt
  - app/src/main/java/com/example/bibliored/api/OpenLibraryRepository.kt
  - app/src/main/java/com/example/bibliored/api/AuthRepository.kt
  - app/src/main/java/com/example/bibliored/data/SessionPrefs.kt
  - DTOs: DescriptionAdapter.kt, GenericKeyDto.kt, OpenLibraryEditionDto.kt

- Restricciones y prácticas recomendadas:
  - Mantener separación clara entre dominio y red
  - Manejar errores de red y estados de carga en los ViewModels
  - Pruebas unitarias para modelos de dominio y transformaciones DTO -> entidad
