package co.bitfuse.movieexplorer.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import co.bitfuse.movieexplorer.domain.model.ContentLanguage
import co.bitfuse.movieexplorer.domain.model.UserSettings
import java.io.File
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultSettingsRepositoryTest {

    @Test
    fun `settings are read from and written to DataStore`() = runTest {
        val preferencesFile = File.createTempFile("movie-settings", ".preferences_pb").apply {
            delete()
        }
        val dataStore = PreferenceDataStoreFactory.create(
            scope = backgroundScope,
            produceFile = { preferencesFile },
        )
        val repository = DefaultSettingsRepository(dataStore)

        assertEquals(UserSettings(), repository.observeSettings().first())

        repository.setDarkModeEnabled(true)
        repository.setContentLanguage(ContentLanguage.Urdu)
        repository.setMovieManagementEnabled(true)

        assertEquals(
            UserSettings(
                darkModeEnabled = true,
                contentLanguage = ContentLanguage.Urdu,
                movieManagementEnabled = true,
            ),
            repository.observeSettings().first(),
        )
    }
}
