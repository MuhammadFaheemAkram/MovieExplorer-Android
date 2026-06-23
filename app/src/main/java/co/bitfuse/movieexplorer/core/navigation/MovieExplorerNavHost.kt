package co.bitfuse.movieexplorer.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import co.bitfuse.movieexplorer.feature.details.DetailsRoute
import co.bitfuse.movieexplorer.feature.favorites.FavoritesRoute
import co.bitfuse.movieexplorer.feature.home.HomeRoute
import co.bitfuse.movieexplorer.feature.search.SearchRoute
import co.bitfuse.movieexplorer.feature.settings.AboutScreen
import co.bitfuse.movieexplorer.feature.settings.SettingsRoute

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
