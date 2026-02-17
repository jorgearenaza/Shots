package com.example.espressoshots.data

import android.content.Context

object ServiceLocator {
    private var repository: ShotsRepository? = null

    fun provideRepository(context: Context): ShotsRepository {
        synchronized(this) {
            return repository ?: run {
                val db = AppDatabase.getInstance(context.applicationContext)
                val ds = DataStoreManager.create(context.applicationContext)
                val repo = ShotsRepository(db.shotDao(), ds)
                repository = repo
                repo
            }
        }
    }
}
