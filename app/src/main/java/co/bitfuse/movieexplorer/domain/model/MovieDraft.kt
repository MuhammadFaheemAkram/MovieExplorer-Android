package co.bitfuse.movieexplorer.domain.model

/**
 * The editable fields of a movie, as entered in the Add / Edit form.
 * It has no id (the backend assigns it on create) and no favorite state.
 */
data class MovieDraft(
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Double,
    val releaseYear: Int,
    val genres: List<String>,
)
