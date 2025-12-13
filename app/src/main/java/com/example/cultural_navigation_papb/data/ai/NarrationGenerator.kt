package com.example.cultural_navigation_papb.data.ai

import android.util.Log
import com.example.cultural_navigation_papb.data.dao.NarrationDao
import com.example.cultural_navigation_papb.data.models.Narration
import com.example.cultural_navigation_papb.data.models.Place
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generator untuk membuat narasi audio guide menggunakan Gemini AI
 */
@Singleton
class NarrationGenerator @Inject constructor(
    private val narrationDao: NarrationDao,
    private val functions: FirebaseFunctions
) {
    companion object {
        private const val TAG = "NarrationGenerator"
        private const val FUNCTION_NAME = "generateNarration"
    }

    /**
     * Generate narration untuk tempat wisata
     * Check cache terlebih dahulu, jika tidak ada baru generate baru
     */
    suspend fun generateNarration(
        place: Place,
        language: String = "id",
        forceRegenerate: Boolean = false
    ): Result<Narration> {
        return try {
            // Check cache jika tidak force regenerate
            if (!forceRegenerate) {
                val cachedNarration = narrationDao.getNarration(place.id, language)
                if (cachedNarration != null) {
                    Log.d(TAG, "✅ Using cached narration for ${place.name}")
                    return Result.success(cachedNarration)
                }
            }

            Log.d(TAG, "🔄 Generating new narration for ${place.name} in $language")

            // Build prompt untuk Gemini AI
            val prompt = buildPrompt(place, language)

            // Call Firebase Function yang akan memanggil Gemini AI
            val data = hashMapOf(
                "prompt" to prompt,
                "placeId" to place.id,
                "placeName" to place.name,
                "language" to language
            )

            val result = functions
                .getHttpsCallable(FUNCTION_NAME)
                .call(data)
                .await()

            val narrationText = (result.getData() as? Map<*, *>)?.get("narration") as? String
                ?: throw Exception("Invalid response from AI service")

            // Estimate duration (rata-rata 150 kata per menit untuk bahasa Indonesia)
            val wordCount = narrationText.split("\\s+".toRegex()).size
            val estimatedDuration = (wordCount / 2.5).toInt() // words per second

            // Create and cache narration
            val narration = Narration(
                placeId = place.id,
                placeName = place.name,
                narrationText = narrationText,
                language = language,
                generatedAt = System.currentTimeMillis(),
                duration = estimatedDuration,
                lastPlayedPosition = 0,
                isDownloaded = false
            )

            narrationDao.insertNarration(narration)
            Log.d(TAG, "✅ Narration generated and cached for ${place.name}")

            Result.success(narration)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error generating narration: ${e.message}", e)

            // Fallback: gunakan deskripsi asli jika AI gagal
            val fallbackNarration = createFallbackNarration(place, language)
            narrationDao.insertNarration(fallbackNarration)

            Result.success(fallbackNarration)
        }
    }

    /**
     * Build prompt untuk Gemini AI
     */
    private fun buildPrompt(place: Place, language: String): String {
        return when (language) {
            "id" -> """
                Kamu adalah pemandu wisata profesional di Indonesia yang berpengalaman dan ramah.
                Buatkan narasi audio guide yang menarik, informatif, dan mudah dipahami dalam bahasa Indonesia untuk tempat wisata berikut:
                
                Nama: ${place.name}
                Deskripsi: ${place.detailedDescription}
                Sejarah: ${place.historicalInfo}
                Arsitektur: ${place.architectureInfo}
                
                Gunakan gaya bercerita yang engaging dan personal, seolah-olah sedang berbicara langsung dengan wisatawan.
                Durasi ideal: 2-3 menit (300-450 kata).
                Fokus pada fakta menarik, sejarah penting, dan tips untuk pengunjung.
                Mulai dengan sambutan hangat dan akhiri dengan ajakan untuk menikmati pengalaman.
                
                Format: Narasi langsung tanpa judul atau format khusus.
            """.trimIndent()

            "en" -> """
                You are a professional and friendly tour guide in Indonesia.
                Create an engaging, informative, and easy-to-understand audio guide narration in English for this tourist destination:
                
                Name: ${place.name}
                Description: ${place.detailedDescription}
                History: ${place.historicalInfo}
                Architecture: ${place.architectureInfo}
                
                Use an engaging and personal storytelling style, as if speaking directly to tourists.
                Ideal duration: 2-3 minutes (300-450 words).
                Focus on interesting facts, important history, and tips for visitors.
                Start with a warm greeting and end with an invitation to enjoy the experience.
                
                Format: Direct narration without titles or special formatting.
            """.trimIndent()

            else -> buildPrompt(place, "id") // Default ke bahasa Indonesia
        }
    }

    /**
     * Create fallback narration jika AI gagal
     */
    private fun createFallbackNarration(place: Place, language: String): Narration {
        val narrationText = when (language) {
            "id" -> """
                Selamat datang di ${place.name}.
                
                ${place.detailedDescription}
                
                ${if (place.historicalInfo.isNotEmpty()) "Sejarah: ${place.historicalInfo}" else ""}
                
                ${if (place.architectureInfo.isNotEmpty()) "Arsitektur: ${place.architectureInfo}" else ""}
                
                ${if (place.visitingInfo.isNotEmpty()) "Informasi Kunjungan: ${place.visitingInfo}" else ""}
                
                Terima kasih telah menggunakan audio guide kami. Selamat menikmati kunjungan Anda!
            """.trimIndent()

            "en" -> """
                Welcome to ${place.name}.
                
                ${place.detailedDescription}
                
                ${if (place.historicalInfo.isNotEmpty()) "History: ${place.historicalInfo}" else ""}
                
                ${if (place.architectureInfo.isNotEmpty()) "Architecture: ${place.architectureInfo}" else ""}
                
                ${if (place.visitingInfo.isNotEmpty()) "Visiting Information: ${place.visitingInfo}" else ""}
                
                Thank you for using our audio guide. Enjoy your visit!
            """.trimIndent()

            else -> createFallbackNarration(place, "id").narrationText
        }

        val wordCount = narrationText.split("\\s+".toRegex()).size
        val estimatedDuration = (wordCount / 2.5).toInt()

        return Narration(
            placeId = place.id,
            placeName = place.name,
            narrationText = narrationText,
            language = language,
            generatedAt = System.currentTimeMillis(),
            duration = estimatedDuration,
            lastPlayedPosition = 0,
            isDownloaded = false
        )
    }

    /**
     * Delete old narrations (lebih dari 30 hari)
     */
    suspend fun cleanupOldNarrations() {
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        narrationDao.deleteOldNarrations(thirtyDaysAgo)
    }
}
