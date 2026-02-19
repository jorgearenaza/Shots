package com.example.shots.data

import com.example.shots.data.dao.BeanDao
import com.example.shots.data.dao.GrinderDao
import com.example.shots.data.dao.ProfileDao
import com.example.shots.data.dao.ShotDao
import com.example.shots.data.model.BeanEntity
import com.example.shots.data.model.GrinderEntity
import com.example.shots.data.model.ProfileEntity
import com.example.shots.data.model.ShotDetails
import com.example.shots.data.model.ShotEntity
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

    // Search methods
    fun searchBeans(query: String): Flow<List<BeanEntity>> = beanDao.searchActive(query)
    fun searchGrinders(query: String): Flow<List<GrinderEntity>> = grinderDao.searchActive(query)
    fun searchProfiles(query: String): Flow<List<ProfileEntity>> = profileDao.searchActive(query)

    // Filtered query method
    fun queryFilteredShots(
        minRating: Int,
        maxRating: Int,
        beanId: Long?,
        grinderId: Long?,
        startDate: Long?,
        endDate: Long?
    ): Flow<List<ShotDetails>> = shotDao.queryFiltered(
        minRating = minRating,
        maxRating = maxRating,
        beanId = beanId,
        grinderId = grinderId,
        startDate = startDate,
        endDate = endDate
    )

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
