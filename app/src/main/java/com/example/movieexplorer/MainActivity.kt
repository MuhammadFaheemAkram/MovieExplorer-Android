package com.example.movieexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movieexplorer.core.designsystem.theme.MovieExplorerTheme
import com.example.movieexplorer.core.navigation.MovieExplorerRoot
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings = viewModel.settings.collectAsStateWithLifecycle()

            MovieExplorerTheme(darkTheme = settings.value.darkModeEnabled) {
                MovieExplorerRoot()
            }
        }
    }
}
