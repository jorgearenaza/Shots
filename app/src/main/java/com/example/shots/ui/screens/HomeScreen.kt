package com.example.espressoshots.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.espressoshots.viewmodel.MainViewModel

@Composable
fun HomeScreen(navController: NavController, vm: MainViewModel) {
    val items = vm.shots
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        val list = items.collectAsState()
        Column(modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(padding)) {
            LazyColumn {
                items(list.value) { shot ->
                    ShotRow(shot.beanName, shot.dose, shot.yield)
                }
            }
        }
    }
}

@Composable
private fun ShotRow(bean: String, dose: Float, yield: Float) {
    Text(text = "$bean — dose ${dose}g → ${yield}g", modifier = androidx.compose.ui.Modifier.padding(16.dp))
}
