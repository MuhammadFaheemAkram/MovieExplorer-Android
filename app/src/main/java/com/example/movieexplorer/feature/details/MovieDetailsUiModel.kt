package com.example.movieexplorer.feature.details

import com.example.movieexplorer.domain.model.Movie

data class MovieDetailsUiModel(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: String,
    val releaseYear: String,
    val genres: List<String>,
    val isFavorite: Boolean,
)

fun Movie.toDetailsUiModel(): MovieDetailsUiModel = MovieDetailsUiModel(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = "%.1f".format(rating),
    releaseYear = releaseYear,
    genres = genres,
    isFavorite = isFavorite,
)

fun MovieDetailsUiModel.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating.toDoubleOrNull() ?: 0.0,
    releaseYear = releaseYear,
    genres = genres,
    isFavorite = isFavorite,
)
