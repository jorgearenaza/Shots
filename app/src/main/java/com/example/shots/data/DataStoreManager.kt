package com.example.espressoshots.data

import android.content.Context
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("espressoshots_prefs")

class DataStoreManager private constructor(private val context: Context) {
    companion object {
        private val DEFAULT_DOSE = floatPreferencesKey("default_dose")

        fun create(context: Context): DataStoreManager = DataStoreManager(context)
    }

    val defaultDose: Flow<Float> = context.dataStore.data.map { prefs ->
        prefs[DEFAULT_DOSE] ?: 18f
    }

    suspend fun setDefaultDose(value: Float) {
        context.dataStore.edit { prefs ->
            prefs[DEFAULT_DOSE] = value
        }
    }
}
