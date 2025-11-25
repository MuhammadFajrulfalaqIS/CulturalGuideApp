package com.example.cultural_navigation_papb.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cultural_navigation_papb.data.dao.PlaceDao
import com.example.cultural_navigation_papb.data.dao.ReviewDao
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.models.Review
import com.example.cultural_navigation_papb.data.converters.Converters

/**
 * Room Database untuk aplikasi Cultural Navigation
 * Menyimpan data tempat wisata dan review
 */
@Database(
    entities = [Place::class, Review::class],
    version = 2, // Update version karena ada entity baru
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cultural_navigation_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

