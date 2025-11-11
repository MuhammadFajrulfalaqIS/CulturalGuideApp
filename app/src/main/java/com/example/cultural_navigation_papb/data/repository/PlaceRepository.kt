package com.example.cultural_navigation_papb.data.repository

import com.example.cultural_navigation_papb.data.dao.PlaceDao
import com.example.cultural_navigation_papb.data.models.Place
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository untuk Place
 * Layer yang menghubungkan ViewModel dengan sumber data (Database)
 *
 * CATATAN: File ini sudah disiapkan untuk implementasi database nantinya
 * Saat ini belum digunakan karena ListScreen hanya menampilkan data statis dari ViewModel
 */
@Singleton
class PlaceRepository @Inject constructor(
    private val placeDao: PlaceDao
) {

    /**
     * Mengambil semua places dari database
     * Menggunakan Flow untuk observasi data realtime
     */
    fun getAllPlaces(): Flow<List<Place>> {
        return placeDao.getAllPlaces()
    }

    /**
     * Mengambil place berdasarkan ID
     */
    suspend fun getPlaceById(placeId: String): Place? {
        return placeDao.getPlaceById(placeId)
    }

    /**
     * Menambah place baru ke database
     */
    suspend fun insertPlace(place: Place) {
        placeDao.insertPlace(place)
    }

    /**
     * Menambah banyak places sekaligus
     */
    suspend fun insertPlaces(places: List<Place>) {
        placeDao.insertPlaces(places)
    }

    /**
     * Update place
     */
    suspend fun updatePlace(place: Place) {
        placeDao.updatePlace(place)
    }

    /**
     * Hapus place
     */
    suspend fun deletePlace(place: Place) {
        placeDao.deletePlace(place)
    }

    /**
     * Hapus semua places
     */
    suspend fun deleteAllPlaces() {
        placeDao.deleteAllPlaces()
    }
}

