package com.example.cultural_navigation_papb.data.dao

import androidx.room.*
import com.example.cultural_navigation_papb.data.models.VisitedPlace
import kotlinx.coroutines.flow.Flow

/**
 * DAO for managing visited/downloaded places data
 * Simplified for inbox/offline functionality
 */
@Dao
interface VisitedPlaceDao {

    /**
     * Insert a new visited place record
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVisitedPlace(visitedPlace: VisitedPlace)

    /**
     * Check if a specific place is downloaded/visited
     */
    @Query("SELECT COUNT(*) > 0 FROM visited_places WHERE id = :placeId")
    fun isPlaceVisited(placeId: String): Flow<Boolean>

    /**
     * Delete a visited place record
     */
    @Query("DELETE FROM visited_places WHERE id = :placeId")
    suspend fun deleteVisitedPlace(placeId: String)

    /**
     * Get all visited places
     */
    @Query("SELECT * FROM visited_places ORDER BY visitDate DESC")
    fun getAllVisitedPlaces(): Flow<List<VisitedPlace>>

    /**
     * Get a specific visited place
     */
    @Query("SELECT * FROM visited_places WHERE id = :placeId LIMIT 1")
    suspend fun getVisitedPlace(placeId: String): VisitedPlace?
}