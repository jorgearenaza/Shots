package com.example.shots.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.shots.ui.components.ShotFilters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("espressoshots_prefs")

class DataStoreManager private constructor(private val context: Context) {
    companion object {
        private val DEFAULT_DOSE = doublePreferencesKey("default_dose")
        private val DEFAULT_RATIO = doublePreferencesKey("default_ratio")
        private val DEFAULT_YIELD = doublePreferencesKey("default_yield")
        private val DEFAULT_GRINDER_ID = longPreferencesKey("default_grinder_id")
        private val DEFAULT_PROFILE_ID = longPreferencesKey("default_profile_id")
        private val AUTOFILL_SHOTS = booleanPreferencesKey("autofill_shots")
        
        // Filter persistence keys
        private val FILTER_MIN_RATING = intPreferencesKey("filter_min_rating")
        private val FILTER_MAX_RATING = intPreferencesKey("filter_max_rating")
        private val FILTER_BEAN_ID = longPreferencesKey("filter_bean_id")
        private val FILTER_GRINDER_ID = longPreferencesKey("filter_grinder_id")
        private val FILTER_START_DATE = longPreferencesKey("filter_start_date")
        private val FILTER_END_DATE = longPreferencesKey("filter_end_date")

        fun create(context: Context): DataStoreManager = DataStoreManager(context)
    }

    val settings: Flow<SettingsState> = context.dataStore.data.map { prefs ->
        val dose = prefs[DEFAULT_DOSE] ?: 18.0
        val ratio = prefs[DEFAULT_RATIO] ?: 2.0
        val yield = prefs[DEFAULT_YIELD] ?: (dose * ratio)
        SettingsState(
            defaultDoseG = dose,
            defaultRatio = ratio,
            defaultYieldG = yield,
            defaultMolinoId = prefs[DEFAULT_GRINDER_ID],
            defaultPerfilId = prefs[DEFAULT_PROFILE_ID],
            autofillShots = prefs[AUTOFILL_SHOTS] ?: true
        )
    }

    suspend fun setSettings(state: SettingsState) {
        context.dataStore.edit { prefs ->
            prefs[DEFAULT_DOSE] = state.defaultDoseG
            prefs[DEFAULT_RATIO] = state.defaultRatio
            prefs[DEFAULT_YIELD] = state.defaultYieldG
            if (state.defaultMolinoId == null) {
                prefs.remove(DEFAULT_GRINDER_ID)
            } else {
                prefs[DEFAULT_GRINDER_ID] = state.defaultMolinoId
            }
            if (state.defaultPerfilId == null) {
                prefs.remove(DEFAULT_PROFILE_ID)
            } else {
                prefs[DEFAULT_PROFILE_ID] = state.defaultPerfilId
            }
            prefs[AUTOFILL_SHOTS] = state.autofillShots
        }
    }

    val filters: Flow<ShotFilters> = context.dataStore.data.map { prefs ->
        ShotFilters(
            minRating = prefs[FILTER_MIN_RATING],
            maxRating = prefs[FILTER_MAX_RATING],
            selectedBeamId = prefs[FILTER_BEAN_ID],
            selectedGrinderId = prefs[FILTER_GRINDER_ID],
            startDate = prefs[FILTER_START_DATE],
            endDate = prefs[FILTER_END_DATE]
        )
    }

    suspend fun setFilters(filters: ShotFilters) {
        context.dataStore.edit { prefs ->
            if (filters.minRating == null) {
                prefs.remove(FILTER_MIN_RATING)
            } else {
                prefs[FILTER_MIN_RATING] = filters.minRating
            }
            if (filters.maxRating == null) {
                prefs.remove(FILTER_MAX_RATING)
            } else {
                prefs[FILTER_MAX_RATING] = filters.maxRating
            }
            if (filters.selectedBeamId == null) {
                prefs.remove(FILTER_BEAN_ID)
            } else {
                prefs[FILTER_BEAN_ID] = filters.selectedBeamId
            }
            if (filters.selectedGrinderId == null) {
                prefs.remove(FILTER_GRINDER_ID)
            } else {
                prefs[FILTER_GRINDER_ID] = filters.selectedGrinderId
            }
            if (filters.startDate == null) {
                prefs.remove(FILTER_START_DATE)
            } else {
                prefs[FILTER_START_DATE] = filters.startDate
            }
            if (filters.endDate == null) {
                prefs.remove(FILTER_END_DATE)
            } else {
                prefs[FILTER_END_DATE] = filters.endDate
            }
        }
    }
}

data class SettingsState(
    val defaultDoseG: Double = 18.0,
    val defaultRatio: Double = 2.0,
    val defaultYieldG: Double = 36.0,
    val defaultMolinoId: Long? = null,
    val defaultPerfilId: Long? = null,
    val autofillShots: Boolean = true
)
