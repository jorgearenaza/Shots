package com.example.espressoshots.data

import com.example.espressoshots.data.dao.BeanDao
import com.example.espressoshots.data.dao.GrinderDao
import com.example.espressoshots.data.dao.ProfileDao
import com.example.espressoshots.data.dao.ShotDao
import com.example.espressoshots.data.model.BeanEntity
import com.example.espressoshots.data.model.GrinderEntity
import com.example.espressoshots.data.model.ProfileEntity
import com.example.espressoshots.data.model.ShotDetails
import com.example.espressoshots.data.model.ShotEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShotsRepository(
    private val shotDao: ShotDao,
    private val beanDao: BeanDao,
    private val grinderDao: GrinderDao,
    private val profileDao: ProfileDao,
    val dataStore: DataStoreManager
) {
    fun observeShots(): Flow<List<ShotDetails>> = shotDao.observeAll()
    fun observeBeans(): Flow<List<BeanEntity>> = beanDao.observeActive()
    fun observeGrinders(): Flow<List<GrinderEntity>> = grinderDao.observeActive()
    fun observeProfiles(): Flow<List<ProfileEntity>> = profileDao.observeActive()

    suspend fun insertShot(shot: ShotEntity): Long {
        return withContext(Dispatchers.IO) {
            shotDao.insert(shot)
        }
    }

    suspend fun updateShot(shot: ShotEntity) {
        withContext(Dispatchers.IO) {
            shotDao.update(shot)
        }
    }

    suspend fun deleteShot(id: Long) {
        withContext(Dispatchers.IO) {
            shotDao.delete(id)
        }
    }

    suspend fun getShot(id: Long): ShotEntity? = withContext(Dispatchers.IO) {
        shotDao.getById(id)
    }

    suspend fun insertBean(bean: BeanEntity): Long = withContext(Dispatchers.IO) {
        beanDao.insert(bean)
    }

    suspend fun updateBean(bean: BeanEntity) {
        withContext(Dispatchers.IO) {
            beanDao.update(bean)
        }
    }

    suspend fun deactivateBean(id: Long) {
        withContext(Dispatchers.IO) {
            beanDao.deactivate(id, System.currentTimeMillis())
        }
    }

    suspend fun getBean(id: Long): BeanEntity? = withContext(Dispatchers.IO) {
        beanDao.getById(id)
    }

    suspend fun insertGrinder(grinder: GrinderEntity): Long = withContext(Dispatchers.IO) {
        grinderDao.insert(grinder)
    }

    suspend fun updateGrinder(grinder: GrinderEntity) {
        withContext(Dispatchers.IO) {
            grinderDao.update(grinder)
        }
    }

    suspend fun deactivateGrinder(id: Long) {
        withContext(Dispatchers.IO) {
            grinderDao.deactivate(id, System.currentTimeMillis())
        }
    }

    suspend fun getGrinder(id: Long): GrinderEntity? = withContext(Dispatchers.IO) {
        grinderDao.getById(id)
    }

    suspend fun insertProfile(profile: ProfileEntity): Long = withContext(Dispatchers.IO) {
        profileDao.insert(profile)
    }

    suspend fun updateProfile(profile: ProfileEntity) {
        withContext(Dispatchers.IO) {
            profileDao.update(profile)
        }
    }

    suspend fun deactivateProfile(id: Long) {
        withContext(Dispatchers.IO) {
            profileDao.deactivate(id, System.currentTimeMillis())
        }
    }

    suspend fun getProfile(id: Long): ProfileEntity? = withContext(Dispatchers.IO) {
        profileDao.getById(id)
    }
}
