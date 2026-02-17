package com.example.espressoshots.data

import com.example.espressoshots.data.dao.ShotDao
import com.example.espressoshots.data.model.Shot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShotsRepository(
    private val shotDao: ShotDao,
    val dataStore: DataStoreManager
) {
    fun observeShots(): Flow<List<Shot>> = shotDao.observeAll()

    suspend fun insertShot(shot: Shot): Long {
        return withContext(Dispatchers.IO) {
            shotDao.insert(shot)
        }
    }
}
