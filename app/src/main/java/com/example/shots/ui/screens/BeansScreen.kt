package com.example.shots.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.shots.R
import com.example.shots.ui.components.BeanCard
import com.example.shots.ui.components.EmptyState
import com.example.shots.ui.theme.AppSpacing
import com.example.shots.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun BeansScreen(navController: NavController, vm: MainViewModel, padding: PaddingValues) {
    val beans = vm.beans.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var beanToDelete by remember { mutableStateOf<Long?>(null) }

    if (beans.value.isEmpty()) {
        EmptyState(
            message = stringResource(R.string.empty_beans_message),
            actionLabel = stringResource(R.string.empty_beans_action),
            onAction = { navController.navigate("beans/new") }
        )
        return
    }

    // Usar búsqueda en BD si hay query, si no mostrar todos
    val filteredBeans = if (searchQuery.isNotEmpty()) {
        vm.searchBeans(searchQuery).collectAsState(emptyList()).value
    } else {
        beans.value
    }

    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
        SnackbarHost(hostState = snackbarHostState)
        
        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(stringResource(R.string.search_beans_hint)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.large),
            singleLine = true
        )

        // Lista de granos
        if (filteredBeans.isEmpty()) {
            EmptyState(
                message = stringResource(R.string.no_beans_found),
                actionLabel = stringResource(R.string.clear_beans_search),
                onAction = { searchQuery = "" }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = AppSpacing.large, end = AppSpacing.large, top = AppSpacing.large, bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.medium)
            ) {
                items(filteredBeans) { bean ->
                    BeanCard(
                        bean = bean,
                        onEdit = { navController.navigate("beans/edit/${bean.id}") },
                        onDelete = { beanToDelete = bean.id },
                        onPurchaseUpdate = {
                            vm.updateBeanEntity(bean.copy(fechaCompra = System.currentTimeMillis()))
                            scope.launch { snackbarHostState.showSnackbar("Fecha de compra actualizada") }
                        },
                        onRoastUpdate = {
                            vm.updateBeanEntity(bean.copy(fechaTostado = System.currentTimeMillis()))
                            scope.launch { snackbarHostState.showSnackbar("Fecha de tostado actualizada") }
                        }
                    )
                }
            }
        }
    }

    // Diálogo de confirmación para eliminar
    if (beanToDelete != null) {
        AlertDialog(
            onDismissRequest = { beanToDelete = null },
            title = { Text("Eliminar grano") },
            text = { Text("¿Estás seguro de que quieres eliminar este grano? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        beanToDelete?.let { vm.deleteBean(it) }
                        beanToDelete = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { beanToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
