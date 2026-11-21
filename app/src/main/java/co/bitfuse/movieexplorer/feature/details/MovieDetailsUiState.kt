package co.bitfuse.movieexplorer.feature.details

sealed interface MovieDetailsUiState {
    data object Loading : MovieDetailsUiState

    data class Success(
        val movie: MovieDetailsUiModel,
        // Delete is in progress (buttons are disabled meanwhile).
        val isDeleting: Boolean = false,
        // A failed delete, e.g. no connection. The movie stays on screen.
        val actionErrorMessage: String? = null,
    ) : MovieDetailsUiState

    data class Error(val message: String) : MovieDetailsUiState

    // The backend deleted the movie; the route navigates back.
    data object Deleted : MovieDetailsUiState
}
