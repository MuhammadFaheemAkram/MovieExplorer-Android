package co.bitfuse.movieexplorer.feature.favorites

data class FavoritesUiState(
    val movies: List<FavoriteMovieUiModel> = emptyList(),
)
