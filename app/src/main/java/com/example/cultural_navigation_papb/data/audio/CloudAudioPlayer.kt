package com.example.cultural_navigation_papb.data.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Audio player untuk file MP3 dari cloud TTS
 */
@Singleton
class CloudAudioPlayer @Inject constructor(
    private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null

    private val _state = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Idle)
    val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _currentSpeed = MutableStateFlow(1.0f)
    val currentSpeed: StateFlow<Float> = _currentSpeed.asStateFlow()

    companion object {
        private const val TAG = "CloudAudioPlayer"
    }

    /**
     * Play audio file
     */
    fun play(audioFile: File) {
        try {
            // Release previous player
            release()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                prepare()

                setOnCompletionListener {
                    _state.value = AudioPlayerState.Stopped
                    _progress.value = 1.0f
                }

                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                    _state.value = AudioPlayerState.Error("Playback error")
                    true
                }

                start()
                _state.value = AudioPlayerState.Playing
            }

            // Start progress tracking
            startProgressTracking()

            Log.d(TAG, "▶️ Playing audio file: ${audioFile.name}")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error playing audio: ${e.message}", e)
            _state.value = AudioPlayerState.Error("Cannot play audio: ${e.message}")
        }
    }

    /**
     * Pause playback
     */
    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _state.value = AudioPlayerState.Paused
                Log.d(TAG, "⏸️ Paused")
            }
        }
    }

    /**
     * Resume playback
     */
    fun resume() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                _state.value = AudioPlayerState.Playing
                startProgressTracking()
                Log.d(TAG, "▶️ Resumed")
            }
        }
    }

    /**
     * Stop playback
     */
    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            _state.value = AudioPlayerState.Stopped
            _progress.value = 0f
            Log.d(TAG, "⏹️ Stopped")
        }
    }

    /**
     * Set playback speed (Android 6.0+)
     */
    fun setSpeed(speed: Float) {
        mediaPlayer?.let {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val validSpeed = speed.coerceIn(0.5f, 2.0f)
                it.playbackParams = it.playbackParams.setSpeed(validSpeed)
                _currentSpeed.value = validSpeed
                Log.d(TAG, "⚡ Speed set to ${validSpeed}x")
            }
        }
    }

    /**
     * Track progress
     */
    private fun startProgressTracking() {
        Thread {
            while (mediaPlayer?.isPlaying == true) {
                try {
                    val current = mediaPlayer?.currentPosition ?: 0
                    val duration = mediaPlayer?.duration ?: 1
                    _progress.value = current.toFloat() / duration.toFloat()
                    Thread.sleep(100)
                } catch (e: Exception) {
                    break
                }
            }
        }.start()
    }

    /**
     * Release resources
     */
    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        _state.value = AudioPlayerState.Idle
        _progress.value = 0f
    }
}

