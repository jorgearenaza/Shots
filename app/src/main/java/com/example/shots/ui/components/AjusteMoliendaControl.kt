package com.example.espressoshots.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AjusteMoliendaControl(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf("Espresso", "Turbo", "Filtro", "Fino", "Medio", "Grueso")

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Ajuste de molienda") },
        modifier = modifier.fillMaxWidth()
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        presets.forEach { preset ->
            AssistChip(
                onClick = { onValueChange(preset) },
                label = { Text(preset) },
                colors = AssistChipDefaults.assistChipColors()
            )
        }
    }
    // TODO: extend to pro mode (slider + stepper + numeric presets)
}
