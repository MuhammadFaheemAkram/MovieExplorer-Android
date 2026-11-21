package co.bitfuse.movieexplorer.feature.movieform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bitfuse.movieexplorer.core.common.MovieExplorerResult
import co.bitfuse.movieexplorer.core.navigation.MovieExplorerRoute
import co.bitfuse.movieexplorer.domain.model.Movie
import co.bitfuse.movieexplorer.domain.usecase.CreateMovieUseCase
import co.bitfuse.movieexplorer.domain.usecase.GetMovieDetailsUseCase
import co.bitfuse.movieexplorer.domain.usecase.UpdateMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * One ViewModel for both "Add movie" and "Edit movie".
 * Navigation puts the `movieId` argument into [SavedStateHandle] only on the edit route,
 * so its presence decides the mode.
 */
@HiltViewModel
class MovieFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val createMovieUseCase: CreateMovieUseCase,
    private val updateMovieUseCase: UpdateMovieUseCase,
) : ViewModel() {

    private val editingMovieId: Int? = savedStateHandle.get<Int>(MovieExplorerRoute.EditMovie.MOVIE_ID_ARG)

    // Compose snapshot state instead of a StateFlow, because this screen is a text form.
    // A StateFlow reaches Compose one frame later; with fast typing (or a keyboard's
    // autocorrect) the TextField can be handed that stale value and drop characters.
    // Snapshot state is read and written synchronously, so every keystroke survives.
    var uiState by mutableStateOf(MovieFormUiState(editingMovieId = editingMovieId))
        private set

    init {
        if (editingMovieId != null) {
            loadMovie()
        }
    }

    /** Edit mode: fetch the current values from the backend and pre-fill the form. */
    fun loadMovie() {
        val movieId = editingMovieId ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, loadErrorMessage = null)
            runCatching {
                getMovieDetailsUseCase(movieId)
            }.onSuccess { movie ->
                uiState = uiState.copy(isLoading = false, form = movie.toForm())
            }.onFailure { throwable ->
                uiState = uiState.copy(
                    isLoading = false,
                    loadErrorMessage = throwable.message ?: "Could not load the movie.",
                )
            }
        }
    }

    fun onFieldChange(field: MovieFormField, value: String) {
        val state = uiState
        val form = when (field) {
            MovieFormField.Title -> state.form.copy(title = value)
            MovieFormField.Overview -> state.form.copy(overview = value)
            MovieFormField.Rating -> state.form.copy(rating = value)
            MovieFormField.ReleaseYear -> state.form.copy(releaseYear = value)
            MovieFormField.Genres -> state.form.copy(genres = value)
            MovieFormField.PosterUrl -> state.form.copy(posterUrl = value)
            MovieFormField.BackdropUrl -> state.form.copy(backdropUrl = value)
        }
        uiState = state.copy(
            form = form,
            fieldErrors = state.fieldErrors - field,
            errorMessage = null,
        )
    }

    fun save() {
        val currentState = uiState
        if (currentState.isSaving || currentState.isLoading) return

        val validation = MovieFormValidator.validate(currentState.form)
        if (validation is MovieFormValidationResult.Invalid) {
            uiState = currentState.copy(fieldErrors = validation.errors)
            return
        }
        val movieDraft = (validation as MovieFormValidationResult.Valid).movieDraft

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true, fieldErrors = emptyMap(), errorMessage = null)

            // POST for a new movie, PATCH for an existing one.
            val result = if (editingMovieId == null) {
                createMovieUseCase(movieDraft)
            } else {
                updateMovieUseCase(editingMovieId, movieDraft)
            }

            when (result) {
                is MovieExplorerResult.Success -> {
                    uiState = uiState.copy(isSaving = false, savedMovieId = result.data.id)
                }

                is MovieExplorerResult.Error -> {
                    uiState = uiState.copy(isSaving = false, errorMessage = result.message)
                }
            }
        }
    }

    fun onSavedMovieHandled() {
        uiState = uiState.copy(savedMovieId = null)
    }
}

/** Existing movie -> editable text, e.g. rating 8.2 -> "8.2", genres -> "Sci-Fi, Thriller". */
private fun Movie.toForm(): MovieForm = MovieForm(
    title = title,
    overview = overview,
    rating = rating.toString(),
    releaseYear = releaseYear,
    genres = genres.joinToString(separator = ", "),
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
)
