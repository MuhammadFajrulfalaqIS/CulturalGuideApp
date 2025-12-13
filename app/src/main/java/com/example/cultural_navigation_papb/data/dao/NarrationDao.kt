package com.example.cultural_navigation_papb.data.dao

import androidx.room.*
import com.example.cultural_navigation_papb.data.models.Narration
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk akses data narration dari Room database
 */
@Dao
interface NarrationDao {

    @Query("SELECT * FROM narrations WHERE placeId = :placeId AND language = :language LIMIT 1")
    suspend fun getNarration(placeId: String, language: String = "id"): Narration?

    @Query("SELECT * FROM narrations WHERE placeId = :placeId AND language = :language LIMIT 1")
    fun getNarrationFlow(placeId: String, language: String = "id"): Flow<Narration?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNarration(narration: Narration)

    @Update
    suspend fun updateNarration(narration: Narration)

    @Query("UPDATE narrations SET lastPlayedPosition = :position WHERE placeId = :placeId AND language = :language")
    suspend fun updateLastPosition(placeId: String, position: Int, language: String = "id")

    @Query("DELETE FROM narrations WHERE placeId = :placeId")
    suspend fun deleteNarration(placeId: String)

    @Query("SELECT * FROM narrations WHERE isDownloaded = 1")
    fun getAllDownloadedNarrations(): Flow<List<Narration>>

    @Query("DELETE FROM narrations WHERE generatedAt < :timestamp")
    suspend fun deleteOldNarrations(timestamp: Long)
}

