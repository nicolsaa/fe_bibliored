package com.example.bibliored.controller

import com.example.bibliored.api.OpenLibraryRepository
import com.example.bibliored.data.ISessionPrefs
import com.example.bibliored.model.Autor
import com.example.bibliored.model.Libro
import com.example.bibliored.model.PortadaUrl
import com.example.bibliored.model.Session
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// Helper Rule for testing ViewModels with Coroutines
@ExperimentalCoroutinesApi
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class LibroViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repo: OpenLibraryRepository
    private lateinit var sessionPrefs: ISessionPrefs
    private lateinit var viewModel: LibroViewModel

    // Mock data using the correct data class definitions
    private val fakeUserSession = Session(isLoggedIn = true, userId = "1", userName = "Test User", userEmail = "test@user.com")
    private val loggedOutSession = Session(isLoggedIn = false, userId = "", userName = "", userEmail = "")
    private val fakeLibro = Libro(
        isbn10 = "1234567890",
        isbn13 = "123-1234567890",
        titulo = "Kotlin for Dummies",
        autores = listOf(Autor(1L, "Dr. Kotlin")),
        descripcion = "A book about Kotlin",
        portada = PortadaUrl("S", "M", "L"),
        workKey = "/works/OL1",
        editionKey = "/books/OL1",
        nombreUsuario = null,
        userId = null
    )
    private val sessionFlow = MutableStateFlow<Session>(loggedOutSession)

    @Before
    fun setUp() {
        repo = mockk()
        sessionPrefs = mockk<ISessionPrefs>(relaxed = true)

        coEvery { sessionPrefs.sessionFlow } returns sessionFlow

        viewModel = LibroViewModel(repo, sessionPrefs)
    }

    @Test
    fun `cargarPorIsbn success should update state to Ok`() = runTest {
        // Arrange
        sessionFlow.value = fakeUserSession
        val isbn = "1234567890"
        coEvery { repo.getLibroByIsbn(isbn, fakeUserSession.userEmail, true) } returns Result.success(fakeLibro)

        // Act
        viewModel.cargarPorIsbn(isbn)

        // Assert
        val finalState = viewModel.estado.value
        assertTrue("State should be Ok", finalState is UiState.Ok)
        assertEquals(fakeLibro, (finalState as UiState.Ok).libro)

        coVerify(exactly = 1) { repo.getLibroByIsbn(isbn, fakeUserSession.userEmail, true) }
    }
    /*Este test verifica que cuando se carga un libro por ISBN exitosamente, el estado del ViewModel
    cambia a "Ok" con el libro correcto.*/

    @Test
    fun `cargarPorIsbn failure should update state to Error`() = runTest {
        // Arrange
        sessionFlow.value = fakeUserSession
        val isbn = "0987654321"
        val errorMessage = "Libro no encontrado"
        coEvery { repo.getLibroByIsbn(isbn, fakeUserSession.userEmail, true) } returns Result.failure(Exception(errorMessage))

        // Act
        viewModel.cargarPorIsbn(isbn)

        // Assert
        val finalState = viewModel.estado.value
        assertTrue("State should be Error", finalState is UiState.Error)
        assertEquals(errorMessage, (finalState as UiState.Error).msg)

        coVerify(exactly = 1) { repo.getLibroByIsbn(isbn, fakeUserSession.userEmail, true) }
    }

    /*Este test verifica que cuando falla la búsqueda de un libro por ISBN, el estado del ViewModel
    cambia a "Error" con el mensaje correcto.*/

    @Test
    fun `cargarPorIsbn when not logged in should throw exception`() = runTest {
        // Arrange
        sessionFlow.value = loggedOutSession

        // Act & Assert
        assertThrows(RuntimeException::class.java) {
            viewModel.cargarPorIsbn("any-isbn")
        }
    }


    /*Este test verifica que cuando un usuario NO está logueado e intenta cargar un libro por ISBN,
    se lanza una excepción porque no debería permitir la operación sin estar logueado.*/
}
