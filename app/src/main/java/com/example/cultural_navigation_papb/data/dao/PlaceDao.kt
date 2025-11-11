package com.example.cultural_navigation_papb.data.dao

import androidx.room.*
import com.example.cultural_navigation_papb.data.models.Place
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) untuk Place
 * Interface untuk mengakses database Room
 *
 * CATATAN: File ini sudah disiapkan untuk implementasi database nantinya
 * Saat ini belum digunakan karena ListScreen hanya menampilkan data statis
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

