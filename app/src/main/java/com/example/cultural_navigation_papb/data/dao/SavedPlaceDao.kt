package com.example.cultural_navigation_papb.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cultural_navigation_papb.data.models.SavedPlace
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPlaceDao {
    @Query("SELECT * FROM saved_places ORDER BY downloadedAt DESC")
    fun getAllSavedPlaces(): Flow<List<SavedPlace>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedPlace(place: SavedPlace)

    @Query("DELETE FROM saved_places WHERE id = :id")
    suspend fun deleteSavedPlace(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_places WHERE id = :id)")
    fun isPlaceSaved(id: String): Flow<Boolean>
}