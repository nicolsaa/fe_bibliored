package com.example.bibliored.network.dto

import com.squareup.moshi.JsonClass

/*GenericKeyDto.kt es complementario al OpenLibraryEditionDto que vimos antes.
Ambos trabajan juntos para obtener información detallada desde la API de OpenLibrary, pero este sirve para consultas secundarias.*/

/*Cuando haces una búsqueda en OpenLibrary por ISBN (por ejemplo https://openlibrary.org/isbn/9780141036144.json), obtienes un JSON así:
* {
  "title": "The God Delusion",
  "authors": [
    { "key": "/authors/OL12345A" }
  ],
  "works": [
    { "key": "/works/OL82563W" }
  ]
}
Eso te da referencias (/authors/..., /works/...) pero no los datos completos.
Si quieres saber el nombre del autor o el título de la obra original, tienes que hacer otra petición.

Y ahí entra tu GenericKeyDto.
*
* Tu OpenLibraryService tiene este método:

@GET("{key}.json")
suspend fun getGenericByKey(@Path("key") key: String): GenericKeyDto

👉 Sirve para resolver cualquier tipo de “key” del sistema OpenLibrary:

Autores → /authors/OL12345A.json
Obras → /works/OL82563W.json

Entonces cuando llamas: api.getGenericByKey("authors/OL12345A")

El servidor responde con algo como:
{
  "key": "/authors/OL12345A",
  "name": "Richard Dawkins"
}

O si es una obra:
{
  "key": "/works/OL82563W",
  "title": "The God Delusion"
}
Tu DTO (GenericKeyDto) mapea exactamente esa estructura.*/


@JsonClass(generateAdapter = true)
data class GenericKeyDto(
    val key: String?, /*Es la ruta base de OpenLibrary (/authors/... o /works/...), te indica de qué entidad estás hablando.*/
    val name: String?,  /*Se usa solo cuando la key corresponde a un autor.
                        Ejemplo: "name": "Gabriel García Márquez"*/
    val title: String?  /*Se usa solo cuando la key corresponde a una obra.
                        Ejemplo: "title": "Cien años de soledad"*/
)