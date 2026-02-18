package com.example.shots.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shots.data.model.BeanEntity
import com.example.shots.ui.components.DateField
import com.example.shots.ui.components.DropdownField
import com.example.shots.ui.components.SectionHeader
import com.example.shots.ui.theme.AppSpacing
import com.example.shots.viewmodel.MainViewModel
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
    var pais by remember { mutableStateOf("") }
    var proceso by remember { mutableStateOf("") }
    var varietal by remember { mutableStateOf("") }
    var altitud by remember { mutableStateOf("") }
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
                pais = bean.pais ?: ""
                proceso = bean.proceso ?: ""
                varietal = bean.varietal ?: ""
                altitud = bean.altitud?.toString() ?: ""
                createdAt = bean.createdAt
            }
        }
    }

    // Opciones para proceso y varietal
    val procesoOptions = listOf("N/A", "Lavado", "Natural", "Honey", "Wet Hulled", "Anaeróbico", "Semi-Lavado")
    val varietalOptions = listOf("N/A", "Caturra", "Bourbon", "Typica", "Gesha", "Catuai", "Castillo", "SL28", "SL34", "Pacamara", "Maragogipe")
    
    val procesoDisplay = proceso.ifBlank { "N/A" }
    val varietalDisplay = varietal.ifBlank { "N/A" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(AppSpacing.large)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
    ) {
        SectionHeader(icon = "📝", title = "Información Básica")
        
        OutlinedTextField(value = tostador, onValueChange = { tostador = it }, label = { Text("Tostador") })
        OutlinedTextField(value = cafe, onValueChange = { cafe = it }, label = { Text("Café") })
        DateField(label = "Fecha de tostado", valueMillis = fechaTostado, onValueChange = { fechaTostado = it })
        DateField(label = "Fecha de compra", valueMillis = fechaCompra, onValueChange = { fechaCompra = it })
        
        SectionHeader(
            icon = "☕",
            title = "Origen y Proceso",
            modifier = Modifier.padding(top = AppSpacing.small)
        )
        OutlinedTextField(
            value = pais,
            onValueChange = { pais = it },
            label = { Text("País") },
            placeholder = { Text("Ej: Colombia, Etiopía, Brasil") }
        )
        DropdownField(
            label = "Proceso",
            value = procesoDisplay,
            options = procesoOptions,
            onSelect = { idx -> proceso = if (idx == 0) "" else procesoOptions[idx] }
        )
        DropdownField(
            label = "Varietal",
            value = varietalDisplay,
            options = varietalOptions,
            onSelect = { idx -> varietal = if (idx == 0) "" else varietalOptions[idx] }
        )
        OutlinedTextField(
            value = altitud,
            onValueChange = { altitud = it },
            label = { Text("Altitud (msnm)") },
            placeholder = { Text("Ej: 1600") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        
        SectionHeader(
            icon = "📝",
            title = "Notas",
            modifier = Modifier.padding(top = AppSpacing.small)
        )
        OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Notas adicionales") })

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
                                notas = notas.ifBlank { null },
                                pais = pais.ifBlank { null },
                                proceso = proceso.ifBlank { null },
                                varietal = varietal.ifBlank { null },
                                altitud = altitud.toIntOrNull()
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
                                    pais = pais.ifBlank { null },
                                    proceso = proceso.ifBlank { null },
                                    varietal = varietal.ifBlank { null },
                                    altitud = altitud.toIntOrNull(),
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
