package com.example.espressoshots.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.espressoshots.data.SettingsState
import com.example.espressoshots.ui.components.DropdownField
import com.example.espressoshots.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun OptionsScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val settings = vm.settings.collectAsState()
    val grinders = vm.grinders.collectAsState()
    val profiles = vm.profiles.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var dose by remember { mutableStateOf("") }
    var ratio by remember { mutableStateOf("") }
    var yield by remember { mutableStateOf("") }
    var grinderIndex by remember { mutableStateOf<Int?>(null) }
    var profileIndex by remember { mutableStateOf<Int?>(null) }
    var autofill by remember { mutableStateOf(true) }
    val configuration = LocalConfiguration.current
    val isWide = configuration.screenWidthDp >= 600

    LaunchedEffect(settings.value) {
        dose = settings.value.defaultDoseG.toString()
        ratio = settings.value.defaultRatio.toString()
        yield = settings.value.defaultYieldG.toString()
        grinderIndex = grinders.value.indexOfFirst { it.id == settings.value.defaultMolinoId }.takeIf { it >= 0 }
        profileIndex = profiles.value.indexOfFirst { it.id == settings.value.defaultPerfilId }.takeIf { it >= 0 }
        autofill = settings.value.autofillShots
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SnackbarHost(hostState = snackbarHostState)

        val grinderOptions = listOf("Sin molino") + grinders.value.map { it.nombre }
        val profileOptions = listOf("Sin perfil") + profiles.value.map { it.nombre }

        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Defaults", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(value = dose, onValueChange = { dose = it }, label = { Text("Default dose (g)") })
                    OutlinedTextField(value = ratio, onValueChange = { ratio = it }, label = { Text("Default ratio") })
                    OutlinedTextField(value = yield, onValueChange = { yield = it }, label = { Text("Default yield (g)") })
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Preferencias", style = MaterialTheme.typography.titleMedium)
                    DropdownField(
                        label = "Molino default",
                        value = grinderIndex?.let { grinders.value.getOrNull(it)?.nombre } ?: "Sin molino",
                        options = grinderOptions,
                        onSelect = { idx -> grinderIndex = if (idx == 0) null else idx - 1 }
                    )
                    DropdownField(
                        label = "Perfil default",
                        value = profileIndex?.let { profiles.value.getOrNull(it)?.nombre } ?: "Sin perfil",
                        options = profileOptions,
                        onSelect = { idx -> profileIndex = if (idx == 0) null else idx - 1 }
                    )
                    Column {
                        Text("Autofill en shots")
                        Switch(checked = autofill, onCheckedChange = { autofill = it })
                    }
                }
            }
        } else {
            Text("Defaults", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = dose, onValueChange = { dose = it }, label = { Text("Default dose (g)") })
            OutlinedTextField(value = ratio, onValueChange = { ratio = it }, label = { Text("Default ratio") })
            OutlinedTextField(value = yield, onValueChange = { yield = it }, label = { Text("Default yield (g)") })

            Text("Preferencias", style = MaterialTheme.typography.titleMedium)
            DropdownField(
                label = "Molino default",
                value = grinderIndex?.let { grinders.value.getOrNull(it)?.nombre } ?: "Sin molino",
                options = grinderOptions,
                onSelect = { idx -> grinderIndex = if (idx == 0) null else idx - 1 }
            )

            DropdownField(
                label = "Perfil default",
                value = profileIndex?.let { profiles.value.getOrNull(it)?.nombre } ?: "Sin perfil",
                options = profileOptions,
                onSelect = { idx -> profileIndex = if (idx == 0) null else idx - 1 }
            )

            Column {
                Text("Autofill en shots")
                Switch(checked = autofill, onCheckedChange = { autofill = it })
            }
        }

        Button(
            onClick = {
                val d = dose.toDoubleOrNull() ?: 18.0
                val r = ratio.toDoubleOrNull() ?: 2.0
                val y = yield.toDoubleOrNull() ?: (d * r)
                val grinderId = grinderIndex?.let { grinders.value.getOrNull(it)?.id }
                val profileId = profileIndex?.let { profiles.value.getOrNull(it)?.id }

                vm.saveSettings(
                    SettingsState(
                        defaultDoseG = d,
                        defaultRatio = r,
                        defaultYieldG = y,
                        defaultMolinoId = grinderId,
                        defaultPerfilId = profileId,
                        autofillShots = autofill
                    )
                )
                scope.launch { snackbarHostState.showSnackbar("Guardado") }
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
