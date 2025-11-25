package com.example.cultural_navigation_papb.data.repository

import com.example.cultural_navigation_papb.data.dao.PlaceDao
import com.example.cultural_navigation_papb.data.dao.ReviewDao
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.models.Review
import com.example.cultural_navigation_papb.data.PrambananData
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*

/**
 * Repository untuk Place
 * Layer yang menghubungkan ViewModel dengan sumber data (Database)
 */
@Singleton
class PlaceRepository @Inject constructor(
    private val placeDao: PlaceDao,
    private val reviewDao: ReviewDao
) {

    /**
     * Inisialisasi database dengan data candi Prambanan
     */
    suspend fun initializeDatabase() {
        try {
            val allPlaces = placeDao.getAllPlaces().first()
            if (allPlaces.isEmpty()) {
                // Database kosong, insert data awal
                placeDao.insertPlaces(PrambananData.allTemples)
            }
        } catch (e: Exception) {
            // Error handling
        }
    }

    /**
     * Mengambil semua places dari database
     */
    fun getAllPlaces(): Flow<List<Place>> {
        return placeDao.getAllPlaces()
    }

    /**
     * Mengambil tempat yang tersedia (buka)
     */
    fun getAvailablePlaces(): Flow<List<Place>> {
        return placeDao.getAvailablePlaces()
    }

    /**
     * Mengambil tempat berdasarkan kategori
     */
    fun getPlacesByCategory(category: String): Flow<List<Place>> {
        return placeDao.getPlacesByCategory(category)
    }

    /**
     * Mengambil tempat terdekat dari lokasi user
     */
    suspend fun getNearbyPlaces(userLocation: LatLng, limit: Int = 10): List<Place> {
        return placeDao.getNearbyPlaces(userLocation.latitude, userLocation.longitude, limit)
    }

    /**
     * Menghitung jarak antara dua titik (dalam meter)
     */
    fun calculateDistance(from: LatLng, to: LatLng): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            from.latitude, from.longitude,
            to.latitude, to.longitude,
            results
        )
        return results[0]
    }

    /**
     * Mencari tempat berdasarkan nama
     */
    suspend fun searchPlaces(query: String): List<Place> {
        return placeDao.searchPlaces(query)
    }

    /**
     * Mengambil place berdasarkan ID
     */
    suspend fun getPlaceById(placeId: String): Place? {
        return placeDao.getPlaceById(placeId)
    }

    /**
     * Mendapatkan rekomendasi path berdasarkan lokasi user
     */
    suspend fun getRecommendedPath(userLocation: LatLng, duration: String = "pendek"): List<Place> {
        val pathIds = PrambananData.getPathByDuration(duration)
        val places = mutableListOf<Place>()

        for (placeId in pathIds) {
            placeDao.getPlaceById(placeId)?.let { place ->
                places.add(place)
            }
        }

        // Sort berdasarkan jarak dari lokasi user
        return places.sortedBy { place ->
            calculateDistance(userLocation, place.getLatLng())
        }
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
     * Update rating place setelah review baru ditambahkan
     */
    suspend fun updatePlaceRating(placeId: String) {
        val avgRating = reviewDao.getAverageRatingForPlace(placeId) ?: 0.0f
        val reviewCount = reviewDao.getReviewCountForPlace(placeId)
        placeDao.updatePlaceRating(placeId, avgRating, reviewCount)
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

    // REVIEW RELATED METHODS

    /**
     * Mendapatkan review untuk tempat tertentu
     */
    fun getReviewsForPlace(placeId: String): Flow<List<Review>> {
        return reviewDao.getReviewsForPlace(placeId)
    }

    /**
     * Menambah review baru
     */
    suspend fun addReview(review: Review) {
        reviewDao.insertReview(review)
        // Update rating place
        updatePlaceRating(review.placeId)
    }

    /**
     * Menandai review sebagai helpful
     */
    suspend fun markReviewHelpful(reviewId: String) {
        reviewDao.incrementHelpfulCount(reviewId)
    }

    /**
     * Mendapatkan review dari user tertentu
     */
    fun getUserReviews(userId: String): Flow<List<Review>> {
        return reviewDao.getUserReviews(userId)
    }
}

