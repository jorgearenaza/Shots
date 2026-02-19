package com.example.shots.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shots.data.ShotsRepository
import com.example.shots.data.SettingsState
import com.example.shots.data.model.BeanEntity
import com.example.shots.data.model.GrinderEntity
import com.example.shots.data.model.ProfileEntity
import com.example.shots.data.model.ShotDetails
import com.example.shots.data.model.ShotEntity
import com.example.shots.ui.components.ShotFilters
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.abs

class MainViewModel(private val repo: ShotsRepository) : ViewModel() {
    val shots: StateFlow<List<ShotDetails>> = repo.observeShots()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val beans: StateFlow<List<BeanEntity>> = repo.observeBeans()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val grinders: StateFlow<List<GrinderEntity>> = repo.observeGrinders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val profiles: StateFlow<List<ProfileEntity>> = repo.observeProfiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Loading states
    val isLoadingShots: StateFlow<Boolean> = shots.map { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isLoadingStats: StateFlow<Boolean> = shots.map { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    // Search methods that return Flow for reactive updates
    fun searchBeans(query: String) = repo.searchBeans(query)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun searchGrinders(query: String) = repo.searchGrinders(query)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun searchProfiles(query: String) = repo.searchProfiles(query)
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val settings: StateFlow<SettingsState> = repo.dataStore.settings
        .stateIn(viewModelScope, SharingStarted.Lazily, SettingsState())

    // Persistent filters from DataStore
    val persistedFilters: StateFlow<ShotFilters> = repo.dataStore.filters
        .stateIn(viewModelScope, SharingStarted.Lazily, ShotFilters())

    // Computed Stats - Cached and only recalculated when shots change
    
    val beanRatingsStats: StateFlow<List<Pair<String, Pair<Double, Int>>>> = shots.map { allShots ->
        allShots
            .filter { (it.shot.calificacion ?: 0) > 0 }
            .groupBy { "${it.beanTostador} - ${it.beanCafe}" }
            .mapValues { (_, shotsOfBean) ->
                val avgRating = shotsOfBean.mapNotNull { it.shot.calificacion }.average()
                val count = shotsOfBean.size
                Pair(avgRating, count)
            }
            .toList()
            .sortedByDescending { it.second.first }
            .take(6)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val grinderPerformanceStats: StateFlow<List<Pair<String?, Pair<Double, Int>>>> = shots.map { allShots ->
        allShots
            .filter { it.grinderNombre != null && (it.shot.calificacion ?: 0) > 0 }
            .groupBy { it.grinderNombre }
            .mapValues { (_, shotsOfGrinder) ->
                val avgRating = shotsOfGrinder.mapNotNull { it.shot.calificacion }.average()
                val count = shotsOfGrinder.size
                Pair(avgRating, count)
            }
            .toList()
            .sortedByDescending { it.second.first }
            .take(5)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val winningCombination: StateFlow<ShotDetails?> = shots.map { allShots ->
        val ratedShots = allShots.filter { (it.shot.calificacion ?: 0) >= 8 }
        ratedShots.maxByOrNull { it.shot.calificacion ?: 0 }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val timeDistributionStats: StateFlow<Map<String, Pair<Int, Double>>> = shots.map { allShots ->
        val timesWithRating = allShots
            .filter { it.shot.tiempoSeg != null && (it.shot.calificacion ?: 0) > 0 }
        
        val ranges = mapOf(
            "20-30s" to timesWithRating.filter { it.shot.tiempoSeg!! in 20..30 },
            "30-40s" to timesWithRating.filter { it.shot.tiempoSeg!! in 31..40 },
            "40-50s" to timesWithRating.filter { it.shot.tiempoSeg!! in 41..50 },
            "50-60s" to timesWithRating.filter { it.shot.tiempoSeg!! in 51..60 },
            "60s+" to timesWithRating.filter { it.shot.tiempoSeg!! > 60 }
        ).filterValues { it.isNotEmpty() }

        ranges.mapValues { (_, shotsInRange) ->
            val avgRating = shotsInRange.mapNotNull { it.shot.calificacion }.average()
            Pair(shotsInRange.size, avgRating)
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    val statsInsights: StateFlow<List<Pair<String, String>>> = shots.map { allShots ->
        val insights = mutableListOf<Pair<String, String>>()
        
        val avgRating = allShots.mapNotNull { it.shot.calificacion }.let {
            if (it.isEmpty()) 0.0 else it.average()
        }
        
        val ratioRange = allShots.map { it.shot.ratio }
        val avgRatio = ratioRange.average()
        val consistentRatio = if (ratioRange.isNotEmpty()) {
            ratioRange.map { abs(it - avgRatio) }.average() < 0.3
        } else false
        
        val last7Shots = allShots.sortedByDescending { it.shot.fecha }.take(7)
        val last7Avg = last7Shots.mapNotNull { it.shot.calificacion }.let {
            if (it.isEmpty()) 0.0 else it.average()
        }
        
        if (avgRating >= 8.0) {
            insights.add("🎯" to "Excelente consistencia con rating promedio de ${String.format("%.1f", avgRating)}")
        } else if (avgRating < 6.0 && allShots.size > 5) {
            insights.add("💡" to "Hay espacio para mejorar. Experimenta con diferentes ajustes")
        }
        
        if (consistentRatio) {
            insights.add("⚖️" to "Ratio muy consistente alrededor de ${String.format("%.2f", avgRatio)}")
        }
        
        if (last7Shots.size >= 7 && last7Avg > avgRating + 0.5) {
            insights.add("📈" to "Mejorando! Tus últimos shots tienen mejor rating")
        } else if (last7Shots.size >= 7 && last7Avg < avgRating - 0.5) {
            insights.add("📉" to "Últimos shots por debajo del promedio. Revisa tu técnica")
        }
        
        val mostUsedBean = allShots.groupingBy { "${it.beanTostador} - ${it.beanCafe}" }
            .eachCount()
            .maxByOrNull { it.value }
        
        if (mostUsedBean != null && mostUsedBean.value > allShots.size * 0.3) {
            insights.add("☕" to "Tu grano favorito es ${mostUsedBean.key}")
        }
        
        if (allShots.size >= 10) {
            val highRatedShots = allShots.filter { (it.shot.calificacion ?: 0) >= 8 }
            val avgHighRatio = highRatedShots.map { it.shot.ratio }.let {
                if (it.isEmpty()) 0.0 else it.average()
            }
            if (highRatedShots.isNotEmpty()) {
                insights.add("✨" to "Tus mejores shots tienen ratio de ${String.format("%.2f", avgHighRatio)}")
            }
        }
        
        if (insights.isEmpty()) {
            insights.add("📊" to "Sigue registrando shots para obtener insights personalizados")
        }
        
        insights
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addShot(
        fecha: Long,
        beanId: Long,
        molinoId: Long?,
        perfilId: Long?,
        dosisG: Double,
        rendimientoG: Double,
        tiempoSeg: Int?,
        temperaturaC: Double?,
        ajusteMolienda: String?,
        preinfusionTiempoSeg: Int?,
        preinfusionPresionBar: Double?,
        aguaMlInfusion: Int?,
        aromaNotes: String?,
        saborNotes: String?,
        cuerpo: String?,
        acidez: String?,
        finish: String?,
        notas: String?,
        nextShotNotes: String?,
        calificacion: Int?
    ) {
        val now = System.currentTimeMillis()
        val ratio = if (dosisG == 0.0) 0.0 else rendimientoG / dosisG
        viewModelScope.launch {
            repo.insertShot(
                ShotEntity(
                    fecha = fecha,
                    beanId = beanId,
                    molinoId = molinoId,
                    perfilId = perfilId,
                    dosisG = dosisG,
                    rendimientoG = rendimientoG,
                    ratio = ratio,
                    tiempoSeg = tiempoSeg,
                    temperaturaC = temperaturaC,
                    ajusteMolienda = ajusteMolienda,
                    preinfusionTiempoSeg = preinfusionTiempoSeg,
                    preinfusionPresionBar = preinfusionPresionBar,
                    aguaMlInfusion = aguaMlInfusion,
                    aromaNotes = aromaNotes,
                    saborNotes = saborNotes,
                    cuerpo = cuerpo,
                    acidez = acidez,
                    finish = finish,
                    notas = notas,
                    nextShotNotes = nextShotNotes,
                    calificacion = calificacion,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun updateShot(shot: ShotEntity) {
        viewModelScope.launch {
            repo.updateShot(shot.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteShot(id: Long) {
        viewModelScope.launch {
            repo.deleteShot(id)
        }
    }

    suspend fun getShot(id: Long): ShotEntity? = repo.getShot(id)

    suspend fun saveBean(
        id: Long?,
        tostador: String,
        cafe: String,
        fechaTostado: Long,
        fechaCompra: Long,
        notas: String?,
        pais: String?,
        proceso: String?,
        varietal: String?,
        altitud: Int?
    ) {
        val now = System.currentTimeMillis()
        if (id == null) {
            repo.insertBean(
                BeanEntity(
                    tostador = tostador,
                    cafe = cafe,
                    fechaTostado = fechaTostado,
                    fechaCompra = fechaCompra,
                    notas = notas,
                    pais = pais,
                    proceso = proceso,
                    varietal = varietal,
                    altitud = altitud,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun updateBeanEntity(bean: BeanEntity) {
        viewModelScope.launch { repo.updateBean(bean.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun deactivateBean(id: Long) {
        viewModelScope.launch { repo.deactivateBean(id) }
    }

    fun deleteBean(id: Long) {
        viewModelScope.launch { repo.deactivateBean(id) }
    }

    suspend fun getBean(id: Long): BeanEntity? = repo.getBean(id)

    suspend fun saveGrinder(id: Long?, nombre: String, ajusteDefault: String?, notas: String?) {
        val now = System.currentTimeMillis()
        if (id == null) {
            repo.insertGrinder(
                GrinderEntity(
                    nombre = nombre,
                    ajusteDefault = ajusteDefault,
                    notas = notas,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun updateGrinderEntity(grinder: GrinderEntity) {
        viewModelScope.launch { repo.updateGrinder(grinder.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun deactivateGrinder(id: Long) {
        viewModelScope.launch { repo.deactivateGrinder(id) }
    }

    fun deleteGrinder(id: Long) {
        viewModelScope.launch { repo.deactivateGrinder(id) }
    }

    suspend fun getGrinder(id: Long): GrinderEntity? = repo.getGrinder(id)

    suspend fun saveProfile(id: Long?, nombre: String, descripcion: String?, parametros: String?) {
        val now = System.currentTimeMillis()
        if (id == null) {
            repo.insertProfile(
                ProfileEntity(
                    nombre = nombre,
                    descripcion = descripcion,
                    parametros = parametros,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }

    fun updateProfileEntity(profile: ProfileEntity) {
        viewModelScope.launch { repo.updateProfile(profile.copy(updatedAt = System.currentTimeMillis())) }
    }

    fun deactivateProfile(id: Long) {
        viewModelScope.launch { repo.deactivateProfile(id) }
    }

    fun deleteProfile(id: Long) {
        viewModelScope.launch { repo.deactivateProfile(id) }
    }

    suspend fun getProfile(id: Long): ProfileEntity? = repo.getProfile(id)

    fun saveSettings(state: SettingsState) {
        viewModelScope.launch {
            repo.dataStore.setSettings(state)
        }
    }

    fun saveFilters(filters: ShotFilters) {
        viewModelScope.launch {
            repo.dataStore.setFilters(filters)
        }
    }

    // Filtered shots - accepts ShotFilters data class with optional parameters
    fun getFilteredShots(filters: ShotFilters): StateFlow<List<ShotDetails>> {
        return repo.queryFilteredShots(
            minRating = filters.minRating ?: 1,
            maxRating = filters.maxRating ?: 10,
            beanId = filters.selectedBeamId,
            grinderId = filters.selectedGrinderId,
            startDate = filters.startDate,
            endDate = filters.endDate
        ).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

}
