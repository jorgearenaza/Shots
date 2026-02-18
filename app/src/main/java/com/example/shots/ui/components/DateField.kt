package com.example.espressoshots.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import android.content.res.Configuration

@Composable
fun DateField(
    label: String,
    valueMillis: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val date = remember(valueMillis) {
        Instant.ofEpochMilli(valueMillis).atZone(ZoneId.systemDefault()).toLocalDate()
    }
    val formatter = remember { DateTimeFormatter.ofPattern("yyyy-MM-dd") }
    val showPicker = remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    if (showPicker.value) {
        // Detectar si estamos en tema oscuro
        val isDarkTheme = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        
        val dialog = DatePickerDialog(
            context,
            if (isDarkTheme) android.R.style.Theme_Material_Dialog else android.R.style.Theme_Material_Light_Dialog,
            { _, year, month, day ->
                val newDate = LocalDate.of(year, month + 1, day)
                val millis = newDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                onValueChange(millis)
                showPicker.value = false
            },
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        )
        dialog.setOnDismissListener { showPicker.value = false }
        dialog.show()
    }

    OutlinedTextField(
        value = date.format(formatter),
        onValueChange = {},
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = { showPicker.value = true }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Seleccionar fecha")
            }
        },
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null
        ) { showPicker.value = true },
        readOnly = true,
        interactionSource = interactionSource
    )
}
