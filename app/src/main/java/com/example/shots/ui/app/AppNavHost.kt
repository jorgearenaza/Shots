package com.example.espressoshots.ui.app

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.espressoshots.data.ShotsRepository
import com.example.espressoshots.data.ServiceLocator
import com.example.espressoshots.viewmodel.MainViewModel
import com.example.espressoshots.ui.screens.AddShotScreen
import com.example.espressoshots.ui.screens.HomeScreen

@Composable
fun AppNavHost(repository: ShotsRepository) {
    val nav = rememberNavController()
    val vm: MainViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
    })

    NavHost(navController = nav, startDestination = "home") {
        composable("home") { HomeScreen(navController = nav, vm = vm) }
        composable("add") { AddShotScreen(navController = nav, vm = vm) }
    }
}
