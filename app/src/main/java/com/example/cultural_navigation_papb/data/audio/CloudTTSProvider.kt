package com.example.cultural_navigation_papb.data.audio

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Cloud-based TTS Provider sebagai fallback
 * Menggunakan Google Translate TTS API (gratis, tidak perlu API key)
 */
@Singleton
class CloudTTSProvider @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "CloudTTSProvider"

        // Google Translate TTS API (gratis, tanpa API key)
        private const val GOOGLE_TTS_URL = "https://translate.google.com/translate_tts"

        // Backup: ResponsiveVoice API
        private const val RESPONSIVE_VOICE_URL = "https://code.responsivevoice.org/getvoice.php"
    }

    /**
     * Generate audio dari text menggunakan Google Translate TTS
     */
    suspend fun generateAudio(text: String, language: String = "id"): Result<File> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🌐 Generating audio from cloud TTS...")

            // Split text jika terlalu panjang (Google TTS max 200 chars per request)
            val chunks = text.chunked(200)
            val audioFiles = mutableListOf<File>()

            chunks.forEachIndexed { index, chunk ->
                val audioFile = generateChunk(chunk, language, index)
                if (audioFile != null) {
                    audioFiles.add(audioFile)
                }
            }

            if (audioFiles.isEmpty()) {
                return@withContext Result.failure(Exception("Failed to generate audio"))
            }

            // Jika hanya 1 chunk, return langsung
            if (audioFiles.size == 1) {
                return@withContext Result.success(audioFiles.first())
            }

            // Merge multiple audio files
            val mergedFile = mergeAudioFiles(audioFiles)
            audioFiles.forEach { it.delete() } // Cleanup individual chunks

            Result.success(mergedFile)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error generating cloud TTS: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Generate single audio chunk
     */
    private fun generateChunk(text: String, language: String, index: Int): File? {
        try {
            val encodedText = URLEncoder.encode(text, "UTF-8")
            val urlString = "$GOOGLE_TTS_URL?ie=UTF-8&client=tw-ob&tl=$language&q=$encodedText"

            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.connectTimeout = 10000
            connection.readTimeout = 10000

            if (connection.responseCode == 200) {
                val inputStream = connection.inputStream
                val outputFile = File(context.cacheDir, "tts_chunk_$index.mp3")
                val outputStream = FileOutputStream(outputFile)

                inputStream.copyTo(outputStream)

                outputStream.close()
                inputStream.close()

                Log.d(TAG, "✅ Generated chunk $index: ${outputFile.length()} bytes")
                return outputFile
            } else {
                Log.e(TAG, "❌ HTTP error: ${connection.responseCode}")
                return null
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error generating chunk $index: ${e.message}")
            return null
        }
    }

    /**
     * Merge multiple audio files (simple concatenation for MP3)
     */
    private fun mergeAudioFiles(files: List<File>): File {
        val mergedFile = File(context.cacheDir, "tts_merged_${System.currentTimeMillis()}.mp3")
        val outputStream = FileOutputStream(mergedFile)

        files.forEach { file ->
            file.inputStream().use { input ->
                input.copyTo(outputStream)
            }
        }

        outputStream.close()
        Log.d(TAG, "✅ Merged ${files.size} chunks into ${mergedFile.length()} bytes")
        return mergedFile
    }

    /**
     * Check apakah bisa akses cloud TTS (internet connection)
     */
    fun isAvailable(): Boolean {
        return try {
            val url = URL("https://www.google.com")
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 3000
            connection.connect()
            val available = connection.responseCode == 200
            connection.disconnect()
            available
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Cleanup old cache files
     */
    fun cleanupCache() {
        val cacheDir = context.cacheDir
        cacheDir.listFiles()?.filter {
            it.name.startsWith("tts_chunk_") || it.name.startsWith("tts_merged_")
        }?.forEach {
            it.delete()
            Log.d(TAG, "🗑️ Deleted cache file: ${it.name}")
        }
    }
}

