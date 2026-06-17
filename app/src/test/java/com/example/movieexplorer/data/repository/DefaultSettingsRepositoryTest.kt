package com.example.movieexplorer.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.movieexplorer.domain.model.ContentLanguage
import com.example.movieexplorer.domain.model.UserSettings
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

        assertEquals(
            UserSettings(
                darkModeEnabled = true,
                contentLanguage = ContentLanguage.Urdu,
            ),
            repository.observeSettings().first(),
        )
    }
}
