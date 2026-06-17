package com.example.movieexplorer.feature.search

import com.example.movieexplorer.domain.model.Movie

data class SearchMovieUiModel(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val rating: String,
    val releaseYear: String,
    val genres: List<String>,
)

fun Movie.toSearchUiModel(): SearchMovieUiModel = SearchMovieUiModel(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    rating = "%.1f".format(rating),
    releaseYear = releaseYear,
    genres = genres,
)
