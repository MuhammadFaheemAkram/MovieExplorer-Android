package com.example.movieexplorer.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.movieexplorer.feature.details.DetailsRoute
import com.example.movieexplorer.feature.favorites.FavoritesRoute
import com.example.movieexplorer.feature.home.HomeRoute
import com.example.movieexplorer.feature.search.SearchRoute
import com.example.movieexplorer.feature.settings.AboutScreen
import com.example.movieexplorer.feature.settings.SettingsRoute

@Composable
fun MovieExplorerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MovieExplorerRoute.Home.route,
        modifier = modifier,
    ) {
        composable(route = MovieExplorerRoute.Home.route) {
            HomeRoute(
                onMovieClick = { movieId ->
                    navController.navigate(MovieExplorerRoute.Details.createRoute(movieId))
                },
            )
        }
        composable(route = MovieExplorerRoute.Search.route) {
            SearchRoute(
                onMovieClick = { movieId ->
                    navController.navigate(MovieExplorerRoute.Details.createRoute(movieId))
                },
            )
        }
        composable(route = MovieExplorerRoute.Favorites.route) {
            FavoritesRoute(
                onMovieClick = { movieId ->
                    navController.navigate(MovieExplorerRoute.Details.createRoute(movieId))
                },
            )
        }
        composable(route = MovieExplorerRoute.Settings.route) {
            SettingsRoute()
        }
        composable(route = MovieExplorerRoute.About.route) {
            AboutScreen()
        }
        composable(
            route = MovieExplorerRoute.Details.routePattern,
            arguments = listOf(
                navArgument(MovieExplorerRoute.Details.MOVIE_ID_ARG) {
                    type = NavType.IntType
                },
            ),
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments
                ?.getInt(MovieExplorerRoute.Details.MOVIE_ID_ARG)
                ?: 0
            DetailsRoute(movieId = movieId)
        }
    }
}
