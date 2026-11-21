package co.bitfuse.movieexplorer.feature.movieform

data class MovieFormUiState(
    // null = adding a new movie; otherwise the id of the movie being edited.
    val editingMovieId: Int? = null,
    // Edit mode only: true while the existing movie is loaded into the form.
    val isLoading: Boolean = false,
    val loadErrorMessage: String? = null,
    val form: MovieForm = MovieForm(),
    // Shown only after the user taps Save; an edited field clears its own error.
    val fieldErrors: Map<MovieFormField, String> = emptyMap(),
    val isSaving: Boolean = false,
    // Server-side failure, e.g. a duplicate title + year (HTTP 409) or no connection.
    val errorMessage: String? = null,
    // Set once the backend saved the movie; the route navigates to its details, then clears it.
    val savedMovieId: Int? = null,
) {
    val isEditing: Boolean
        get() = editingMovieId != null
}
