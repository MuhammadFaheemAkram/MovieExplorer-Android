package co.bitfuse.movieexplorer.feature.movieform

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import co.bitfuse.movieexplorer.core.designsystem.MovieExplorerTestTags
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MovieFormScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun typingAndSavingCallsCallbacks() {
        val changes = mutableListOf<Pair<MovieFormField, String>>()
        var saveClicked = false

        composeRule.setContent {
            MovieExplorerTheme {
                MovieFormScreen(
                    uiState = MovieFormUiState(),
                    onFieldChange = { field, value -> changes += field to value },
                    onSave = { saveClicked = true },
                )
            }
        }

        composeRule.onNodeWithTag(MovieExplorerTestTags.MovieFormTitleField).performTextInput("Lantern Sea")
        composeRule.onNodeWithTag(MovieExplorerTestTags.MovieFormSaveButton).performScrollTo().performClick()

        assertEquals(MovieFormField.Title to "Lantern Sea", changes.single())
        assertTrue(saveClicked)
    }

    @Test
    fun fieldErrorsAndServerErrorAreDisplayed() {
        composeRule.setContent {
            MovieExplorerTheme {
                MovieFormScreen(
                    uiState = MovieFormUiState(
                        fieldErrors = mapOf(MovieFormField.Rating to "Enter a number from 0 to 10."),
                        errorMessage = "A movie titled 'Lantern Sea' from 2026 already exists (id 17).",
                    ),
                    onFieldChange = { _, _ -> },
                    onSave = {},
                )
            }
        }

        composeRule.onNodeWithText("Enter a number from 0 to 10.").assertIsDisplayed()
        composeRule.onNodeWithTag(MovieExplorerTestTags.MovieFormError).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun saveButtonIsDisabledWhileSaving() {
        composeRule.setContent {
            MovieExplorerTheme {
                MovieFormScreen(
                    uiState = MovieFormUiState(isSaving = true),
                    onFieldChange = { _, _ -> },
                    onSave = {},
                )
            }
        }

        composeRule.onNodeWithTag(MovieExplorerTestTags.MovieFormSaveButton).performScrollTo().assertIsNotEnabled()
        composeRule.onNodeWithText("Saving...").assertIsDisplayed()
    }

    @Test
    fun editModeShowsSaveChangesLabel() {
        composeRule.setContent {
            MovieExplorerTheme {
                MovieFormScreen(
                    uiState = MovieFormUiState(editingMovieId = 7, form = MovieForm(title = "Harbor of Glass")),
                    onFieldChange = { _, _ -> },
                    onSave = {},
                )
            }
        }

        composeRule.onNodeWithText("Harbor of Glass").assertIsDisplayed()
        composeRule.onNodeWithText("Save changes").performScrollTo().assertIsDisplayed()
    }
}
