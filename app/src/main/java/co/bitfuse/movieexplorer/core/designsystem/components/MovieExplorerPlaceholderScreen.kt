package co.bitfuse.movieexplorer.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerSpacing

data class PlaceholderAction(
    val label: String,
    val onClick: () -> Unit,
)

// Phase 1 keeps screens stateless so later ViewModels can own state and events cleanly.
@Composable
fun MovieExplorerPlaceholderScreen(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    action: PlaceholderAction? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MovieExplorerSpacing.large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = title,
            modifier = Modifier.padding(top = MovieExplorerSpacing.medium),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )
        Text(
            text = subtitle,
            modifier = Modifier.padding(top = MovieExplorerSpacing.small),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        if (action != null) {
            Button(
                onClick = action.onClick,
                modifier = Modifier.padding(top = MovieExplorerSpacing.large),
            ) {
                Text(text = action.label)
            }
        }
    }
}
