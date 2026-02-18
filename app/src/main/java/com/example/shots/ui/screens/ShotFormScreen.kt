package com.example.shots.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shots.ui.components.AjusteMoliendaControl
import com.example.shots.ui.components.DateField
import com.example.shots.ui.components.DropdownField
import com.example.shots.ui.components.EmptyState
import com.example.shots.ui.components.RatingStars
import com.example.shots.ui.components.SectionHeaderCompact
import com.example.shots.ui.theme.AppSpacing
import com.example.shots.viewmodel.MainViewModel

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
    // Pre-infusión
    var preinfusionTiempo by remember { mutableStateOf("") }
    var preinfusionPresion by remember { mutableStateOf("") }
    // Tasting notes estructuradas
    var aromaNotes by remember { mutableStateOf("") }
    var saborNotes by remember { mutableStateOf("") }
    var cuerpo by remember { mutableStateOf("") }
    var acidez by remember { mutableStateOf("") }
    var finish by remember { mutableStateOf("") }
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
                preinfusionTiempo = shot.preinfusionTiempoSeg?.toString() ?: ""
                preinfusionPresion = shot.preinfusionPresionBar?.toString() ?: ""
                aromaNotes = shot.aromaNotes ?: ""
                saborNotes = shot.saborNotes ?: ""
                cuerpo = shot.cuerpo ?: ""
                acidez = shot.acidez ?: ""
                finish = shot.finish ?: ""
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
            // Seleccionar molino por defecto si existe y llenar su configuración
            if (settings.value.defaultMolinoId != null) {
                grinderIndex = grinders.value.indexOfFirst { it.id == settings.value.defaultMolinoId }.takeIf { it >= 0 }
                val defaultGrinder = grinders.value.find { it.id == settings.value.defaultMolinoId }
                if (defaultGrinder != null && defaultGrinder.ajusteDefault != null) {
                    ajuste = defaultGrinder.ajusteDefault
                }
            }
            loaded = true
        }
    }

    val beanLabels = beans.value.map { "${it.tostador} - ${it.cafe}" }
    val grinderLabels = listOf("Sin molino") + grinders.value.map { it.nombre }
    val profileLabels = listOf("Sin perfil") + profiles.value.map { it.nombre }
    val nextShotOptions = listOf(
        "Sin sugerencia",
        "Mas Grueso",
        "Mas fino",
        "+ Temp",
        "- Temp",
        "+ Yield",
        "- Yield",
        "+ PreInf",
        "- PreInf",
        "Cambiar Perfil",
        "Mejorar Puck Prep"
    )
    val nextShotDisplay = if (nextShotNotes.isBlank()) "Sin sugerencia" else nextShotNotes

    // Opciones para tasting notes
    val aromaOptions = listOf("N/A", "Floral", "Frutal", "Cítrico", "Chocolate", "Nueces", "Caramelo", "Vainilla", "Especiado", "Herbal")
    val saborOptions = listOf("N/A", "Chocolate", "Caramelo", "Frutas rojas", "Frutas cítricas", "Nueces", "Avellana", "Miel", "Panela", "Frutas tropicales")
    val cuerpoOptions = listOf("N/A", "Ligero", "Medio-Ligero", "Medio", "Medio-Completo", "Completo")
    val acidezOptions = listOf("N/A", "Baja", "Media-Baja", "Media", "Media-Alta", "Alta", "Cítrica", "Málica", "Tánica")
    val finishOptions = listOf("N/A", "Corto", "Medio", "Largo", "Dulce", "Limpio", "Seco", "Cremoso")
    
    val aromaDisplay = aromaNotes.ifBlank { "N/A" }
    val saborDisplay = saborNotes.ifBlank { "N/A" }
    val cuerpoDisplay = cuerpo.ifBlank { "N/A" }
    val acidezDisplay = acidez.ifBlank { "N/A" }
    val finishDisplay = finish.ifBlank { "N/A" }

    val ratioValue = run {
        val d = dosis.toDoubleOrNull() ?: 0.0
        val y = rendimiento.toDoubleOrNull() ?: 0.0
        if (d == 0.0) 0.0 else y / d
    }

    // Estados de expansión de secciones
    var expandPreInfusion by remember { mutableStateOf(false) }
    var expandTasting by remember { mutableStateOf(false) }
    var expandNextShot by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = AppSpacing.medium, vertical = AppSpacing.small)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ========== ROW 1: SETUP & MOLIENDA ==========
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionCard(
                title = "Setup",
                icon = "⚙️",
                modifier = Modifier.weight(1f)
            ) {
                DateField(label = "Fecha", valueMillis = fechaMillis, onValueChange = { fechaMillis = it })

                DropdownField(
                    label = "Grano",
                    value = beanLabels.getOrElse(beanIndex) { "" },
                    options = beanLabels,
                    onSelect = { beanIndex = it }
                )
            }

            SectionCard(
                title = "Molienda",
                icon = "🌾",
                modifier = Modifier.weight(1f)
            ) {
                AjusteMoliendaControl(value = ajuste, onValueChange = { ajuste = it })
            }
        }

        // ========== MOLINO Y PERFIL ==========
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("🔌 Molino", style = MaterialTheme.typography.titleSmall)
                    if (grinders.value.isEmpty()) {
                        Text("Agrega un molino", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    } else {
                        DropdownField(
                            label = "Selecciona molino",
                            value = grinderIndex?.let { grinderLabels.getOrNull(it + 1) } ?: "Sin molino",
                            options = grinderLabels,
                            onSelect = { idx -> grinderIndex = if (idx == 0) null else idx - 1 }
                        )
                    }
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📋 Perfil", style = MaterialTheme.typography.titleSmall)
                    if (profiles.value.isEmpty()) {
                        Text("Agrega un perfil", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    } else {
                        DropdownField(
                            label = "Selecciona perfil",
                            value = profileIndex?.let { profileLabels.getOrNull(it + 1) } ?: "Sin perfil",
                            options = profileLabels,
                            onSelect = { idx -> profileIndex = if (idx == 0) null else idx - 1 }
                        )
                    }
                }
            }
        }

        // ========== ROW 2: DOSIS, RENDIMIENTO, EXTRACCIÓN ==========
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⚖️ Dosis", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = dosis,
                        onValueChange = { dosis = it },
                        label = { Text("Gramos") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📊 Ratio", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = rendimiento,
                        onValueChange = { rendimiento = it },
                        label = { Text("Rendimiento (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Text(
                        text = "Ratio: ${"%.2f".format(ratioValue)}x",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("☕ Extracción", style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = tiempoSeg,
                            onValueChange = { tiempoSeg = it },
                            label = { Text("Tiempo (s)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = temperatura,
                            onValueChange = { temperatura = it },
                            label = { Text("Temp (°C)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }

        // ========== SECCIÓN COLAPSABLE PRE-INFUSIÓN ==========
        ExpandableSection(
            title = "Pre-Infusión",
            icon = "⏱️",
            expanded = expandPreInfusion,
            onExpandChange = { expandPreInfusion = it }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedTextField(
                    value = preinfusionTiempo,
                    onValueChange = { preinfusionTiempo = it },
                    label = { Text("Tiempo (segundos)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = preinfusionPresion,
                    onValueChange = { preinfusionPresion = it },
                    label = { Text("Presión (bar)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        // ========== SECCIÓN COLAPSABLE TASTING NOTES ==========
        ExpandableSection(
            title = "Tasting Notes",
            icon = "👃",
            expanded = expandTasting,
            onExpandChange = { expandTasting = it }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DropdownField(
                        label = "Aroma",
                        value = aromaDisplay,
                        options = aromaOptions,
                        onSelect = { idx -> aromaNotes = if (idx == 0) "" else aromaOptions[idx] },
                        modifier = Modifier.weight(1f)
                    )
                    DropdownField(
                        label = "Sabor",
                        value = saborDisplay,
                        options = saborOptions,
                        onSelect = { idx -> saborNotes = if (idx == 0) "" else saborOptions[idx] },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DropdownField(
                        label = "Cuerpo",
                        value = cuerpoDisplay,
                        options = cuerpoOptions,
                        onSelect = { idx -> cuerpo = if (idx == 0) "" else cuerpoOptions[idx] },
                        modifier = Modifier.weight(1f)
                    )
                    DropdownField(
                        label = "Acidez",
                        value = acidezDisplay,
                        options = acidezOptions,
                        onSelect = { idx -> acidez = if (idx == 0) "" else acidezOptions[idx] },
                        modifier = Modifier.weight(1f)
                    )
                }
                DropdownField(
                    label = "Finish",
                    value = finishDisplay,
                    options = finishOptions,
                    onSelect = { idx -> finish = if (idx == 0) "" else finishOptions[idx] }
                )
            }
        }

        // ========== ROW 3: NOTAS Y CALIFICACIÓN ==========
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("📝 Notas", style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        label = { Text("Observaciones") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            }

            Card(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                ) {
                    Text("Calificación", style = MaterialTheme.typography.labelSmall)
                    RatingStars(
                        rating = calificacion.toIntOrNull() ?: 0,
                        max = 10,
                        onRatingChange = { calificacion = it.toString() }
                    )
                }
            }
        }

        // ========== SECCIÓN COLAPSABLE SIGUIENTE SHOT ==========
        ExpandableSection(
            title = "Siguiente Shot",
            icon = "💡",
            expanded = expandNextShot,
            onExpandChange = { expandNextShot = it }
        ) {
            DropdownField(
                label = "Sugerencia",
                value = nextShotDisplay,
                options = nextShotOptions,
                onSelect = { idx ->
                    nextShotNotes = if (idx == 0) "" else nextShotOptions[idx]
                }
            )
        }

        if (error != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Button(
            onClick = {
                val d = dosis.toDoubleOrNull()
                val y = rendimiento.toDoubleOrNull()
                if (d == null || y == null) {
                    error = "⚠️ Dosis y rendimiento son obligatorios"
                    return@Button
                }
                if (beanIndex !in beans.value.indices) {
                    error = "⚠️ Selecciona un grano válido"
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
                        calificacion = rating,
                        preinfusionTiempoSeg = preinfusionTiempo.toIntOrNull(),
                        preinfusionPresionBar = preinfusionPresion.toDoubleOrNull(),
                        aromaNotes = aromaNotes.ifBlank { null },
                        saborNotes = saborNotes.ifBlank { null },
                        cuerpo = cuerpo.ifBlank { null },
                        acidez = acidez.ifBlank { null },
                        finish = finish.ifBlank { null }
                    )
                } else {
                    vm.updateShot(
                        com.example.shots.data.model.ShotEntity(
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
                            preinfusionTiempoSeg = preinfusionTiempo.toIntOrNull(),
                            preinfusionPresionBar = preinfusionPresion.toDoubleOrNull(),
                            aromaNotes = aromaNotes.ifBlank { null },
                            saborNotes = saborNotes.ifBlank { null },
                            cuerpo = cuerpo.ifBlank { null },
                            acidez = acidez.ifBlank { null },
                            finish = finish.ifBlank { null },
                            createdAt = createdAt ?: System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
                navController.navigateUp()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(if (shotId == null) "Registrar" else "Actualizar", modifier = Modifier.padding(6.dp), style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("$icon $title", style = MaterialTheme.typography.titleSmall)
            content()
        }
    }
}

@Composable
private fun ExpandableSection(
    title: String,
    icon: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$icon $title", style = MaterialTheme.typography.titleSmall)
                IconButton(onClick = { onExpandChange(!expanded) }, modifier = Modifier.padding(0.dp)) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) "Colapsar" else "Expandir",
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }
            if (expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    content()
                }
            }
        }
    }
}
