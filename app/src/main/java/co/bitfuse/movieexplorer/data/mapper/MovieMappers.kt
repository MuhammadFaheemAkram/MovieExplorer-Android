package co.bitfuse.movieexplorer.data.mapper

import co.bitfuse.movieexplorer.data.model.MovieRequestDto
import co.bitfuse.movieexplorer.data.model.MovieDto
import co.bitfuse.movieexplorer.domain.model.Movie
import co.bitfuse.movieexplorer.domain.model.MovieDraft

// DTOs and domain models are separate so transport changes do not leak upward:
// the backend sends releaseYear as a number, while the domain/UI keep a display String.
fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    releaseYear = releaseYear.toString(),
    genres = genres,
    isFavorite = isFavorite,
)

fun MovieDraft.toRequestDto(): MovieRequestDto = MovieRequestDto(
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    releaseYear = releaseYear,
    genres = genres,
)
