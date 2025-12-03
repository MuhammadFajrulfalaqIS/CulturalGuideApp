package com.example.cultural_navigation_papb.data.api

import android.util.Log
import com.example.cultural_navigation_papb.data.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Service untuk berinteraksi dengan Firebase Firestore
 * Menangani operasi CRUD untuk reviews
 */
@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val TAG = "FirestoreService"
        private const val REVIEWS_COLLECTION = "reviews"
        private const val PLACES_COLLECTION = "places"
    }

    /**
     * Menambah review baru ke Firestore
     */
    suspend fun addReview(review: Review): Result<Unit> = suspendCancellableCoroutine { continuation ->
        try {
            val reviewData = mapOf(
                "id" to review.id,
                "placeId" to review.placeId,
                "userId" to review.userId,
                "userName" to review.userName,
                "userPhoto" to review.userPhoto,
                "rating" to review.rating,
                "comment" to review.comment,
                "photos" to review.photos,
                "timestamp" to review.timestamp,
                "helpfulCount" to review.helpfulCount
            )

            firestore.collection(REVIEWS_COLLECTION)
                .document(review.id)
                .set(reviewData)
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Review added successfully: ${review.id}")
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error adding review: ${exception.message}", exception)
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in addReview: ${e.message}", e)
            continuation.resumeWithException(e)
        }
    }

    /**
     * Mengambil semua reviews untuk tempat tertentu
     */
    suspend fun getReviewsForPlace(placeId: String): Result<List<Review>> = suspendCancellableCoroutine { continuation ->
        try {
            firestore.collection(REVIEWS_COLLECTION)
                .whereEqualTo("placeId", placeId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    try {
                        val reviews = querySnapshot.documents.mapNotNull { document ->
                            try {
                                Review(
                                    id = document.getString("id") ?: return@mapNotNull null,
                                    placeId = document.getString("placeId") ?: "",
                                    userId = document.getString("userId") ?: "",
                                    userName = document.getString("userName") ?: "",
                                    userPhoto = document.getString("userPhoto") ?: "",
                                    rating = document.getDouble("rating")?.toFloat() ?: 0f,
                                    comment = document.getString("comment") ?: "",
                                    photos = (document.get("photos") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                                    timestamp = document.getLong("timestamp") ?: System.currentTimeMillis(),
                                    helpfulCount = document.getLong("helpfulCount")?.toInt() ?: 0
                                )
                            } catch (e: Exception) {
                                Log.w(TAG, "Error parsing review document: ${e.message}")
                                null
                            }
                        }
                        Log.d(TAG, "✅ Retrieved ${reviews.size} reviews for place: $placeId")
                        continuation.resume(Result.success(reviews))
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error parsing reviews: ${e.message}", e)
                        continuation.resumeWithException(e)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error getting reviews: ${exception.message}", exception)
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in getReviewsForPlace: ${e.message}", e)
            continuation.resumeWithException(e)
        }
    }

    /**
     * Mengambil reviews dari user tertentu
     */
    suspend fun getUserReviews(userId: String): Result<List<Review>> = suspendCancellableCoroutine { continuation ->
        try {
            firestore.collection(REVIEWS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    try {
                        val reviews = querySnapshot.documents.mapNotNull { document ->
                            try {
                                Review(
                                    id = document.getString("id") ?: return@mapNotNull null,
                                    placeId = document.getString("placeId") ?: "",
                                    userId = document.getString("userId") ?: "",
                                    userName = document.getString("userName") ?: "",
                                    userPhoto = document.getString("userPhoto") ?: "",
                                    rating = document.getDouble("rating")?.toFloat() ?: 0f,
                                    comment = document.getString("comment") ?: "",
                                    photos = (document.get("photos") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                                    timestamp = document.getLong("timestamp") ?: System.currentTimeMillis(),
                                    helpfulCount = document.getLong("helpfulCount")?.toInt() ?: 0
                                )
                            } catch (e: Exception) {
                                Log.w(TAG, "Error parsing review document: ${e.message}")
                                null
                            }
                        }
                        Log.d(TAG, "✅ Retrieved ${reviews.size} reviews for user: $userId")
                        continuation.resume(Result.success(reviews))
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error parsing user reviews: ${e.message}", e)
                        continuation.resumeWithException(e)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error getting user reviews: ${exception.message}", exception)
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in getUserReviews: ${e.message}", e)
            continuation.resumeWithException(e)
        }
    }

    /**
     * Update review
     */
    suspend fun updateReview(review: Review): Result<Unit> = suspendCancellableCoroutine { continuation ->
        try {
            val reviewData = mapOf(
                "rating" to review.rating,
                "comment" to review.comment,
                "photos" to review.photos,
                "timestamp" to review.timestamp
            )

            firestore.collection(REVIEWS_COLLECTION)
                .document(review.id)
                .update(reviewData)
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Review updated successfully: ${review.id}")
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error updating review: ${exception.message}", exception)
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in updateReview: ${e.message}", e)
            continuation.resumeWithException(e)
        }
    }

    /**
     * Delete review
     */
    suspend fun deleteReview(reviewId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        try {
            firestore.collection(REVIEWS_COLLECTION)
                .document(reviewId)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Review deleted successfully: $reviewId")
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error deleting review: ${exception.message}", exception)
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in deleteReview: ${e.message}", e)
            continuation.resumeWithException(e)
        }
    }

    /**
     * Increment helpful count untuk review
     */
    suspend fun incrementHelpfulCount(reviewId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        try {
            firestore.collection(REVIEWS_COLLECTION)
                .document(reviewId)
                .update("helpfulCount", com.google.firebase.firestore.FieldValue.increment(1L))
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Helpful count incremented: $reviewId")
                    continuation.resume(Result.success(Unit))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error incrementing helpful count: ${exception.message}", exception)
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in incrementHelpfulCount: ${e.message}", e)
            continuation.resumeWithException(e)
        }
    }

    /**
     * Get average rating untuk tempat tertentu
     */
    suspend fun getAverageRatingForPlace(placeId: String): Result<Float> = suspendCancellableCoroutine { continuation ->
        try {
            firestore.collection(REVIEWS_COLLECTION)
                .whereEqualTo("placeId", placeId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    try {
                        val reviews = querySnapshot.documents.mapNotNull { document ->
                            document.getDouble("rating")?.toFloat()
                        }
                        val avgRating = if (reviews.isNotEmpty()) {
                            reviews.average().toFloat()
                        } else {
                            0f
                        }
                        Log.d(TAG, "✅ Average rating for place $placeId: $avgRating")
                        continuation.resume(Result.success(avgRating))
                    } catch (e: Exception) {
                        Log.e(TAG, "❌ Error calculating average rating: ${e.message}", e)
                        continuation.resumeWithException(e)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error getting average rating: ${exception.message}", exception)
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in getAverageRatingForPlace: ${e.message}", e)
            continuation.resumeWithException(e)
        }
    }

    /**
     * Get review count untuk tempat tertentu
     */
    suspend fun getReviewCountForPlace(placeId: String): Result<Int> = suspendCancellableCoroutine { continuation ->
        try {
            firestore.collection(REVIEWS_COLLECTION)
                .whereEqualTo("placeId", placeId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val count = querySnapshot.size()
                    Log.d(TAG, "✅ Review count for place $placeId: $count")
                    continuation.resume(Result.success(count))
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "❌ Error getting review count: ${exception.message}", exception)
                    continuation.resumeWithException(exception)
                }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception in getReviewCountForPlace: ${e.message}", e)
            continuation.resumeWithException(e)
        }
    }
}

