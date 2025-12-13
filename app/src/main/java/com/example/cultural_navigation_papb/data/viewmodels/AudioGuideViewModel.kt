package com.example.cultural_navigation_papb.data.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cultural_navigation_papb.data.ai.NarrationGenerator
import com.example.cultural_navigation_papb.data.audio.AudioGuidePlayer
import com.example.cultural_navigation_papb.data.audio.AudioPlayerState
import com.example.cultural_navigation_papb.data.dao.NarrationDao
import com.example.cultural_navigation_papb.data.location.LocationService
import com.example.cultural_navigation_papb.data.models.Narration
import com.example.cultural_navigation_papb.data.models.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel untuk mengelola Audio Guide feature
 */
@HiltViewModel
class AudioGuideViewModel @Inject constructor(
    private val audioPlayer: AudioGuidePlayer,
    private val narrationGenerator: NarrationGenerator,
    private val narrationDao: NarrationDao,
    private val locationService: LocationService
) : ViewModel() {

    companion object {
        private const val TAG = "AudioGuideViewModel"
    }

    // Audio player state
    val playerState: StateFlow<AudioPlayerState> = audioPlayer.state
    val progress: StateFlow<Float> = audioPlayer.progress
    val currentSpeed: StateFlow<Float> = audioPlayer.currentSpeed

    // Current narration
    private val _currentNarration = MutableStateFlow<Narration?>(null)
    val currentNarration: StateFlow<Narration?> = _currentNarration.asStateFlow()

    // Loading state
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    // Error state
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Distance to place
    private val _distanceToPlace = MutableStateFlow<Float?>(null)
    val distanceToPlace: StateFlow<Float?> = _distanceToPlace.asStateFlow()

    // Current language
    private val _currentLanguage = MutableStateFlow("id")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    init {
        // Initialize audio player
        audioPlayer.initialize()
    }

    /**
     * Load atau generate narration untuk place
     */
    fun loadNarration(place: Place, forceRegenerate: Boolean = false) {
        viewModelScope.launch {
            try {
                _isGenerating.value = true
                _errorMessage.value = null

                Log.d(TAG, "🔄 Loading narration for ${place.name}")

                val result = narrationGenerator.generateNarration(
                    place = place,
                    language = _currentLanguage.value,
                    forceRegenerate = forceRegenerate
                )

                result.fold(
                    onSuccess = { narration ->
                        _currentNarration.value = narration
                        Log.d(TAG, "✅ Narration loaded: ${narration.narrationText.length} characters")
                    },
                    onFailure = { error ->
                        _errorMessage.value = "Failed to generate narration: ${error.message}"
                        Log.e(TAG, "❌ Error: ${error.message}", error)
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                Log.e(TAG, "❌ Exception: ${e.message}", e)
            } finally {
                _isGenerating.value = false
            }
        }
    }

    /**
     * Play audio guide
     */
    fun play() {
        _currentNarration.value?.let { narration ->
            // PERBAIKAN: Force language ke "id" supaya pasti pake voice Indonesia
            val language = if (narration.language == "en" && narration.narrationText.any { it.toInt() > 127 }) {
                // Kalau ada karakter Indonesia tapi language salah, override ke "id"
                Log.d(TAG, "⚠️ Detected Indonesian text, forcing language to 'id'")
                "id"
            } else {
                narration.language
            }

            audioPlayer.play(narration.narrationText, language)
            Log.d(TAG, "▶️ Playing audio guide in language: $language")
        } ?: run {
            _errorMessage.value = "No narration available"
            Log.w(TAG, "⚠️ No narration to play")
        }
    }

    /**
     * Pause audio guide
     */
    fun pause() {
        audioPlayer.pause()
        saveCurrentPosition()
    }

    /**
     * Resume audio guide
     */
    fun resume() {
        audioPlayer.resume()
    }

    /**
     * Stop audio guide
     */
    fun stop() {
        audioPlayer.stop()
        resetPosition()
    }

    /**
     * Replay dari awal
     */
    fun replay() {
        resetPosition()
        play()
    }

    /**
     * Set playback speed
     */
    fun setSpeed(speed: Float) {
        audioPlayer.setSpeed(speed)
    }

    /**
     * Change language dan reload narration
     */
    fun changeLanguage(language: String, place: Place) {
        if (language != _currentLanguage.value) {
            _currentLanguage.value = language
            audioPlayer.setLanguage(language)

            // Stop current playback dan load narration baru
            stop()
            loadNarration(place, forceRegenerate = false)
        }
    }

    /**
     * Check apakah user dalam radius untuk auto-trigger
     */
    fun checkProximity(place: Place) {
        viewModelScope.launch {
            try {
                val isNearby = locationService.isWithinRadius(
                    targetLat = place.latitude,
                    targetLon = place.longitude,
                    radiusMeters = LocationService.NOTIFICATION_RADIUS
                )

                if (isNearby) {
                    Log.d(TAG, "📍 User is within range of ${place.name}")
                    // Auto-load narration jika belum loaded
                    if (_currentNarration.value == null) {
                        loadNarration(place)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking proximity: ${e.message}")
            }
        }
    }

    /**
     * Toggle play/pause
     */
    fun togglePlayback() {
        when (playerState.value) {
            is AudioPlayerState.Playing -> pause()
            is AudioPlayerState.Paused -> resume()
            is AudioPlayerState.Stopped, is AudioPlayerState.Idle -> play()
            else -> {
                Log.w(TAG, "Cannot toggle playback in current state: ${playerState.value}")
            }
        }
    }

    /**
     * Stop playback completely
     */
    fun stopPlayback() {
        stop()
    }

    /**
     * Request narration for a place (called from UI)
     */
    fun requestNarration(place: Place?) {
        place?.let {
            loadNarration(it, forceRegenerate = false)
        } ?: run {
            _errorMessage.value = "No place selected"
            Log.w(TAG, "⚠️ Cannot request narration: No place provided")
        }
    }

    /**
     * Get distance ke place (untuk UI)
     */
    fun updateDistance(place: Place) {
        viewModelScope.launch {
            try {
                val location = locationService.getCurrentLocation()
                if (location != null) {
                    val distance = locationService.calculateDistance(
                        location.latitude,
                        location.longitude,
                        place.latitude,
                        place.longitude
                    )
                    _distanceToPlace.value = distance
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating distance: ${e.message}")
            }
        }
    }

    /**
     * Download narration untuk offline mode
     */
    fun downloadForOffline() {
        viewModelScope.launch {
            _currentNarration.value?.let { narration ->
                val updated = narration.copy(isDownloaded = true)
                narrationDao.insertNarration(updated)
                _currentNarration.value = updated
                Log.d(TAG, "💾 Narration downloaded for offline use")
            }
        }
    }

    /**
     * Save current playback position
     */
    private fun saveCurrentPosition() {
        viewModelScope.launch {
            _currentNarration.value?.let { narration ->
                val position = (progress.value * narration.duration).toInt()
                narrationDao.updateLastPosition(
                    placeId = narration.placeId,
                    position = position,
                    language = narration.language
                )
            }
        }
    }

    /**
     * Reset playback position
     */
    private fun resetPosition() {
        viewModelScope.launch {
            _currentNarration.value?.let { narration ->
                narrationDao.updateLastPosition(
                    placeId = narration.placeId,
                    position = 0,
                    language = narration.language
                )
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * Cleanup resources
     */
    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
        Log.d(TAG, "🔚 ViewModel cleared")
    }
}
