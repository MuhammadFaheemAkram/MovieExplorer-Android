package co.bitfuse.movieexplorer.feature.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.bitfuse.movieexplorer.core.designsystem.components.MovieExplorerPlaceholderScreen

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
) {
    MovieExplorerPlaceholderScreen(
        title = "About Movie Explorer",
        subtitle = "A small, practical app shell for learning modern Android architecture.",
        icon = Icons.Outlined.Info,
        modifier = modifier,
    )
}
