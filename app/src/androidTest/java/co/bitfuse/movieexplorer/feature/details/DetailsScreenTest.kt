package co.bitfuse.movieexplorer.feature.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.bitfuse.movieexplorer.core.designsystem.MovieExplorerTestTags
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val movie = MovieDetailsUiModel(
        id = 7,
        title = "Harbor of Glass",
        overview = "A detective returns home.",
        posterUrl = "",
        backdropUrl = "",
        rating = "8.4",
        releaseYear = "2026",
        genres = listOf("Drama", "Mystery"),
        isFavorite = false,
    )

    @Test
    fun manageButtonsAreHiddenWhenMovieManagementIsOff() {
        setDetails(canManageMovies = false)

        composeRule.onNodeWithText("Harbor of Glass").assertIsDisplayed()
        composeRule.onNodeWithTag(MovieExplorerTestTags.DetailsEditButton).assertDoesNotExist()
        composeRule.onNodeWithTag(MovieExplorerTestTags.DetailsDeleteButton).assertDoesNotExist()
    }

    @Test
    fun editButtonCallsCallback() {
        var editClicks = 0
        setDetails(canManageMovies = true, onEditClick = { editClicks++ })

        composeRule.onNodeWithTag(MovieExplorerTestTags.DetailsEditButton).performClick()

        assertEquals(1, editClicks)
    }

    @Test
    fun deleteAsksForConfirmationBeforeCallingCallback() {
        var deleteConfirmed = false
        setDetails(canManageMovies = true, onDeleteConfirmed = { deleteConfirmed = true })

        composeRule.onNodeWithTag(MovieExplorerTestTags.DetailsDeleteButton).performClick()
        composeRule.onNodeWithText("Delete movie?").assertIsDisplayed()
        assertFalse(deleteConfirmed)

        composeRule.onNodeWithTag(MovieExplorerTestTags.DetailsDeleteConfirmButton).performClick()

        assertTrue(deleteConfirmed)
    }

    private fun setDetails(
        canManageMovies: Boolean,
        onEditClick: () -> Unit = {},
        onDeleteConfirmed: () -> Unit = {},
    ) {
        composeRule.setContent {
            MovieExplorerTheme {
                DetailsScreen(
                    uiState = MovieDetailsUiState.Success(movie),
                    onRetry = {},
                    onFavoriteClick = {},
                    canManageMovies = canManageMovies,
                    onEditClick = onEditClick,
                    onDeleteConfirmed = onDeleteConfirmed,
                )
            }
        }
    }
}
