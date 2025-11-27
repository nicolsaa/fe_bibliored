package com.example.bibliored.util

import com.example.bibliored.model.Libro

/*Este código implementa un patrón de almacenamiento temporal para navegación entre pantallas.*/
object SelectedBookNav {
    var currentLibro: Libro? = null
}
