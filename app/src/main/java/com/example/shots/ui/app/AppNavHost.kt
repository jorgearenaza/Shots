package com.example.shots.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.shots.data.ShotsRepository
import com.example.shots.ui.screens.BeanFormScreen
import com.example.shots.ui.screens.BeansScreen
import com.example.shots.ui.screens.GrinderFormScreen
import com.example.shots.ui.screens.GrindersScreen
import com.example.shots.ui.screens.OptionsScreen
import com.example.shots.ui.screens.ProfileFormScreen
import com.example.shots.ui.screens.ProfilesScreen
import com.example.shots.ui.screens.ShotFormScreen
import com.example.shots.ui.screens.ShotsScreen
import com.example.shots.ui.screens.StatsScreen
import com.example.shots.viewmodel.MainViewModel

private data class TopLevelRoute(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(repository: ShotsRepository) {
    val nav = rememberNavController()
    val vm: MainViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
    })

    val topRoutes = listOf(
        TopLevelRoute("shots", "Shots") { Icon(Icons.Default.Home, contentDescription = null) },
        TopLevelRoute("beans", "Granos") { Icon(Icons.Default.LocalCafe, contentDescription = null) },
        TopLevelRoute("grinders", "Molinos") { Icon(Icons.Default.Build, contentDescription = null) },
        TopLevelRoute("profiles", "Perfiles") { Icon(Icons.Default.Person, contentDescription = null) },
        TopLevelRoute("stats", "Stats") { Icon(Icons.Default.BarChart, contentDescription = null) },
        TopLevelRoute("options", "Opciones") { Icon(Icons.Default.Settings, contentDescription = null) }
    )

    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val fabRoute = when (currentRoute) {
        "shots" -> "shots/new"
        "beans" -> "beans/new"
        "grinders" -> "grinders/new"
        "profiles" -> "profiles/new"
        else -> null
    }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    val title = when (currentRoute) {
        "shots" -> "Shots"
        "shots/new" -> "Nuevo shot"
        "shots/edit/{id}" -> "Editar shot"
        "beans" -> "Granos"
        "beans/new" -> "Nuevo grano"
        "beans/edit/{id}" -> "Editar grano"
        "grinders" -> "Molinos"
        "grinders/new" -> "Nuevo molino"
        "grinders/edit/{id}" -> "Editar molino"
        "profiles" -> "Perfiles"
        "profiles/new" -> "Nuevo perfil"
        "profiles/edit/{id}" -> "Editar perfil"
        "stats" -> "Estadísticas"
        "options" -> "Opciones"
        else -> "EspressoShots"
    }
    val bottomBarContent: @Composable () -> Unit = {
        if (!isLandscape) {
            NavigationBar {
                topRoutes.forEach { item ->
                    val selected = backStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            nav.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(nav.graph.startDestinationId) { saveState = true }
                            }
                        },
                        icon = item.icon,
                        label = { androidx.compose.material3.Text(item.label) }
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) },
        bottomBar = bottomBarContent,
        floatingActionButton = {
            if (fabRoute != null) {
                FloatingActionButton(
                    onClick = { nav.navigate(fabRoute) },
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        }
    ) { padding ->
        val contentPadding = if (isLandscape) PaddingValues(0.dp) else padding
        if (isLandscape) {
            Row(modifier = Modifier.padding(padding)) {
                NavigationRail {
                    topRoutes.forEach { item ->
                        val selected = backStackEntry?.destination?.hierarchy?.any { it.route == item.route } == true
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                nav.navigate(item.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(nav.graph.startDestinationId) { saveState = true }
                                }
                            },
                            icon = item.icon,
                            label = { androidx.compose.material3.Text(item.label) }
                        )
                    }
                }
                NavHost(navController = nav, startDestination = "shots") {
                    composable("shots") { ShotsScreen(navController = nav, vm = vm, padding = contentPadding) }
                    composable("shots/new") { ShotFormScreen(navController = nav, vm = vm, shotId = null, padding = contentPadding) }
                    composable("shots/edit/{id}") { entry ->
                        val id = entry.arguments?.getString("id")?.toLongOrNull()
                        ShotFormScreen(navController = nav, vm = vm, shotId = id, padding = contentPadding)
                    }

                    composable("beans") { BeansScreen(navController = nav, vm = vm, padding = contentPadding) }
                    composable("beans/new") { BeanFormScreen(navController = nav, vm = vm, beanId = null, padding = contentPadding) }
                    composable("beans/edit/{id}") { entry ->
                        val id = entry.arguments?.getString("id")?.toLongOrNull()
                        BeanFormScreen(navController = nav, vm = vm, beanId = id, padding = contentPadding)
                    }

                    composable("grinders") { GrindersScreen(navController = nav, vm = vm, padding = contentPadding) }
                    composable("grinders/new") { GrinderFormScreen(navController = nav, vm = vm, grinderId = null, padding = contentPadding) }
                    composable("grinders/edit/{id}") { entry ->
                        val id = entry.arguments?.getString("id")?.toLongOrNull()
                        GrinderFormScreen(navController = nav, vm = vm, grinderId = id, padding = contentPadding)
                    }

                    composable("profiles") { ProfilesScreen(navController = nav, vm = vm, padding = contentPadding) }
                    composable("profiles/new") { ProfileFormScreen(navController = nav, vm = vm, profileId = null, padding = contentPadding) }
                    composable("profiles/edit/{id}") { entry ->
                        val id = entry.arguments?.getString("id")?.toLongOrNull()
                        ProfileFormScreen(navController = nav, vm = vm, profileId = id, padding = contentPadding)
                    }

                    composable("options") { OptionsScreen(navController = nav, vm = vm, padding = contentPadding) }
                    composable("stats") { StatsScreen(navController = nav, vm = vm, padding = contentPadding) }
                }
            }
        } else {
            NavHost(navController = nav, startDestination = "shots") {
                composable("shots") { ShotsScreen(navController = nav, vm = vm, padding = contentPadding) }
                composable("shots/new") { ShotFormScreen(navController = nav, vm = vm, shotId = null, padding = contentPadding) }
                composable("shots/edit/{id}") { entry ->
                    val id = entry.arguments?.getString("id")?.toLongOrNull()
                    ShotFormScreen(navController = nav, vm = vm, shotId = id, padding = contentPadding)
                }

                composable("beans") { BeansScreen(navController = nav, vm = vm, padding = contentPadding) }
                composable("beans/new") { BeanFormScreen(navController = nav, vm = vm, beanId = null, padding = contentPadding) }
                composable("beans/edit/{id}") { entry ->
                    val id = entry.arguments?.getString("id")?.toLongOrNull()
                    BeanFormScreen(navController = nav, vm = vm, beanId = id, padding = contentPadding)
                }

                composable("grinders") { GrindersScreen(navController = nav, vm = vm, padding = contentPadding) }
                composable("grinders/new") { GrinderFormScreen(navController = nav, vm = vm, grinderId = null, padding = contentPadding) }
                composable("grinders/edit/{id}") { entry ->
                    val id = entry.arguments?.getString("id")?.toLongOrNull()
                    GrinderFormScreen(navController = nav, vm = vm, grinderId = id, padding = contentPadding)
                }

                composable("profiles") { ProfilesScreen(navController = nav, vm = vm, padding = contentPadding) }
                composable("profiles/new") { ProfileFormScreen(navController = nav, vm = vm, profileId = null, padding = contentPadding) }
                composable("profiles/edit/{id}") { entry ->
                    val id = entry.arguments?.getString("id")?.toLongOrNull()
                    ProfileFormScreen(navController = nav, vm = vm, profileId = id, padding = contentPadding)
                }

                composable("options") { OptionsScreen(navController = nav, vm = vm, padding = contentPadding) }
                composable("stats") { StatsScreen(navController = nav, vm = vm, padding = contentPadding) }
            }
        }
    }
}
