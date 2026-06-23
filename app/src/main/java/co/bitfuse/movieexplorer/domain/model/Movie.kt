package co.bitfuse.movieexplorer.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Double,
    val releaseYear: String,
    val genres: List<String>,
    val isFavorite: Boolean,
)
