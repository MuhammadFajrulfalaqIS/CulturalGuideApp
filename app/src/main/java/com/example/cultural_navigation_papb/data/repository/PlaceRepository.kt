package com.example.cultural_navigation_papb.data.repository

import com.example.cultural_navigation_papb.data.dao.PlaceDao
import com.example.cultural_navigation_papb.data.dao.ReviewDao
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.models.Review
import com.example.cultural_navigation_papb.data.PrambananData
import com.example.cultural_navigation_papb.data.api.FirestoreService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.*
import android.util.Log

/**
 * Repository untuk Place
 * Layer yang menghubungkan ViewModel dengan sumber data (Database + Firestore)
 */
@Singleton
class PlaceRepository @Inject constructor(
    private val placeDao: PlaceDao,
    private val reviewDao: ReviewDao,
    private val firestoreService: FirestoreService
) {
    companion object {
        private const val TAG = "PlaceRepository"
    }

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

    // REVIEW RELATED METHODS

    /**
     * Mendapatkan review untuk tempat tertentu dari Firestore
     */
    suspend fun getReviewsForPlace(placeId: String): List<Review> {
        return try {
            val result = firestoreService.getReviewsForPlace(placeId)
            result.getOrElse { exception ->
                Log.e(TAG, "Error getting reviews from Firestore, falling back to local DB: ${exception.message}")
                // Fallback ke local database
                reviewDao.getReviewsForPlaceSync(placeId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getReviewsForPlace: ${e.message}", e)
            // Fallback ke local database
            reviewDao.getReviewsForPlaceSync(placeId)
        }
    }

    /**
     * Menambah review baru ke Firestore dan sync ke local DB
     */
    suspend fun addReview(review: Review) {
        try {
            // Pertama, coba simpan ke Firestore
            val firestoreResult = firestoreService.addReview(review)

            if (firestoreResult.isSuccess) {
                Log.d(TAG, "✅ Review saved to Firestore: ${review.id}")
            } else {
                Log.e(TAG, "❌ Firestore save failed, will save to local DB only")
            }

            // Juga simpan ke local database untuk offline support
            reviewDao.insertReview(review)
            Log.d(TAG, "✅ Review saved to local DB: ${review.id}")

            // Update rating place
            updatePlaceRating(review.placeId)
        } catch (e: Exception) {
            Log.e(TAG, "Error in addReview: ${e.message}", e)
            // Tetap simpan ke local database sebagai fallback
            try {
                reviewDao.insertReview(review)
                updatePlaceRating(review.placeId)
            } catch (localE: Exception) {
                Log.e(TAG, "Failed to save to local DB too: ${localE.message}", localE)
                throw localE
            }
        }
    }

    /**
     * Menandai review sebagai helpful di Firestore
     */
    suspend fun markReviewHelpful(reviewId: String) {
        try {
            val firestoreResult = firestoreService.incrementHelpfulCount(reviewId)
            if (firestoreResult.isSuccess) {
                Log.d(TAG, "✅ Helpful count incremented in Firestore: $reviewId")
            }
            // Also update local DB
            reviewDao.incrementHelpfulCount(reviewId)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking review helpful: ${e.message}", e)
            // Fallback to local DB only
            try {
                reviewDao.incrementHelpfulCount(reviewId)
            } catch (localE: Exception) {
                Log.e(TAG, "Failed to update local DB: ${localE.message}", localE)
                throw localE
            }
        }
    }

    /**
     * Mendapatkan review dari user tertentu dari Firestore
     */
    suspend fun getUserReviews(userId: String): List<Review> {
        return try {
            val result = firestoreService.getUserReviews(userId)
            result.getOrElse { exception ->
                Log.e(TAG, "Error getting user reviews from Firestore, falling back to local DB: ${exception.message}")
                // Fallback ke local database
                reviewDao.getUserReviewsSync(userId)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception in getUserReviews: ${e.message}", e)
            // Fallback ke local database
            reviewDao.getUserReviewsSync(userId)
        }
    }

    /**
     * Update average rating dan review count untuk place
     */
    suspend fun updatePlaceRating(placeId: String) {
        try {
            // Try to get from Firestore first
            val avgRatingResult = firestoreService.getAverageRatingForPlace(placeId)
            val reviewCountResult = firestoreService.getReviewCountForPlace(placeId)

            val avgRating = avgRatingResult.getOrNull() ?: 0f
            val reviewCount = reviewCountResult.getOrNull() ?: 0

            if (avgRating > 0 || reviewCount > 0) {
                placeDao.updatePlaceRating(placeId, avgRating, reviewCount)
                Log.d(TAG, "✅ Updated place rating: $placeId (avg: $avgRating, count: $reviewCount)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating place rating: ${e.message}", e)
            // Fallback to local calculation
            try {
                val avgRating = reviewDao.getAverageRatingForPlace(placeId) ?: 0.0f
                val reviewCount = reviewDao.getReviewCountForPlace(placeId)
                placeDao.updatePlaceRating(placeId, avgRating, reviewCount)
            } catch (localE: Exception) {
                Log.e(TAG, "Failed to update from local DB: ${localE.message}", localE)
            }
        }
    }
}
