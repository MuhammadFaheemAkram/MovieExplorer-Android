package co.bitfuse.movieexplorer.feature.home

data class HomeUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val movies: List<MovieUiModel> = emptyList(),
    val errorMessage: String? = null,
)
