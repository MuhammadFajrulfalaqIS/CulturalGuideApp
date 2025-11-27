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
import com.example.cultural_navigation_papb.data.models.User
import com.example.cultural_navigation_papb.data.dao.UserDao
import com.example.cultural_navigation_papb.data.models.SavedPlace
import com.example.cultural_navigation_papb.data.dao.SavedPlaceDao

/**
 * Room Database untuk aplikasi Cultural Navigation
 * Menyimpan data tempat wisata dan review
 */
@Database(
    entities = [Place::class, Review::class, User::class, SavedPlace::class],
    version = 4, // Update version karena ada entity baru
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao
    abstract fun reviewDao(): ReviewDao
    abstract fun userDao(): UserDao
    abstract fun savedPlaceDao(): SavedPlaceDao

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

