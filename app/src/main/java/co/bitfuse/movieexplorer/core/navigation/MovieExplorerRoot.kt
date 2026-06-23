package co.bitfuse.movieexplorer.core.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerSpacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieExplorerRoot(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route ?: MovieExplorerRoute.Home.route
    val isDetails = currentRoute == MovieExplorerRoute.Details.routePattern
    val showBottomBar = BottomBarDestinations.any { it.route.route == currentRoute }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = !isDetails,
        drawerContent = {
            MovieExplorerDrawerContent(
                currentDestination = currentDestination,
                onDestinationClick = { route ->
                    scope.launch {
                        drawerState.close()
                        navController.navigateToTopLevel(route.route)
                    }
                },
            )
        },
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                TopAppBar(
                    title = { Text(text = currentRoute.toScreenTitle()) },
                    navigationIcon = {
                        if (isDetails) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go back",
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    scope.launch { drawerState.open() }
                                },
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Open navigation drawer",
                                )
                            }
                        }
                    },
                )
            },
            bottomBar = {
                if (showBottomBar) {
                    MovieExplorerBottomBar(
                        currentDestination = currentDestination,
                        onDestinationClick = { route ->
                            navController.navigateToTopLevel(route.route)
                        },
                    )
                }
            },
            contentWindowInsets = WindowInsets(0),
        ) { innerPadding ->
            MovieExplorerNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}

@Composable
private fun MovieExplorerDrawerContent(
    currentDestination: NavDestination?,
    onDestinationClick: (MovieExplorerRoute) -> Unit,
) {
    ModalDrawerSheet {
        Text(
            text = "Movie Explorer",
            modifier = Modifier.padding(MovieExplorerSpacing.large),
            style = MaterialTheme.typography.titleLarge,
        )
        DrawerDestinations.forEach { destination ->
            val selected = currentDestination.isRouteSelected(destination.route.route)
            NavigationDrawerItem(
                label = { Text(text = destination.route.title) },
                selected = selected,
                onClick = { onDestinationClick(destination.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        },
                        contentDescription = null,
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
            )
        }
        Spacer(modifier = Modifier.height(MovieExplorerSpacing.medium))
    }
}

@Composable
private fun MovieExplorerBottomBar(
    currentDestination: NavDestination?,
    onDestinationClick: (MovieExplorerRoute) -> Unit,
) {
    NavigationBar {
        BottomBarDestinations.forEach { destination ->
            val selected = currentDestination.isRouteSelected(destination.route.route)
            NavigationBarItem(
                selected = selected,
                onClick = { onDestinationClick(destination.route) },
                icon = {
                    Icon(
                        imageVector = if (selected) {
                            destination.selectedIcon
                        } else {
                            destination.unselectedIcon
                        },
                        contentDescription = null,
                    )
                },
                label = { Text(text = destination.route.title) },
            )
        }
    }
}

private fun NavHostController.navigateToTopLevel(route: String) {
    navigate(
        route = route,
        navOptions = navOptions {
            popUpTo(graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        },
    )
}

private fun NavDestination?.isRouteSelected(route: String): Boolean = this?.route == route

private fun String.toScreenTitle(): String = when (this) {
    MovieExplorerRoute.Home.route -> MovieExplorerRoute.Home.title
    MovieExplorerRoute.Search.route -> MovieExplorerRoute.Search.title
    MovieExplorerRoute.Favorites.route -> MovieExplorerRoute.Favorites.title
    MovieExplorerRoute.Settings.route -> MovieExplorerRoute.Settings.title
    MovieExplorerRoute.About.route -> MovieExplorerRoute.About.title
    MovieExplorerRoute.Details.routePattern -> MovieExplorerRoute.Details.title
    else -> MovieExplorerRoute.Home.title
}
