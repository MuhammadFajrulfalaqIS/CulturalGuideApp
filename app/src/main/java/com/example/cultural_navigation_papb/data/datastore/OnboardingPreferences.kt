package com.example.cultural_navigation_papb.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension property untuk DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "onboarding_preferences")

@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        val FEATURE_GUIDE_COMPLETED = booleanPreferencesKey("feature_guide_completed")
    }

    // Flow untuk mengecek apakah feature guide sudah selesai
    val isFeatureGuideCompleted: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[FEATURE_GUIDE_COMPLETED] ?: false
        }

    // Fungsi untuk set feature guide sebagai completed
    suspend fun setFeatureGuideCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[FEATURE_GUIDE_COMPLETED] = completed
        }
    }

    // Fungsi untuk reset (untuk testing atau dari settings)
    suspend fun resetFeatureGuide() {
        setFeatureGuideCompleted(false)
    }
}

