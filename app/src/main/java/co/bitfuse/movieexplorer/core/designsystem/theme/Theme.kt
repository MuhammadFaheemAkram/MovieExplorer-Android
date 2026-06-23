package co.bitfuse.movieexplorer.core.designsystem.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = CinemaBlue,
    onPrimary = Color.White,
    secondary = EmeraldAccent,
    onSecondary = Color.White,
    tertiary = AmberHighlight,
    background = LightBackground,
    surface = LightSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = CinemaBlueDark,
    onPrimary = Color(0xFF0A1B47),
    secondary = EmeraldAccentDark,
    onSecondary = Color(0xFF062818),
    tertiary = AmberHighlightDark,
    onTertiary = Color(0xFF3A1D00),
    background = DarkBackground,
    surface = DarkSurface,
)

@Composable
fun MovieExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MovieExplorerTypography,
        content = content,
    )
}
