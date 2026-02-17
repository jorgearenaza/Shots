package com.example.espressoshots.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.espressoshots.data.dao.ShotDao
import com.example.espressoshots.data.model.Shot

@Database(entities = [Shot::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shotDao(): ShotDao

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
