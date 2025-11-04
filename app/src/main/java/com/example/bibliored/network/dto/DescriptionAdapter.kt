package com.example.bibliored.network.dto

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/*En OpenLibrary, el campo description de un libro no tiene siempre el mismo formato.
Dependiendo del registro, puede venir como texto plano("description": "A fascinating study of evolution.") o como objeto("description": { "value": "A fascinating study of evolution." })
Si no tienes un adaptador, Moshi no sabría cómo convertir ambos formatos al mismo tipo Kotlin y te lanzaría un error de parseo.*/

/*Esto define dos tipos posibles de descripción dentro de la app:
Moshi puede mapear automáticamente cualquiera de esos tipos gracias al adaptador.*/

sealed class Description {
    data class Text(val value: String) : Description() //Description.Text("...") → tiene texto
    object None : Description() // Description.None → no hay descripción
}

class DescriptionAdapter {
    @FromJson //Le dice a Moshi cómo interpretar el JSON entrante.
    fun fromJson(raw: Any?): Description = when (raw) {
        //Si el valor que llega es:
        is String -> Description.Text(raw) //Un String → lo envuelve como Description.Text("texto")
        is Map<*, *> -> (raw["value"] as? String)?.let { Description.Text(it) } ?: Description.None // Un Map con "value" → extrae ese valor
        else -> Description.None // Cualquier otra cosa → devuelve Description.None
    }
    @ToJson // Hace el proceso inverso al serializar:
    fun toJson(desc: Description): Any? = when (desc) {
        is Description.Text -> desc.value // Description.Text("abc") → "abc"
        Description.None -> null // Description.None → null
    }
}

/** Helper para extraer texto plano o null */
fun Any?.descriptionTextOrNull(): String? = when (this) {
    is String -> this
    is Map<*, *> -> this["value"] as? String
    else -> null
}
/*Esta es una función de conveniencia:
Sirve para extraer el texto del campo description sin tener que tratar directamente con Description.
Se usa, por ejemplo, en tu OpenLibraryRepository: val descripcion = dto.description.descriptionTextOrNull()
Eso te da directamente "A fascinating study of evolution.",
sin importar si venía como "string" o como {"value": "..."}.*/