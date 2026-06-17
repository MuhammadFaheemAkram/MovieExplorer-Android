package com.example.movieexplorer.feature.search

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.movieexplorer.core.designsystem.MovieExplorerTestTags
import com.example.movieexplorer.core.designsystem.theme.MovieExplorerTheme
import com.example.movieexplorer.testing.testSearchMovieUiModel
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class SearchScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun emptyQueryStateShowsPromptAndTextFieldCallback() {
        var latestQuery = ""

        composeRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = SearchUiState(),
                    onQueryChange = { latestQuery = it },
                    onClearQuery = {},
                    onRetry = {},
                    onMovieClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Find a movie").assertIsDisplayed()
        composeRule.onNodeWithTag(MovieExplorerTestTags.SearchField).performTextInput("orbit")

        assertEquals("orbit", latestQuery)
    }

    @Test
    fun resultStateShowsMovieAndHandlesClick() {
        var clickedMovieId: Int? = null

        composeRule.setContent {
            MovieExplorerTheme {
                SearchScreen(
                    uiState = SearchUiState(
                        query = "orbit",
                        hasSearched = true,
                        movies = listOf(testSearchMovieUiModel()),
                    ),
                    onQueryChange = {},
                    onClearQuery = {},
                    onRetry = {},
                    onMovieClick = { clickedMovieId = it },
                )
            }
        }

        composeRule.onNodeWithText("Orbit Cafe").assertIsDisplayed()
        composeRule.onAllNodesWithTag(MovieExplorerTestTags.SearchResultRow)[0].performClick()

        assertEquals(8, clickedMovieId)
    }
}
