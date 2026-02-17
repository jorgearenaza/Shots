package com.example.espressoshots.data

import android.content.Context
import com.example.espressoshots.data.db.AppDatabase

object ServiceLocator {
    private var repository: ShotsRepository? = null

    fun provideRepository(context: Context): ShotsRepository {
        synchronized(this) {
            return repository ?: run {
                val db = AppDatabase.getInstance(context.applicationContext)
                val ds = DataStoreManager.create(context.applicationContext)
                val repo = ShotsRepository(
                    shotDao = db.shotDao(),
                    beanDao = db.beanDao(),
                    grinderDao = db.grinderDao(),
                    profileDao = db.profileDao(),
                    dataStore = ds
                )
                repository = repo
                repo
            }
        }
    }
}
