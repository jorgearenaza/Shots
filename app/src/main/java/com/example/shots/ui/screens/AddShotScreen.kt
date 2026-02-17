package com.example.espressoshots.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.espressoshots.viewmodel.MainViewModel

@Composable
fun AddShotScreen(navController: NavController, vm: MainViewModel) {
    val bean = remember { mutableStateOf("") }
    val grinder = remember { mutableStateOf("") }
    val dose = remember { mutableStateOf("18") }
    val yield = remember { mutableStateOf("36") }
    val time = remember { mutableStateOf("25") }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = bean.value, onValueChange = { bean.value = it }, label = { Text("Bean") })
        OutlinedTextField(value = grinder.value, onValueChange = { grinder.value = it }, label = { Text("Grinder") })
        OutlinedTextField(value = dose.value, onValueChange = { dose.value = it }, label = { Text("Dose (g)") })
        OutlinedTextField(value = yield.value, onValueChange = { yield.value = it }, label = { Text("Yield (g)") })
        OutlinedTextField(value = time.value, onValueChange = { time.value = it }, label = { Text("Time (s)") })
        Button(onClick = {
            val d = dose.value.toFloatOrNull() ?: 18f
            val y = yield.value.toFloatOrNull() ?: 36f
            val t = time.value.toIntOrNull() ?: 25
            vm.addShot(bean.value.ifBlank { "Unknown" }, grinder.value.ifBlank { "Unknown" }, d, y, t)
            navController.navigateUp()
        }) {
            Text("Save")
        }
    }
}
