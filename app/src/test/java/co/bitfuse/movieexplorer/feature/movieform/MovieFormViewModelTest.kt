package co.bitfuse.movieexplorer.feature.movieform

import androidx.lifecycle.SavedStateHandle
import co.bitfuse.movieexplorer.core.navigation.MovieExplorerRoute
import co.bitfuse.movieexplorer.domain.usecase.CreateMovieUseCase
import co.bitfuse.movieexplorer.domain.usecase.GetMovieDetailsUseCase
import co.bitfuse.movieexplorer.domain.usecase.UpdateMovieUseCase
import co.bitfuse.movieexplorer.testing.testMovie
import co.bitfuse.movieexplorer.testing.FakeMovieRepository
import co.bitfuse.movieexplorer.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieFormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeMovieRepository()
    private val viewModel = formViewModel()

    // No movieId argument = "Add movie" mode; with one = "Edit movie" mode (like the nav routes).
    private fun formViewModel(editingMovieId: Int? = null): MovieFormViewModel {
        val arguments = if (editingMovieId == null) {
            emptyMap()
        } else {
            mapOf(MovieExplorerRoute.EditMovie.MOVIE_ID_ARG to editingMovieId)
        }
        return MovieFormViewModel(
            savedStateHandle = SavedStateHandle(arguments),
            getMovieDetailsUseCase = GetMovieDetailsUseCase(repository),
            createMovieUseCase = CreateMovieUseCase(repository),
            updateMovieUseCase = UpdateMovieUseCase(repository),
        )
    }

    @Test
    fun `invalid form shows field errors and does not call backend`() = runTest {
        viewModel.save()
        advanceUntilIdle()

        val state = viewModel.uiState
        assertTrue(MovieFormField.Title in state.fieldErrors)
        assertNull(repository.lastCreatedMovie)
    }

    @Test
    fun `editing a field clears only that field error`() = runTest {
        viewModel.save()
        viewModel.onFieldChange(MovieFormField.Title, "Lantern Sea")

        val errors = viewModel.uiState.fieldErrors
        assertFalse(MovieFormField.Title in errors)
        assertTrue(MovieFormField.Overview in errors)
    }

    @Test
    fun `valid form creates movie and exposes its id for navigation`() = runTest {
        fillValidForm()

        viewModel.save()
        advanceUntilIdle()

        val state = viewModel.uiState
        assertEquals(1, state.savedMovieId)
        assertFalse(state.isSaving)
        assertEquals("Lantern Sea", repository.lastCreatedMovie?.title)

        viewModel.onSavedMovieHandled()
        assertNull(viewModel.uiState.savedMovieId)
    }

    @Test
    fun `server error is shown and the form is kept`() = runTest {
        repository.createMovieError =
            IllegalStateException("A movie titled 'Lantern Sea' from 2026 already exists (id 17).")
        fillValidForm()

        viewModel.save()
        advanceUntilIdle()

        val state = viewModel.uiState
        assertEquals("A movie titled 'Lantern Sea' from 2026 already exists (id 17).", state.errorMessage)
        assertNull(state.savedMovieId)
        assertEquals("Lantern Sea", state.form.title)
    }

    @Test
    fun `edit mode loads the movie into the form`() = runTest {
        repository.trendingMovies.value = listOf(testMovie(id = 7, title = "Harbor of Glass"))

        val editViewModel = formViewModel(editingMovieId = 7)
        advanceUntilIdle()

        val state = editViewModel.uiState
        assertTrue(state.isEditing)
        assertFalse(state.isLoading)
        assertEquals("Harbor of Glass", state.form.title)
        assertEquals("8.2", state.form.rating)
        assertEquals("2026", state.form.releaseYear)
        assertEquals("Sci-Fi, Thriller", state.form.genres)
    }

    @Test
    fun `edit mode saves with update instead of create`() = runTest {
        repository.trendingMovies.value = listOf(testMovie(id = 7, title = "Harbor of Glass"))
        val editViewModel = formViewModel(editingMovieId = 7)
        advanceUntilIdle()

        editViewModel.onFieldChange(MovieFormField.Rating, "9.1")
        editViewModel.save()
        advanceUntilIdle()

        assertEquals(7, editViewModel.uiState.savedMovieId)
        assertEquals(7, repository.lastUpdatedMovie?.first)
        assertEquals(9.1, repository.lastUpdatedMovie?.second?.rating ?: 0.0, 0.0)
        assertNull(repository.lastCreatedMovie)
    }

    @Test
    fun `edit mode shows a load error when the movie cannot be fetched`() = runTest {
        val editViewModel = formViewModel(editingMovieId = 404)
        advanceUntilIdle()

        assertFalse(editViewModel.uiState.isLoading)
        assertTrue(editViewModel.uiState.loadErrorMessage != null)
    }

    private fun fillValidForm() {
        viewModel.onFieldChange(MovieFormField.Title, "Lantern Sea")
        viewModel.onFieldChange(MovieFormField.Overview, "A lighthouse keeper follows floating lanterns.")
        viewModel.onFieldChange(MovieFormField.Rating, "7.2")
        viewModel.onFieldChange(MovieFormField.ReleaseYear, "2026")
        viewModel.onFieldChange(MovieFormField.Genres, "Adventure, Fantasy")
    }
}
