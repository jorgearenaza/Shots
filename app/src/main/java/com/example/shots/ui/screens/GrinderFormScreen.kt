package com.example.espressoshots.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.espressoshots.data.model.GrinderEntity
import com.example.espressoshots.viewmodel.MainViewModel

@Composable
fun GrinderFormScreen(
    navController: NavController,
    vm: MainViewModel,
    grinderId: Long?,
    padding: PaddingValues
) {
    var nombre by remember { mutableStateOf("") }
    var ajusteDefault by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(grinderId) {
        if (grinderId != null) {
            val grinder = vm.getGrinder(grinderId)
            if (grinder != null) {
                nombre = grinder.nombre
                ajusteDefault = grinder.ajusteDefault ?: ""
                notas = grinder.notas ?: ""
                createdAt = grinder.createdAt
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        OutlinedTextField(value = ajusteDefault, onValueChange = { ajusteDefault = it }, label = { Text("Ajuste default") })
        OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Notas") })

        if (error != null) {
            Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                if (nombre.isBlank()) {
                    error = "Nombre es obligatorio."
                    return@Button
                }
                if (grinderId == null) {
                    vm.saveGrinder(
                        id = null,
                        nombre = nombre.trim(),
                        ajusteDefault = ajusteDefault.ifBlank { null },
                        notas = notas.ifBlank { null }
                    )
                } else {
                    vm.updateGrinderEntity(
                        GrinderEntity(
                            id = grinderId,
                            nombre = nombre.trim(),
                            ajusteDefault = ajusteDefault.ifBlank { null },
                            notas = notas.ifBlank { null },
                            activo = true,
                            createdAt = createdAt ?: System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                navController.navigateUp()
            },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Guardar")
        }
            if (grinderId != null) {
                Button(
                    onClick = {
                        vm.deactivateGrinder(grinderId)
                        navController.navigateUp()
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Desactivar")
                }
            }
    }
}
