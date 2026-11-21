package co.bitfuse.movieexplorer.feature.settings

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.bitfuse.movieexplorer.core.designsystem.MovieExplorerTestTags
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerTheme
import co.bitfuse.movieexplorer.domain.model.ContentLanguage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun darkModeSwitchCallsCallback() {
        var darkModeEnabled = false

        composeRule.setContent {
            MovieExplorerTheme {
                SettingsScreen(
                    uiState = SettingsUiState(darkModeEnabled = false),
                    onDarkModeChange = { darkModeEnabled = it },
                    onLanguageChange = {},
                    onMovieManagementChange = {},
                )
            }
        }

        composeRule.onNodeWithText("Dark mode").assertIsDisplayed()
        composeRule.onNodeWithTag(MovieExplorerTestTags.SettingsDarkModeSwitch).performClick()

        assertTrue(darkModeEnabled)
    }

    @Test
    fun languageDropdownCallsCallback() {
        var selectedLanguage: ContentLanguage? = null

        composeRule.setContent {
            MovieExplorerTheme {
                SettingsScreen(
                    uiState = SettingsUiState(selectedLanguage = ContentLanguage.English),
                    onDarkModeChange = {},
                    onLanguageChange = { selectedLanguage = it },
                    onMovieManagementChange = {},
                )
            }
        }

        composeRule.onNodeWithTag(MovieExplorerTestTags.SettingsLanguageField).performClick()
        composeRule.onNodeWithText("Urdu").performClick()

        assertEquals(ContentLanguage.Urdu, selectedLanguage)
    }

    @Test
    fun movieManagementSwitchCallsCallback() {
        var movieManagementEnabled = false

        composeRule.setContent {
            MovieExplorerTheme {
                SettingsScreen(
                    uiState = SettingsUiState(movieManagementEnabled = false),
                    onDarkModeChange = {},
                    onLanguageChange = {},
                    onMovieManagementChange = { movieManagementEnabled = it },
                )
            }
        }

        composeRule.onNodeWithText("Manage movies").assertIsDisplayed()
        composeRule.onNodeWithTag(MovieExplorerTestTags.SettingsMovieManagementSwitch).performClick()

        assertTrue(movieManagementEnabled)
    }
}
