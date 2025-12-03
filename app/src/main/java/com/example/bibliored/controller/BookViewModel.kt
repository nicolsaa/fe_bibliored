package com.example.bibliored.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliored.model.Autor
import com.example.bibliored.model.Libro
import com.example.bibliored.model.PortadaUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {

    private val _homeBooks = MutableStateFlow<List<Libro>>(getInitialBooks())
    val homeBooks: StateFlow<List<Libro>> = _homeBooks

    fun publishBook(libro: Libro, userName: String) {
        viewModelScope.launch {
            // Create a copy of the book to be published, updating its user name.
            val bookToPublish = libro.copy(nombreUsuario = userName)

            val currentBooks = _homeBooks.value.toMutableList()

            // Use editionKey or workKey as a unique identifier.
            val publicationId = bookToPublish.editionKey ?: bookToPublish.workKey

            // If a unique ID is available, find and remove the existing book to prevent duplicates.
            if (publicationId != null) {
                val existingBookIndex = currentBooks.indexOfFirst { (it.editionKey ?: it.workKey) == publicationId }
                if (existingBookIndex != -1) {
                    currentBooks.removeAt(existingBookIndex)
                }
            }

            // Add the new or updated book to the top of the list.
            currentBooks.add(0, bookToPublish)
            _homeBooks.value = currentBooks
        }
    }

    private fun getInitialBooks(): List<Libro> {
        return listOf(
            Libro(
                isbn10 = null,
                isbn13 = "9786070704628",
                titulo = "El Señor de los Anillos",
                autores = listOf(Autor(nombre = "J.R.R. Tolkien")),
                descripcion = "Una gran aventura en la Tierra Media que narra el viaje del hobbit Frodo Bolsón para destruir el Anillo Único y derrotar al Señor Oscuro, Sauron. Acompañado por una comunidad diversa de hobbits, elfos, enanos y hombres, Frodo debe atravesar peligrosas tierras y enfrentarse a sus miedos más profundos.",
                portada = PortadaUrl(
                    small = "https://covers.openlibrary.org/b/id/10308969-S.jpg",
                    medium = "https://covers.openlibrary.org/b/id/10308969-M.jpg",
                    large = "https://covers.openlibrary.org/b/id/10308969-L.jpg"

                ),
                workKey = "/works/OL45804W",
                editionKey = "/books/OL51693993M",
                nombreUsuario = "Juan Perez",
                userId = "user123"
            ),
            Libro(
                isbn10 = "9500700298",
                isbn13 = null,
                titulo = "Cien Años de Soledad",
                autores = listOf(Autor(nombre = "Gabriel García Márquez")),
                descripcion = "Cien años de soledad es una novela del escritor colombiano Gabriel García Márquez, ganador del Premio Nobel de Literatura en 1982. Es considerada una obra maestra de la literatura hispanoamericana y universal, cumbre del denominado \"realismo mágico\". Es asimismo una de las obras más traducidas y leídas en español. Narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo.",
                portada = PortadaUrl(
                    small = "https://covers.openlibrary.org/b/id/8264768-S.jpg",
                    medium = "https://covers.openlibrary.org/b/id/8264768-M.jpg",
                    large = "https://covers.openlibrary.org/b/id/8264768-L.jpg"
                ),
                workKey = "/works/OL45883W",
                editionKey = "/books/OL5583516M",
                nombreUsuario = "Maria Rodriguez",
                userId = "user456"
            ),
            Libro(
                isbn10 = null,
                isbn13 = null,
                titulo = "Libro sin Portada",
                autores = listOf(Autor(nombre = "Autor Anónimo")),
                descripcion = "Un libro misterioso sin portada y con una descripción muy breve.",
                portada = null,
                workKey = null,
                editionKey = null,
                nombreUsuario = "Pedro Pascal",
                userId = "user789"
            )
        )
    }
}
