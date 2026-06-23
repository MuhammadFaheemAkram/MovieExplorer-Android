package co.bitfuse.movieexplorer.feature.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import co.bitfuse.movieexplorer.core.designsystem.MovieExplorerTestTags
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerTheme
import co.bitfuse.movieexplorer.testing.testHomeMovieUiModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loadingStateShowsProgressIndicator() {
        composeRule.setContent {
            MovieExplorerTheme {
                HomeScreen(
                    uiState = HomeUiState(isLoading = true),
                    onMovieClick = {},
                    onFavoriteClick = {},
                    onRefresh = {},
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithTag(MovieExplorerTestTags.HomeLoading).assertIsDisplayed()
    }

    @Test
    fun successStateShowsMoviesAndHandlesClicks() {
        val movie = testHomeMovieUiModel()
        var clickedMovieId: Int? = null
        var favoriteClicked = false

        composeRule.setContent {
            MovieExplorerTheme {
                HomeScreen(
                    uiState = HomeUiState(movies = listOf(movie)),
                    onMovieClick = { clickedMovieId = it },
                    onFavoriteClick = { favoriteClicked = true },
                    onRefresh = {},
                    onRetry = {},
                )
            }
        }

        composeRule.onNodeWithText("Aurora Protocol").assertIsDisplayed()
        composeRule.onNodeWithText("2026").assertIsDisplayed()
        composeRule.onAllNodesWithTag(MovieExplorerTestTags.HomeMovieCard)[0].performClick()
        composeRule.onAllNodesWithTag(MovieExplorerTestTags.HomeFavoriteButton)[0].performClick()

        assertEquals(1, clickedMovieId)
        assertTrue(favoriteClicked)
    }

    @Test
    fun errorStateCallsRetry() {
        var retryClicked = false

        composeRule.setContent {
            MovieExplorerTheme {
                HomeScreen(
                    uiState = HomeUiState(errorMessage = "Network failed"),
                    onMovieClick = {},
                    onFavoriteClick = {},
                    onRefresh = {},
                    onRetry = { retryClicked = true },
                )
            }
        }

        composeRule.onNodeWithText("Movies could not load").assertIsDisplayed()
        composeRule.onNodeWithText("Retry").performClick()

        assertTrue(retryClicked)
    }
}
