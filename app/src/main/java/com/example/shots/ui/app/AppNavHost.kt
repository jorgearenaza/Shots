package com.example.espressoshots.ui.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.espressoshots.data.ShotsRepository
import com.example.espressoshots.ui.screens.BeanFormScreen
import com.example.espressoshots.ui.screens.BeansScreen
import com.example.espressoshots.ui.screens.GrinderFormScreen
import com.example.espressoshots.ui.screens.GrindersScreen
import com.example.espressoshots.ui.screens.OptionsScreen
import com.example.espressoshots.ui.screens.ProfileFormScreen
import com.example.espressoshots.ui.screens.ProfilesScreen
import com.example.espressoshots.ui.screens.ShotFormScreen
import com.example.espressoshots.ui.screens.ShotsScreen
import com.example.espressoshots.viewmodel.MainViewModel

private data class TopLevelRoute(
    val route: String,
    val label: String,
    val icon: @Composable () -> Unit
)

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
        TopLevelRoute("beans", "Granos") { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
        TopLevelRoute("grinders", "Molinos") { Icon(Icons.Default.Build, contentDescription = null) },
        TopLevelRoute("profiles", "Perfiles") { Icon(Icons.Default.Person, contentDescription = null) },
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

    Scaffold(
        bottomBar = {
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
        },
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
        NavHost(navController = nav, startDestination = "shots") {
            composable("shots") { ShotsScreen(navController = nav, vm = vm, padding = padding) }
            composable("shots/new") { ShotFormScreen(navController = nav, vm = vm, shotId = null, padding = padding) }
            composable("shots/edit/{id}") { entry ->
                val id = entry.arguments?.getString("id")?.toLongOrNull()
                ShotFormScreen(navController = nav, vm = vm, shotId = id, padding = padding)
            }

            composable("beans") { BeansScreen(navController = nav, vm = vm, padding = padding) }
            composable("beans/new") { BeanFormScreen(navController = nav, vm = vm, beanId = null, padding = padding) }
            composable("beans/edit/{id}") { entry ->
                val id = entry.arguments?.getString("id")?.toLongOrNull()
                BeanFormScreen(navController = nav, vm = vm, beanId = id, padding = padding)
            }

            composable("grinders") { GrindersScreen(navController = nav, vm = vm, padding = padding) }
            composable("grinders/new") { GrinderFormScreen(navController = nav, vm = vm, grinderId = null, padding = padding) }
            composable("grinders/edit/{id}") { entry ->
                val id = entry.arguments?.getString("id")?.toLongOrNull()
                GrinderFormScreen(navController = nav, vm = vm, grinderId = id, padding = padding)
            }

            composable("profiles") { ProfilesScreen(navController = nav, vm = vm, padding = padding) }
            composable("profiles/new") { ProfileFormScreen(navController = nav, vm = vm, profileId = null, padding = padding) }
            composable("profiles/edit/{id}") { entry ->
                val id = entry.arguments?.getString("id")?.toLongOrNull()
                ProfileFormScreen(navController = nav, vm = vm, profileId = id, padding = padding)
            }

            composable("options") { OptionsScreen(navController = nav, vm = vm, padding = padding) }
        }
    }
}
