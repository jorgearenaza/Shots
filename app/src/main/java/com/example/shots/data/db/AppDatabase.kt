package com.example.espressoshots.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.espressoshots.data.dao.BeanDao
import com.example.espressoshots.data.dao.GrinderDao
import com.example.espressoshots.data.dao.ProfileDao
import com.example.espressoshots.data.dao.ShotDao
import com.example.espressoshots.data.model.BeanEntity
import com.example.espressoshots.data.model.GrinderEntity
import com.example.espressoshots.data.model.ProfileEntity
import com.example.espressoshots.data.model.ShotEntity

@Database(
    entities = [ShotEntity::class, BeanEntity::class, GrinderEntity::class, ProfileEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shotDao(): ShotDao
    abstract fun beanDao(): BeanDao
    abstract fun grinderDao(): GrinderDao
    abstract fun profileDao(): ProfileDao

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
