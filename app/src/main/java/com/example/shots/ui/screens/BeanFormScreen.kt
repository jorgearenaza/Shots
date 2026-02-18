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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.espressoshots.data.model.BeanEntity
import com.example.espressoshots.ui.components.DateField
import com.example.espressoshots.viewmodel.MainViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun BeanFormScreen(
    navController: NavController,
    vm: MainViewModel,
    beanId: Long?,
    padding: PaddingValues
) {
    var tostador by remember { mutableStateOf("") }
    var cafe by remember { mutableStateOf("") }
    var fechaTostado by remember { mutableStateOf(System.currentTimeMillis()) }
    var fechaCompra by remember { mutableStateOf(System.currentTimeMillis()) }
    var notas by remember { mutableStateOf("") }
    var createdAt by remember { mutableStateOf<Long?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(beanId) {
        if (beanId != null) {
            val bean = vm.getBean(beanId)
            if (bean != null) {
                tostador = bean.tostador
                cafe = bean.cafe
                fechaTostado = bean.fechaTostado
                fechaCompra = bean.fechaCompra
                notas = bean.notas ?: ""
                createdAt = bean.createdAt
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(value = tostador, onValueChange = { tostador = it }, label = { Text("Tostador") })
        OutlinedTextField(value = cafe, onValueChange = { cafe = it }, label = { Text("Cafe") })
        DateField(label = "Fecha de tostado", valueMillis = fechaTostado, onValueChange = { fechaTostado = it })
        DateField(label = "Fecha de compra", valueMillis = fechaCompra, onValueChange = { fechaCompra = it })
        OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Notas") })

        if (error != null) {
            Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                if (tostador.isBlank() || cafe.isBlank()) {
                    error = "Tostador y cafe son obligatorios."
                    return@Button
                }
                scope.launch {
                    try {
                        error = null
                        if (beanId == null) {
                            vm.saveBean(
                                id = null,
                                tostador = tostador.trim(),
                                cafe = cafe.trim(),
                                fechaTostado = fechaTostado,
                                fechaCompra = fechaCompra,
                                notas = notas.ifBlank { null }
                            )
                        } else {
                            vm.updateBeanEntity(
                                BeanEntity(
                                    id = beanId,
                                    tostador = tostador.trim(),
                                    cafe = cafe.trim(),
                                    fechaTostado = fechaTostado,
                                    fechaCompra = fechaCompra,
                                    notas = notas.ifBlank { null },
                                    activo = true,
                                    createdAt = createdAt ?: System.currentTimeMillis(),
                                    updatedAt = System.currentTimeMillis()
                                )
                            )
                        }
                        navController.navigateUp()
                    } catch (e: Exception) {
                        if (e.message?.contains("UNIQUE") == true || e.message?.contains("unique") == true) {
                            error = "Ya existe un grano con ese tostador y café."
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

        if (beanId != null) {
            Button(
                onClick = {
                    vm.deactivateBean(beanId)
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
