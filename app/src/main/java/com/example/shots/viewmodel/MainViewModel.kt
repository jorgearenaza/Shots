package com.example.espressoshots.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.espressoshots.data.ShotsRepository
import com.example.espressoshots.data.model.Shot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ShotsRepository) : ViewModel() {
    private val _shots = MutableStateFlow<List<Shot>>(emptyList())
    val shots: StateFlow<List<Shot>> = _shots

    init {
        observeShots()
    }

    private fun observeShots() {
        viewModelScope.launch {
            repository.observeShots().collect { list ->
                _shots.value = list
            }
        }
    }

    fun addShot(beanName: String, grinder: String, dose: Float, yield: Float, timeSeconds: Int) {
        viewModelScope.launch {
            val shot = Shot(
                beanName = beanName,
                grinder = grinder,
                dose = dose,
                yield = yield,
                timeSeconds = timeSeconds
            )
            repository.insertShot(shot)
        }
    }
}
package com.example.espressoshots.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.espressoshots.data.ShotsRepository
import com.example.espressoshots.data.model.Shot
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repo: ShotsRepository) : ViewModel() {
    val shots: StateFlow<List<Shot>> = repo.observeShots()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addShot(bean: String, grinder: String, dose: Float, yield: Float, time: Int) {
        viewModelScope.launch {
            repo.insertShot(Shot(beanName = bean, grinder = grinder, dose = dose, yield = yield, timeSeconds = time))
        }
    }
}
