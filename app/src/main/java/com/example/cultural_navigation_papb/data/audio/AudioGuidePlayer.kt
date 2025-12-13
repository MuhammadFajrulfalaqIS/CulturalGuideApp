package com.example.cultural_navigation_papb.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * State untuk audio player
 */
sealed class AudioPlayerState {
    object Idle : AudioPlayerState()
    object Loading : AudioPlayerState()
    object Playing : AudioPlayerState()
    object Paused : AudioPlayerState()
    object Stopped : AudioPlayerState()
    data class Error(val message: String) : AudioPlayerState()
}

/**
 * Audio Guide Player menggunakan Android Text-to-Speech
 */
@Singleton
class AudioGuidePlayer @Inject constructor(
    private val context: Context,
    private val cloudTTSProvider: CloudTTSProvider,
    private val cloudAudioPlayer: CloudAudioPlayer
) {
    private var tts: TextToSpeech? = null
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow<AudioPlayerState>(AudioPlayerState.Idle)
    val state: StateFlow<AudioPlayerState> = _state.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _currentSpeed = MutableStateFlow(1.0f)
    val currentSpeed: StateFlow<Float> = _currentSpeed.asStateFlow()

    private var currentNarration: String = ""
    private var currentUtteranceId: String = ""
    private var isInitialized = false

    // Voice preferences untuk tour guide
    private var preferredVoice: Voice? = null
    private val _currentPitch = MutableStateFlow(1.0f)
    val currentPitch: StateFlow<Float> = _currentPitch.asStateFlow()

    // Mode: native TTS atau cloud TTS
    private var isUsingCloudTTS = false
    private val _ttsMode = MutableStateFlow<TTSMode>(TTSMode.NATIVE)
    val ttsMode: StateFlow<TTSMode> = _ttsMode.asStateFlow()

    companion object {
        private const val TAG = "AudioGuidePlayer"
        private const val UTTERANCE_ID = "AudioGuide"

        // Voice preferences - prioritas suara tour guide Indonesia
        private val PREFERRED_VOICE_NAMES = listOf(
            "id-id-x-idd-network",      // Google Indonesian Female (paling bagus)
            "id-id-x-idm-network",      // Google Indonesian Male
            "id-id-x-idd-local",        // Local Indonesian Female
            "id-id-x-idm-local",        // Local Indonesian Male
            "id-ID-language",           // Fallback Indonesian
        )
    }

    /**
     * Initialize Text-to-Speech engine
     */
    fun initialize(onReady: () -> Unit = {}) {
        if (isInitialized) {
            onReady()
            return
        }

        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { engine ->
                    // Set default language ke Indonesian
                    val result = engine.setLanguage(Locale("id", "ID"))

                    if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.w(TAG, "⚠️ Indonesian language not supported, using default")
                        engine.setLanguage(Locale.US)
                    } else {
                        // UPGRADE: Cari suara terbaik untuk tour guide Indonesia
                        selectBestVoice(engine)
                    }

                    // UPGRADE: Set speech rate yang lebih santai untuk tour guide (0.9x)
                    engine.setSpeechRate(0.9f)
                    _currentSpeed.value = 0.9f

                    // UPGRADE: Set pitch yang lebih natural dan menarik
                    engine.setPitch(1.1f)
                    _currentPitch.value = 1.1f

                    // Set listener untuk progress
                    engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            Log.d(TAG, "🎵 Speech started")
                            _state.value = AudioPlayerState.Playing
                        }

                        override fun onDone(utteranceId: String?) {
                            Log.d(TAG, "✅ Speech completed")
                            _state.value = AudioPlayerState.Stopped
                            _progress.value = 1.0f
                            releaseAudioFocus()
                        }

                        override fun onError(utteranceId: String?) {
                            Log.e(TAG, "❌ Speech error")
                            _state.value = AudioPlayerState.Error("Playback error")
                            releaseAudioFocus()
                        }

                        override fun onRangeStart(
                            utteranceId: String?,
                            start: Int,
                            end: Int,
                            frame: Int
                        ) {
                            // Update progress based on character position
                            if (currentNarration.isNotEmpty()) {
                                val progress = start.toFloat() / currentNarration.length
                                _progress.value = progress
                            }
                        }
                    })

                    isInitialized = true
                    onReady()
                    Log.d(TAG, "✅ TTS initialized with voice: ${preferredVoice?.name ?: "default"}")
                }
            } else {
                Log.e(TAG, "❌ TTS initialization failed")
                _state.value = AudioPlayerState.Error("Text-to-Speech not available")
            }
        }
    }

    /**
     * BARU: Pilih suara terbaik untuk tour guide Indonesia
     */
    private fun selectBestVoice(engine: TextToSpeech) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val voices = engine.voices
            if (voices != null) {
                Log.d(TAG, "🎙️ Available voices: ${voices.size}")

                // Log semua voice yang tersedia untuk debugging
                voices.forEach { voice ->
                    Log.d(TAG, "  - ${voice.name} | Locale: ${voice.locale} | Quality: ${voice.quality}")
                }

                // Cari voice terbaik berdasarkan prioritas
                for (preferredName in PREFERRED_VOICE_NAMES) {
                    val voice = voices.find {
                        it.name.contains(preferredName, ignoreCase = true) ||
                        it.name.contains("id-ID", ignoreCase = true) ||
                        it.name.contains("indonesia", ignoreCase = true)
                    }
                    if (voice != null) {
                        preferredVoice = voice
                        engine.voice = voice
                        Log.d(TAG, "✅ Selected voice: ${voice.name} (${voice.locale})")
                        return
                    }
                }

                // Fallback: Cari voice Indonesian apapun dengan kualitas tertinggi
                val indonesianVoices = voices.filter {
                    it.locale.language == "id" ||
                    it.locale.toString().startsWith("id")
                }.sortedByDescending { it.quality }

                if (indonesianVoices.isNotEmpty()) {
                    preferredVoice = indonesianVoices.first()
                    engine.voice = indonesianVoices.first()
                    Log.d(TAG, "✅ Selected fallback voice: ${indonesianVoices.first().name}")
                } else {
                    Log.w(TAG, "⚠️ No Indonesian voice found, using default")
                }
            }
        } else {
            Log.d(TAG, "Voice selection not supported on this Android version")
        }
    }

    /**
     * BARU: Set pitch (tinggi rendah suara) - 0.5 (rendah) sampai 2.0 (tinggi)
     */
    fun setPitch(pitch: Float) {
        val validPitch = pitch.coerceIn(0.5f, 2.0f)
        tts?.setPitch(validPitch)
        _currentPitch.value = validPitch
        Log.d(TAG, "🎵 Pitch set to $validPitch")
    }

    /**
     * BARU: Get daftar voice yang tersedia untuk user bisa pilih
     */
    fun getAvailableVoices(): List<Voice> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return tts?.voices?.filter {
                it.locale.language == "id" || it.locale.toString().startsWith("id")
            }?.sortedByDescending { it.quality } ?: emptyList()
        }
        return emptyList()
    }

    /**
     * BARU: Set voice secara manual (kalau user mau pilih sendiri)
     */
    fun setVoice(voice: Voice) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts?.voice = voice
            preferredVoice = voice
            Log.d(TAG, "🎙️ Voice changed to: ${voice.name}")
        }
    }

    /**
     * BARU: Preset untuk berbagai gaya tour guide
     */
    fun applyTourGuidePreset(preset: TourGuidePreset) {
        when (preset) {
            TourGuidePreset.PROFESSIONAL -> {
                // Profesional dan jelas - suara standar, kecepatan normal
                setSpeed(0.9f)
                setPitch(1.0f)
                Log.d(TAG, "🎯 Applied PROFESSIONAL preset")
            }
            TourGuidePreset.FRIENDLY -> {
                // Ramah dan hangat - sedikit lebih tinggi, santai
                setSpeed(0.85f)
                setPitch(1.15f)
                Log.d(TAG, "🎯 Applied FRIENDLY preset")
            }
            TourGuidePreset.ENERGETIC -> {
                // Energik dan antusias - cepat dan tinggi
                setSpeed(1.0f)
                setPitch(1.2f)
                Log.d(TAG, "🎯 Applied ENERGETIC preset")
            }
            TourGuidePreset.CALM -> {
                // Tenang dan meditatif - lambat dan rendah
                setSpeed(0.75f)
                setPitch(0.9f)
                Log.d(TAG, "🎯 Applied CALM preset")
            }
        }
    }

    /**
     * Play narration text
     */
    fun play(narrationText: String, language: String = "id") {
        if (!isInitialized) {
            Log.w(TAG, "TTS not initialized yet")
            initialize {
                play(narrationText, language)
            }
            return
        }

        currentNarration = narrationText
        currentUtteranceId = UTTERANCE_ID + System.currentTimeMillis()

        // PERBAIKAN: Force ke Indonesian kalau detect karakter Indonesia
        val finalLanguage = if (language != "id" && narrationText.any { it.toInt() > 127 }) {
            Log.w(TAG, "⚠️ Detected Indonesian characters, forcing to Indonesian voice")
            "id"
        } else {
            language
        }

        // Set language dan voice
        val locale = when (finalLanguage) {
            "en" -> Locale.US
            "id" -> Locale("id", "ID")
            else -> Locale("id", "ID")  // Default ke Indonesian
        }

        tts?.let { engine ->
            val langResult = engine.setLanguage(locale)

            // PERBAIKAN UTAMA: Kalau voice Indonesia tidak tersedia, pakai Cloud TTS
            if (langResult == TextToSpeech.LANG_MISSING_DATA ||
                langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "⚠️ Native TTS not available, switching to Cloud TTS...")
                playWithCloudTTS(narrationText, finalLanguage)
                return
            }

            Log.d(TAG, "🌐 Language set to: ${locale} (requested: $finalLanguage)")

            // Re-apply preferred voice setelah change language
            if (finalLanguage == "id") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (preferredVoice != null && preferredVoice!!.locale.language == "id") {
                        engine.voice = preferredVoice
                        Log.d(TAG, "✅ Using preferred Indonesian voice: ${preferredVoice!!.name}")
                    } else {
                        // Coba cari voice Indonesia lagi
                        selectBestVoice(engine)

                        // Kalau masih tidak ada, fallback ke cloud
                        if (preferredVoice == null) {
                            Log.w(TAG, "⚠️ No Indonesian voice found, switching to Cloud TTS...")
                            playWithCloudTTS(narrationText, finalLanguage)
                            return
                        }
                    }
                }
            }
        }

        // Request audio focus
        if (requestAudioFocus()) {
            _state.value = AudioPlayerState.Loading
            _progress.value = 0f
            _ttsMode.value = TTSMode.NATIVE

            // Split narration jika terlalu panjang (max 4000 characters per utterance)
            val chunks = narrationText.chunked(4000)

            chunks.forEachIndexed { index, chunk ->
                val queueMode = if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
                tts?.speak(chunk, queueMode, null, currentUtteranceId + index)
            }

            isUsingCloudTTS = false
            Log.d(TAG, "▶️ Playing narration (${chunks.size} chunks) with ${locale} voice")
        } else {
            _state.value = AudioPlayerState.Error("Cannot get audio focus")
        }
    }

    /**
     * BARU: Play dengan Cloud TTS (fallback)
     */
    private fun playWithCloudTTS(text: String, language: String) {
        coroutineScope.launch {
            try {
                _state.value = AudioPlayerState.Loading
                _ttsMode.value = TTSMode.CLOUD
                Log.d(TAG, "☁️ Generating audio using Cloud TTS...")

                val result = cloudTTSProvider.generateAudio(text, language)

                result.fold(
                    onSuccess = { audioFile ->
                        Log.d(TAG, "✅ Cloud TTS audio generated: ${audioFile.length()} bytes")

                        // Monitor cloud player state
                        launch {
                            cloudAudioPlayer.state.collect { state ->
                                _state.value = state
                            }
                        }

                        launch {
                            cloudAudioPlayer.progress.collect { progress ->
                                _progress.value = progress
                            }
                        }

                        // Play audio
                        cloudAudioPlayer.play(audioFile)
                        isUsingCloudTTS = true
                        Log.d(TAG, "🎵 Playing with Cloud TTS")
                    },
                    onFailure = { error ->
                        Log.e(TAG, "❌ Cloud TTS failed: ${error.message}")
                        _state.value = AudioPlayerState.Error(
                            "Tidak dapat memutar audio. Pastikan:\n" +
                            "1. HP terhubung internet, ATAU\n" +
                            "2. Install 'Google Text-to-Speech' dari Play Store"
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error in Cloud TTS: ${e.message}", e)
                _state.value = AudioPlayerState.Error("Cloud TTS error: ${e.message}")
            }
        }
    }

    /**
     * Pause playback
     */
    fun pause() {
        if (isUsingCloudTTS) {
            cloudAudioPlayer.pause()
        } else {
            if (_state.value is AudioPlayerState.Playing) {
                tts?.stop()
                _state.value = AudioPlayerState.Paused
                releaseAudioFocus()
                Log.d(TAG, "⏸️ Paused")
            }
        }
    }

    /**
     * Resume playback
     */
    fun resume() {
        if (isUsingCloudTTS) {
            cloudAudioPlayer.resume()
        } else {
            if (_state.value is AudioPlayerState.Paused && currentNarration.isNotEmpty()) {
                play(currentNarration)
                Log.d(TAG, "▶️ Resumed")
            }
        }
    }

    /**
     * Stop playback
     */
    fun stop() {
        if (isUsingCloudTTS) {
            cloudAudioPlayer.stop()
        } else {
            tts?.stop()
        }
        _state.value = AudioPlayerState.Stopped
        _progress.value = 0f
        currentNarration = ""
        releaseAudioFocus()
        Log.d(TAG, "⏹️ Stopped")
    }

    /**
     * Set playback speed
     */
    fun setSpeed(speed: Float) {
        val validSpeed = speed.coerceIn(0.5f, 2.0f)

        if (isUsingCloudTTS) {
            cloudAudioPlayer.setSpeed(validSpeed)
        } else {
            tts?.setSpeechRate(validSpeed)
        }

        _currentSpeed.value = validSpeed
        Log.d(TAG, "⚡ Speed set to ${validSpeed}x")
    }

    /**
     * Change language
     */
    fun setLanguage(language: String) {
        val locale = when (language) {
            "en" -> Locale.US
            "id" -> Locale("id", "ID")
            else -> Locale("id", "ID")
        }
        tts?.setLanguage(locale)
        Log.d(TAG, "🌐 Language set to $language")
    }

    /**
     * Request audio focus
     */
    private fun requestAudioFocus(): Boolean {
        audioManager?.let { manager ->
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()

                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener { focusChange ->
                        handleAudioFocusChange(focusChange)
                    }
                    .build()

                val result = manager.requestAudioFocus(audioFocusRequest!!)
                result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            } else {
                @Suppress("DEPRECATION")
                val result = manager.requestAudioFocus(
                    { focusChange -> handleAudioFocusChange(focusChange) },
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
                result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
            }
        }
        return false
    }

    /**
     * Release audio focus
     */
    private fun releaseAudioFocus() {
        audioManager?.let { manager ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { manager.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                manager.abandonAudioFocus(null)
            }
        }
    }

    /**
     * Handle audio focus changes
     */
    private fun handleAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Lost focus permanently - stop playback
                stop()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Lost focus temporarily - pause
                pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Can continue at lower volume
                tts?.setSpeechRate(0.5f)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Regained focus - restore normal playback
                tts?.setSpeechRate(_currentSpeed.value)
            }
        }
    }

    /**
     * Release resources
     */
    fun release() {
        stop()
        tts?.shutdown()
        tts = null
        cloudAudioPlayer.release()
        cloudTTSProvider.cleanupCache()
        isInitialized = false
        Log.d(TAG, "🔚 Resources released")
    }
}

/**
 * Preset gaya tour guide
 */
enum class TourGuidePreset {
    PROFESSIONAL,  // Standar tour guide profesional
    FRIENDLY,      // Ramah dan casual
    ENERGETIC,     // Antusias dan bersemangat
    CALM          // Tenang dan santai
}

/**
 * TTS Mode
 */
enum class TTSMode {
    NATIVE,  // Android native TTS
    CLOUD    // Cloud-based TTS (Google Translate API)
}
