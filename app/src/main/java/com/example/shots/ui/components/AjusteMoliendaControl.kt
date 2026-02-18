package com.example.espressoshots.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.round

@Composable
fun AjusteMoliendaControl(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val current = value.toDoubleOrNull() ?: 0.0
    val sliderValue = current.coerceIn(0.0, 20.0)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Ajuste de molienda") },
        modifier = modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = {
            val next = (sliderValue - 0.1).coerceAtLeast(0.0)
            onValueChange(String.format("%.1f", next))
        }) {
            Icon(Icons.Default.Remove, contentDescription = null)
        }
        Slider(
            value = sliderValue.toFloat(),
            onValueChange = { onValueChange(String.format("%.1f", it)) },
            valueRange = 0f..20f,
            steps = 199,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = {
            val next = (sliderValue + 0.1).coerceAtMost(20.0)
            onValueChange(String.format("%.1f", next))
        }) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }

    // TODO: extend to pro mode (advanced presets and profiles)
}
