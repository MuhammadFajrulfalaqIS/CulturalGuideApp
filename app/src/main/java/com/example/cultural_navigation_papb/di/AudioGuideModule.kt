package com.example.cultural_navigation_papb.di

import android.content.Context
import com.example.cultural_navigation_papb.data.ai.NarrationGenerator
import com.example.cultural_navigation_papb.data.audio.AudioGuidePlayer
import com.example.cultural_navigation_papb.data.audio.CloudTTSProvider
import com.example.cultural_navigation_papb.data.audio.CloudAudioPlayer
import com.example.cultural_navigation_papb.data.dao.NarrationDao
import com.example.cultural_navigation_papb.data.location.LocationService
import com.google.firebase.functions.FirebaseFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module untuk menyediakan Audio Guide dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AudioGuideModule {

    @Provides
    @Singleton
    fun provideFirebaseFunctions(): FirebaseFunctions {
        return FirebaseFunctions.getInstance()
    }

    @Provides
    @Singleton
    fun provideLocationService(
        @ApplicationContext context: Context
    ): LocationService {
        return LocationService(context)
    }

    @Provides
    @Singleton
    fun provideCloudTTSProvider(
        @ApplicationContext context: Context
    ): CloudTTSProvider {
        return CloudTTSProvider(context)
    }

    @Provides
    @Singleton
    fun provideCloudAudioPlayer(
        @ApplicationContext context: Context
    ): CloudAudioPlayer {
        return CloudAudioPlayer(context)
    }

    @Provides
    @Singleton
    fun provideAudioGuidePlayer(
        @ApplicationContext context: Context,
        cloudTTSProvider: CloudTTSProvider,
        cloudAudioPlayer: CloudAudioPlayer
    ): AudioGuidePlayer {
        return AudioGuidePlayer(context, cloudTTSProvider, cloudAudioPlayer)
    }

    @Provides
    @Singleton
    fun provideNarrationGenerator(
        narrationDao: NarrationDao,
        functions: FirebaseFunctions
    ): NarrationGenerator {
        return NarrationGenerator(narrationDao, functions)
    }
}
