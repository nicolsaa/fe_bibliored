package com.example.bibliored.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@ExperimentalCoroutinesApi
class SessionPrefsTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var sessionPrefs: SessionPrefs

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder()

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("session_prefs.preferences_pb") }
        )
        sessionPrefs = SessionPrefs(dataStore)
    }

    @Test
    fun `sessionFlow should emit initial logged-out state`() = runTest {
        val session = sessionPrefs.sessionFlow.first()

        assertFalse(session.isLoggedIn)
        assertEquals("", session.userId)
        assertEquals("", session.userName)
        assertEquals("", session.userEmail)
    }
    /*Estado inicial correcto: Cuando un usuario instala la app por primera vez, debe estar en estado
     "no logueado"
    * Datos vacíos: No debe haber información de usuario pre-cargada
    * Flujo funciona: El Flow emite correctamente el estado inicial

    verifica que el sistema de autenticación de LA app comience en un estado limpio y predecible.
    Es el test más básico pero más importante para garantizar que el flujo de login/logout funcione
    correctamente.*/

    @Test
    fun `setLoggedIn should update sessionFlow with user data`() = runTest {
        val id = "test_id_123"
        val name = "Test User"
        val email = "test@example.com"

        sessionPrefs.setLoggedIn(id, name, email)

        val session = sessionPrefs.sessionFlow.first()

        assertTrue(session.isLoggedIn)
        assertEquals(id, session.userId)
        assertEquals(name, session.userName)
        assertEquals(email, session.userEmail)
    }
    /* Verifica que el core de tu sistema de autenticación funciona correctamente.
     * Login exitoso: isLoggedIn = true
     * Persistencia de datos: Todos los campos se guardan correctamente*/

    @Test
    fun `clear should reset sessionFlow to logged-out state`() = runTest {
        // First, log in
        sessionPrefs.setLoggedIn("id", "name", "email")

        // Then, clear the session
        sessionPrefs.clear()

        val session = sessionPrefs.sessionFlow.first()

        assertFalse(session.isLoggedIn)
        assertEquals("", session.userId)
        assertEquals("", session.userName)
        assertEquals("", session.userEmail)
    }
    /*Este test verifica que la función de logout (clear) funciona correctamente.
     * Primero hace login para tener una sesión activa
     * Luego ejecuta logout (clear)
     * Finalmente verifica que todos los datos de usuario se borraron y volvieron a su estado inicial
       (vacío/deslogueado)
    Está comprobando que cuando un usuario cierra sesión:
     * isLoggedIn sea false
     * Todos los campos (userId, userName, userEmail) estén vacíos */

    @Test
    fun `getCurrentSession should return session when logged in`() = runTest {
        val id = "test_id_456"
        val name = "Another User"
        val email = "another@example.com"

        sessionPrefs.setLoggedIn(id, name, email)

        val session = sessionPrefs.getCurrentSession()

        assertNotNull(session)
        assertEquals(id, session?.userId)
        assertEquals(name, session?.userName)
        assertEquals(email, session?.userEmail)
    }
    /*Verifica que la función getCurrentSession() retorne los datos correctos del usuario
     cuando SÍ hay una sesión activa.
     * Comprueba que cuando un usuario está logueado, la función puede recuperar correctamente
     sus datos de sesión.*/
}