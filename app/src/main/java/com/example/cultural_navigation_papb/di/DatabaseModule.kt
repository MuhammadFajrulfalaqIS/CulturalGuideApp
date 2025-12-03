package com.example.cultural_navigation_papb.di

import android.content.Context
import androidx.room.Room
import com.example.cultural_navigation_papb.data.api.FirestoreService
import com.example.cultural_navigation_papb.data.dao.PlaceDao
import com.example.cultural_navigation_papb.data.dao.ReviewDao
import com.example.cultural_navigation_papb.data.database.AppDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module untuk menyediakan Database dan DAO
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cultural_navigation_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePlaceDao(database: AppDatabase): PlaceDao {
        return database.placeDao()
    }

    @Provides
    @Singleton
    fun provideReviewDao(database: AppDatabase): ReviewDao {
        return database.reviewDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestoreService(firestore: FirebaseFirestore): FirestoreService {
        return FirestoreService(firestore)
    }
}
