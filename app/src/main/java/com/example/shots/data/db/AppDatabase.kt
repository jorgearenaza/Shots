package com.example.shots.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shots.data.dao.BeanDao
import com.example.shots.data.dao.GrinderDao
import com.example.shots.data.dao.ProfileDao
import com.example.shots.data.dao.ShotDao
import com.example.shots.data.dao.GoalDao
import com.example.shots.data.model.BeanEntity
import com.example.shots.data.model.GrinderEntity
import com.example.shots.data.model.ProfileEntity
import com.example.shots.data.model.ShotEntity
import com.example.shots.data.model.GoalEntity

@Database(
    entities = [ShotEntity::class, BeanEntity::class, GrinderEntity::class, ProfileEntity::class, GoalEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shotDao(): ShotDao
    abstract fun beanDao(): BeanDao
    abstract fun grinderDao(): GrinderDao
    abstract fun profileDao(): ProfileDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "espressoshots.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = inst
                inst
            }
        }
    }
}
