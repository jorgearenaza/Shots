package com.example.espressoshots.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.espressoshots.ui.components.AjusteMoliendaControl
import com.example.espressoshots.ui.components.DateField
import com.example.espressoshots.ui.components.DropdownField
import com.example.espressoshots.ui.components.EmptyState
import com.example.espressoshots.ui.components.RatingStars
import com.example.espressoshots.viewmodel.MainViewModel

@Composable
fun ShotFormScreen(
    navController: NavController,
    vm: MainViewModel,
    shotId: Long?,
    padding: PaddingValues
) {
    val beans = vm.beans.collectAsState()
    val grinders = vm.grinders.collectAsState()
    val profiles = vm.profiles.collectAsState()
    val settings = vm.settings.collectAsState()
    val configuration = LocalConfiguration.current
    val isWide = configuration.screenWidthDp >= 600

    if (beans.value.isEmpty()) {
        EmptyState(
            message = "No hay granos. Agrega un grano antes de registrar un shot.",
            actionLabel = "Agregar grano",
            onAction = { navController.navigate("beans/new") }
        )
        return
    }

    var loaded by remember { mutableStateOf(false) }
    var fechaMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var beanIndex by remember { mutableStateOf(0) }
    var grinderIndex by remember { mutableStateOf<Int?>(null) }
    var profileIndex by remember { mutableStateOf<Int?>(null) }
    var dosis by remember { mutableStateOf("") }
    var rendimiento by remember { mutableStateOf("") }
    var tiempoSeg by remember { mutableStateOf("") }
    var temperatura by remember { mutableStateOf("") }
    var ajuste by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var nextShotNotes by remember { mutableStateOf("") }
    var calificacion by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var createdAt by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(shotId) {
        if (shotId != null && !loaded) {
            val shot = vm.getShot(shotId)
            if (shot != null) {
                fechaMillis = shot.fecha
                beanIndex = beans.value.indexOfFirst { it.id == shot.beanId }.coerceAtLeast(0)
                grinderIndex = grinders.value.indexOfFirst { it.id == shot.molinoId }.takeIf { it >= 0 }
                profileIndex = profiles.value.indexOfFirst { it.id == shot.perfilId }.takeIf { it >= 0 }
                dosis = shot.dosisG.toString()
                rendimiento = shot.rendimientoG.toString()
                tiempoSeg = shot.tiempoSeg?.toString() ?: ""
                temperatura = shot.temperaturaC?.toString() ?: ""
                ajuste = shot.ajusteMolienda ?: ""
                notas = shot.notas ?: ""
                nextShotNotes = shot.nextShotNotes ?: ""
                calificacion = shot.calificacion?.toString() ?: ""
                createdAt = shot.createdAt
            }
            loaded = true
        } else if (shotId == null && !loaded) {
            if (settings.value.autofillShots) {
                dosis = settings.value.defaultDoseG.toString()
                rendimiento = settings.value.defaultYieldG.toString()
            }
            loaded = true
        }
    }

    val beanLabels = beans.value.map { "${it.tostador} - ${it.cafe}" }
    val grinderLabels = listOf("Sin molino") + grinders.value.map { it.nombre }
    val profileLabels = listOf("Sin perfil") + profiles.value.map { it.nombre }

    val ratioValue = run {
        val d = dosis.toDoubleOrNull() ?: 0.0
        val y = rendimiento.toDoubleOrNull() ?: 0.0
        if (d == 0.0) 0.0 else y / d
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DateField(label = "Fecha", valueMillis = fechaMillis, onValueChange = { fechaMillis = it })

                    DropdownField(
                        label = "Grano",
                        value = beanLabels.getOrElse(beanIndex) { "" },
                        options = beanLabels,
                        onSelect = { beanIndex = it }
                    )

                    if (grinders.value.isEmpty()) {
                        Text("No hay molinos. Agrega uno desde Molinos.")
                        Button(
                            onClick = { navController.navigate("grinders/new") },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text("Agregar molino")
                        }
                    } else {
                        DropdownField(
                            label = "Molino (opcional)",
                            value = grinderIndex?.let { grinderLabels.getOrNull(it + 1) } ?: "Sin molino",
                            options = grinderLabels,
                            onSelect = { idx -> grinderIndex = if (idx == 0) null else idx - 1 }
                        )
                    }

                    if (profiles.value.isEmpty()) {
                        Text("No hay perfiles. Agrega uno desde Perfiles.")
                        Button(
                            onClick = { navController.navigate("profiles/new") },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Text("Agregar perfil")
                        }
                    } else {
                        DropdownField(
                            label = "Perfil (opcional)",
                            value = profileIndex?.let { profileLabels.getOrNull(it + 1) } ?: "Sin perfil",
                            options = profileLabels,
                            onSelect = { idx -> profileIndex = if (idx == 0) null else idx - 1 }
                        )
                    }

                    OutlinedTextField(value = dosis, onValueChange = { dosis = it }, label = { Text("Dosis (g)") })
                    OutlinedTextField(value = rendimiento, onValueChange = { rendimiento = it }, label = { Text("Rendimiento (g)") })
                    Text(text = "Ratio: ${"%.2f".format(ratioValue)}", style = MaterialTheme.typography.bodyMedium)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(value = tiempoSeg, onValueChange = { tiempoSeg = it }, label = { Text("Tiempo (s)") })
                    OutlinedTextField(value = temperatura, onValueChange = { temperatura = it }, label = { Text("Temperatura (C)") })

                    AjusteMoliendaControl(value = ajuste, onValueChange = { ajuste = it })

                    OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Notas") })
                    OutlinedTextField(
                        value = nextShotNotes,
                        onValueChange = { nextShotNotes = it },
                        label = { Text("Para siguiente shot") }
                    )
                    RatingStars(
                        rating = calificacion.toIntOrNull() ?: 0,
                        max = 10,
                        onRatingChange = { calificacion = it.toString() }
                    )
                }
            }
        } else {
            DateField(label = "Fecha", valueMillis = fechaMillis, onValueChange = { fechaMillis = it })

            DropdownField(
                label = "Grano",
                value = beanLabels.getOrElse(beanIndex) { "" },
                options = beanLabels,
                onSelect = { beanIndex = it }
            )

            if (grinders.value.isEmpty()) {
                Text("No hay molinos. Agrega uno desde Molinos.")
                Button(
                    onClick = { navController.navigate("grinders/new") },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Agregar molino")
                }
            } else {
                DropdownField(
                    label = "Molino (opcional)",
                    value = grinderIndex?.let { grinderLabels.getOrNull(it + 1) } ?: "Sin molino",
                    options = grinderLabels,
                    onSelect = { idx -> grinderIndex = if (idx == 0) null else idx - 1 }
                )
            }

            if (profiles.value.isEmpty()) {
                Text("No hay perfiles. Agrega uno desde Perfiles.")
                Button(
                    onClick = { navController.navigate("profiles/new") },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Agregar perfil")
                }
            } else {
                DropdownField(
                    label = "Perfil (opcional)",
                    value = profileIndex?.let { profileLabels.getOrNull(it + 1) } ?: "Sin perfil",
                    options = profileLabels,
                    onSelect = { idx -> profileIndex = if (idx == 0) null else idx - 1 }
                )
            }

            OutlinedTextField(value = dosis, onValueChange = { dosis = it }, label = { Text("Dosis (g)") })
            OutlinedTextField(value = rendimiento, onValueChange = { rendimiento = it }, label = { Text("Rendimiento (g)") })
            Text(text = "Ratio: ${"%.2f".format(ratioValue)}", style = MaterialTheme.typography.bodyMedium)
            OutlinedTextField(value = tiempoSeg, onValueChange = { tiempoSeg = it }, label = { Text("Tiempo (s)") })
            OutlinedTextField(value = temperatura, onValueChange = { temperatura = it }, label = { Text("Temperatura (C)") })

            AjusteMoliendaControl(value = ajuste, onValueChange = { ajuste = it })

            OutlinedTextField(value = notas, onValueChange = { notas = it }, label = { Text("Notas") })
            OutlinedTextField(
                value = nextShotNotes,
                onValueChange = { nextShotNotes = it },
                label = { Text("Para siguiente shot") }
            )
            RatingStars(
                rating = calificacion.toIntOrNull() ?: 0,
                max = 10,
                onRatingChange = { calificacion = it.toString() }
            )
        }

        if (error != null) {
            Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = {
                val d = dosis.toDoubleOrNull()
                val y = rendimiento.toDoubleOrNull()
                if (d == null || y == null) {
                    error = "Dosis y rendimiento son obligatorios."
                    return@Button
                }
                if (beanIndex !in beans.value.indices) {
                    error = "Selecciona un grano valido."
                    return@Button
                }
                val beanId = beans.value[beanIndex].id
                val grinderId = grinderIndex?.let { grinders.value.getOrNull(it)?.id }
                val profileId = profileIndex?.let { profiles.value.getOrNull(it)?.id }
                val tiempo = tiempoSeg.toIntOrNull()
                val temp = temperatura.toDoubleOrNull()
                val rating = calificacion.toIntOrNull()?.coerceIn(1, 10)

                if (shotId == null) {
                    vm.addShot(
                        fecha = fechaMillis,
                        beanId = beanId,
                        molinoId = grinderId,
                        perfilId = profileId,
                        dosisG = d,
                        rendimientoG = y,
                        tiempoSeg = tiempo,
                        temperaturaC = temp,
                        ajusteMolienda = ajuste.ifBlank { null },
                        notas = notas.ifBlank { null },
                        nextShotNotes = nextShotNotes.ifBlank { null },
                        calificacion = rating
                    )
                } else {
                    vm.updateShot(
                        com.example.espressoshots.data.model.ShotEntity(
                            id = shotId,
                            fecha = fechaMillis,
                            beanId = beanId,
                            molinoId = grinderId,
                            perfilId = profileId,
                            dosisG = d,
                            rendimientoG = y,
                            ratio = if (d == 0.0) 0.0 else y / d,
                            tiempoSeg = tiempo,
                            temperaturaC = temp,
                            ajusteMolienda = ajuste.ifBlank { null },
                            notas = notas.ifBlank { null },
                            nextShotNotes = nextShotNotes.ifBlank { null },
                            calificacion = rating,
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
    }
}
