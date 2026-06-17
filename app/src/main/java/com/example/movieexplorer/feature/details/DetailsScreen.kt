package com.example.movieexplorer.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.movieexplorer.core.designsystem.components.MovieExplorerPlaceholderScreen
import com.example.movieexplorer.core.designsystem.components.PlaceholderAction
import com.example.movieexplorer.core.designsystem.theme.MovieExplorerSpacing

@Composable
fun DetailsRoute(
    movieId: Int,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    DetailsScreen(
        uiState = uiState,
        onRetry = { viewModel.loadMovie(movieId) },
        onFavoriteClick = viewModel::toggleFavorite,
        modifier = modifier,
    )
}

@Composable
fun DetailsScreen(
    uiState: MovieDetailsUiState,
    onRetry: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        MovieDetailsUiState.Loading -> LoadingState(modifier = modifier.fillMaxSize())
        is MovieDetailsUiState.Error -> ErrorState(
            message = uiState.message,
            onRetry = onRetry,
            modifier = modifier.fillMaxSize(),
        )

        is MovieDetailsUiState.Success -> DetailsContent(
            movie = uiState.movie,
            onFavoriteClick = onFavoriteClick,
            modifier = modifier.fillMaxSize(),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailsContent(
    movie: MovieDetailsUiModel,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
    ) {
        item {
            Box {
                AsyncImage(
                    model = movie.backdropUrl,
                    contentDescription = "${movie.title} backdrop",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = MovieExplorerSpacing.large),
                verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
                    verticalAlignment = Alignment.Top,
                ) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = "${movie.title} poster",
                        modifier = Modifier
                            .fillMaxWidth(0.32f)
                            .aspectRatio(2f / 3f)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp),
                            ),
                        contentScale = ContentScale.Crop,
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
                    ) {
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically,
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
                        FavoriteActionChip(
                            isFavorite = movie.isFavorite,
                            onClick = onFavoriteClick,
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
                    verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
                ) {
                    movie.genres.forEach { genre ->
                        AssistChip(
                            onClick = {},
                            label = { Text(text = genre) },
                        )
                    }
                }

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(MovieExplorerSpacing.large))
            }
        }
    }
}

@Composable
private fun FavoriteActionChip(
    isFavorite: Boolean,
    onClick: () -> Unit,
) {
    ElevatedAssistChip(
        onClick = onClick,
        label = {
            Text(text = if (isFavorite) "Remove favorite" else "Add favorite")
        },
        leadingIcon = {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
            )
        },
    )
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MovieExplorerPlaceholderScreen(
        title = "Details could not load",
        subtitle = message,
        icon = Icons.Outlined.Warning,
        modifier = modifier,
        action = PlaceholderAction(
            label = "Retry",
            onClick = onRetry,
        ),
    )
}
