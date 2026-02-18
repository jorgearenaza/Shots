package com.example.espressoshots.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.espressoshots.data.model.GrinderEntity
import com.example.espressoshots.viewmodel.MainViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

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
        OutlinedTextField(
            value = ajusteDefault,
            onValueChange = { ajusteDefault = it },
            label = { Text("Ajuste default") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
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
                scope.launch {
                    try {
                        error = null
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
                    } catch (e: Exception) {
                        val msg = e.message ?: ""
                        if (msg.contains("UNIQUE", ignoreCase = true) || msg.contains("constraint", ignoreCase = true)) {
                            error = "Ya existe un molino con ese nombre."
                        } else {
                            error = "Error al guardar: ${e.message}"
                        }
                    }
                }
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
