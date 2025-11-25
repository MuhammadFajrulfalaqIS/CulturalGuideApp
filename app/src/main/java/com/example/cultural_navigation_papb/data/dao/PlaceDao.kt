package com.example.cultural_navigation_papb.data.dao

import androidx.room.*
import com.example.cultural_navigation_papb.data.models.Place
import kotlinx.coroutines.flow.Flow
import com.google.android.gms.maps.model.LatLng

/**
 * DAO (Data Access Object) untuk Place
 * Interface untuk mengakses database Room
 */
@Dao
interface PlaceDao {

    /**
     * Mengambil semua data place dari database
     * Menggunakan Flow untuk observasi data secara realtime
     */
    @Query("SELECT * FROM places ORDER BY name ASC")
    fun getAllPlaces(): Flow<List<Place>>

    /**
     * Mengambil tempat yang tersedia (buka)
     */
    @Query("SELECT * FROM places WHERE isAvailable = 1 ORDER BY name ASC")
    fun getAvailablePlaces(): Flow<List<Place>>

    /**
     * Mengambil tempat berdasarkan kategori
     */
    @Query("SELECT * FROM places WHERE category = :category ORDER BY name ASC")
    fun getPlacesByCategory(category: String): Flow<List<Place>>

    /**
     * Mengambil tempat terdekat dari lokasi user
     */
    @Query("""
        SELECT *,
        ((latitude - :userLat) * (latitude - :userLat) +
         (longitude - :userLng) * (longitude - :userLng)) as distance
        FROM places
        WHERE isAvailable = 1
        ORDER BY distance ASC
        LIMIT :limit
    """)
    suspend fun getNearbyPlaces(userLat: Double, userLng: Double, limit: Int = 10): List<Place>

    /**
     * Mencari tempat berdasarkan nama
     */
    @Query("SELECT * FROM places WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchPlaces(query: String): List<Place>

    /**
     * Mengambil satu place berdasarkan ID
     */
    @Query("SELECT * FROM places WHERE id = :placeId")
    suspend fun getPlaceById(placeId: String): Place?

    /**
     * Menambah atau update place
     * OnConflict REPLACE: jika ID sudah ada, data akan di-replace
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: Place)

    /**
     * Menambah banyak places sekaligus
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<Place>)

    /**
     * Update place
     */
    @Update
    suspend fun updatePlace(place: Place)

    /**
     * Update rating dan review count
     */
    @Query("UPDATE places SET rating = :rating, reviewCount = :reviewCount WHERE id = :placeId")
    suspend fun updatePlaceRating(placeId: String, rating: Float, reviewCount: Int)

    /**
     * Hapus place
     */
    @Delete
    suspend fun deletePlace(place: Place)

    /**
     * Hapus semua data
     */
    @Query("DELETE FROM places")
    suspend fun deleteAllPlaces()
}

