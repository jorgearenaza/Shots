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
import com.example.espressoshots.data.model.ProfileEntity
import com.example.espressoshots.viewmodel.MainViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ProfileFormScreen(
    navController: NavController,
    vm: MainViewModel,
    profileId: Long?,
    padding: PaddingValues
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var parametros by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(profileId) {
        if (profileId != null) {
            val profile = vm.getProfile(profileId)
            if (profile != null) {
                nombre = profile.nombre
                descripcion = profile.descripcion ?: ""
                parametros = profile.parametros ?: ""
                createdAt = profile.createdAt
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") })
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripcion") })
        OutlinedTextField(value = parametros, onValueChange = { parametros = it }, label = { Text("Parametros") })

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
                        if (profileId == null) {
                            vm.saveProfile(
                                id = null,
                                nombre = nombre.trim(),
                                descripcion = descripcion.ifBlank { null },
                                parametros = parametros.ifBlank { null }
                            )
                        } else {
                            vm.updateProfileEntity(
                                ProfileEntity(
                                    id = profileId,
                                    nombre = nombre.trim(),
                                    descripcion = descripcion.ifBlank { null },
                                    parametros = parametros.ifBlank { null },
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
                            error = "Ya existe un perfil con ese nombre."
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

        if (profileId != null) {
            Button(
                onClick = {
                    vm.deactivateProfile(profileId)
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
