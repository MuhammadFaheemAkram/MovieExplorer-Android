package co.bitfuse.movieexplorer.data.model

data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Double,
    val releaseYear: String,
    val genres: List<String>,
)
