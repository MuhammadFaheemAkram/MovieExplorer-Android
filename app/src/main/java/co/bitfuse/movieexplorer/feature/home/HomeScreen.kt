package co.bitfuse.movieexplorer.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import co.bitfuse.movieexplorer.core.designsystem.MovieExplorerTestTags
import co.bitfuse.movieexplorer.core.designsystem.components.MovieExplorerPlaceholderScreen
import co.bitfuse.movieexplorer.core.designsystem.components.PlaceholderAction
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerSpacing

@Composable
fun HomeRoute(
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onMovieClick = onMovieClick,
        onFavoriteClick = viewModel::toggleFavorite,
        onRefresh = viewModel::refresh,
        onRetry = viewModel::retry,
        modifier = modifier,
    )
}

// Composables receive state and callbacks so business logic stays in the ViewModel.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (MovieUiModel) -> Unit,
    onRefresh: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize(),
    ) {
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.fillMaxSize())
            uiState.errorMessage != null && uiState.movies.isEmpty() -> ErrorState(
                message = uiState.errorMessage,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
            )

            uiState.movies.isEmpty() -> EmptyState(
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
            )

            else -> MovieGrid(
                movies = uiState.movies,
                errorMessage = uiState.errorMessage,
                onMovieClick = onMovieClick,
                onFavoriteClick = onFavoriteClick,
                onRetry = onRetry,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun MovieGrid(
    movies: List<MovieUiModel>,
    errorMessage: String?,
    onMovieClick: (Int) -> Unit,
    onFavoriteClick: (MovieUiModel) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier.testTag(MovieExplorerTestTags.HomeMovieGrid),
        contentPadding = PaddingValues(MovieExplorerSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
        horizontalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
    ) {
        if (errorMessage != null) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                InlineErrorBanner(
                    message = errorMessage,
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        items(
            items = movies,
            key = { movie -> movie.id },
        ) { movie ->
            MovieCard(
                movie = movie,
                onClick = { onMovieClick(movie.id) },
                onFavoriteClick = { onFavoriteClick(movie) },
            )
        }
    }
}

@Composable
private fun MovieCard(
    movie: MovieUiModel,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .testTag(MovieExplorerTestTags.HomeMovieCard)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Box {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = "${movie.title} poster",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                )
                FavoriteButton(
                    isFavorite = movie.isFavorite,
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(MovieExplorerSpacing.small),
                )
            }

            Column(
                modifier = Modifier.padding(MovieExplorerSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                    Text(
                        text = movie.rating,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = movie.releaseYear,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = movie.genres.joinToString(" / "),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .testTag(MovieExplorerTestTags.HomeFavoriteButton)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f)),
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isFavorite) "Remove favorite" else "Add favorite",
            tint = if (isFavorite) {
                MaterialTheme.colorScheme.tertiary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.testTag(MovieExplorerTestTags.HomeLoading),
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MovieExplorerPlaceholderScreen(
        title = "Movies could not load",
        subtitle = message,
        icon = Icons.Outlined.Warning,
        modifier = modifier,
        action = PlaceholderAction(
            label = "Retry",
            onClick = onRetry,
        ),
    )
}

@Composable
private fun EmptyState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MovieExplorerPlaceholderScreen(
        title = "No trending movies yet",
        subtitle = "Pull to refresh or try again to ask the fake API for movies.",
        icon = Icons.Outlined.Home,
        modifier = modifier,
        action = PlaceholderAction(
            label = "Refresh",
            onClick = onRetry,
        ),
    )
}

@Composable
private fun InlineErrorBanner(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AssistChip(
        onClick = onRetry,
        label = {
            Text(
                text = "$message Tap to retry.",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
            )
        },
        modifier = modifier,
    )
    Spacer(modifier = Modifier.height(MovieExplorerSpacing.small))
}
