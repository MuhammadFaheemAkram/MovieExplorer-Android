package co.bitfuse.movieexplorer.core.navigation

sealed interface MovieExplorerRoute {
    val route: String
    val title: String

    data object Home : MovieExplorerRoute {
        override val route = "home"
        override val title = "Home"
    }

    data object Search : MovieExplorerRoute {
        override val route = "search"
        override val title = "Search"
    }

    data object Favorites : MovieExplorerRoute {
        override val route = "favorites"
        override val title = "Favorites"
    }

    data object Settings : MovieExplorerRoute {
        override val route = "settings"
        override val title = "Settings"
    }

    data object About : MovieExplorerRoute {
        override val route = "about"
        override val title = "About"
    }

    data object AddMovie : MovieExplorerRoute {
        override val route = "add_movie"
        override val title = "Add Movie"
    }

    data object EditMovie : MovieExplorerRoute {
        const val MOVIE_ID_ARG = "movieId"
        const val routePattern = "edit_movie/{$MOVIE_ID_ARG}"

        override val route = routePattern
        override val title = "Edit Movie"

        fun createRoute(movieId: Int) = "edit_movie/$movieId"
    }

    data object Details : MovieExplorerRoute {
        const val MOVIE_ID_ARG = "movieId"
        const val routePattern = "details/{$MOVIE_ID_ARG}"

        override val route = routePattern
        override val title = "Movie Details"

        fun createRoute(movieId: Int) = "details/$movieId"
    }
}
