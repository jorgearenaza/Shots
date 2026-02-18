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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repo: ShotsRepository) : ViewModel() {
    val shots: StateFlow<List<ShotDetails>> = repo.observeShots()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val beans: StateFlow<List<BeanEntity>> = repo.observeBeans()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val grinders: StateFlow<List<GrinderEntity>> = repo.observeGrinders()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val profiles: StateFlow<List<ProfileEntity>> = repo.observeProfiles()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val settings: StateFlow<SettingsState> = repo.dataStore.settings
        .stateIn(viewModelScope, SharingStarted.Lazily, SettingsState())

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

}
