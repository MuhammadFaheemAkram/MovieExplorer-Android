package com.example.movieexplorer.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite as FavoriteFilled
import androidx.compose.material.icons.filled.Home as HomeFilled
import androidx.compose.material.icons.filled.Info as InfoFilled
import androidx.compose.material.icons.filled.Search as SearchFilled
import androidx.compose.material.icons.filled.Settings as SettingsFilled
import androidx.compose.material.icons.outlined.FavoriteBorder as FavoriteBorderOutlined
import androidx.compose.material.icons.outlined.Home as HomeOutlined
import androidx.compose.material.icons.outlined.Info as InfoOutlined
import androidx.compose.material.icons.outlined.Search as SearchOutlined
import androidx.compose.material.icons.outlined.Settings as SettingsOutlined
import androidx.compose.ui.graphics.vector.ImageVector

data class TopLevelDestination(
    val route: MovieExplorerRoute,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)

val BottomBarDestinations = listOf(
    TopLevelDestination(
        route = MovieExplorerRoute.Home,
        selectedIcon = Icons.Filled.HomeFilled,
        unselectedIcon = Icons.Outlined.HomeOutlined,
    ),
    TopLevelDestination(
        route = MovieExplorerRoute.Search,
        selectedIcon = Icons.Filled.SearchFilled,
        unselectedIcon = Icons.Outlined.SearchOutlined,
    ),
    TopLevelDestination(
        route = MovieExplorerRoute.Favorites,
        selectedIcon = Icons.Filled.FavoriteFilled,
        unselectedIcon = Icons.Outlined.FavoriteBorderOutlined,
    ),
)

val DrawerDestinations = BottomBarDestinations + listOf(
    TopLevelDestination(
        route = MovieExplorerRoute.Settings,
        selectedIcon = Icons.Filled.SettingsFilled,
        unselectedIcon = Icons.Outlined.SettingsOutlined,
    ),
    TopLevelDestination(
        route = MovieExplorerRoute.About,
        selectedIcon = Icons.Filled.InfoFilled,
        unselectedIcon = Icons.Outlined.InfoOutlined,
    ),
)
