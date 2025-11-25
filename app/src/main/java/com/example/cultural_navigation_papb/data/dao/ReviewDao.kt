package com.example.cultural_navigation_papb.data.dao

import androidx.room.*
import com.example.cultural_navigation_papb.data.models.Review
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object untuk Review entity
 */
@Dao
interface ReviewDao {

    @Query("SELECT * FROM reviews WHERE placeId = :placeId ORDER BY timestamp DESC")
    fun getReviewsForPlace(placeId: String): Flow<List<Review>>

    @Query("SELECT * FROM reviews WHERE placeId = :placeId ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentReviewsForPlace(placeId: String, limit: Int = 5): List<Review>

    @Query("SELECT AVG(rating) FROM reviews WHERE placeId = :placeId")
    suspend fun getAverageRatingForPlace(placeId: String): Float?

    @Query("SELECT COUNT(*) FROM reviews WHERE placeId = :placeId")
    suspend fun getReviewCountForPlace(placeId: String): Int

    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY timestamp DESC")
    fun getUserReviews(userId: String): Flow<List<Review>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: Review)

    @Update
    suspend fun updateReview(review: Review)

    @Delete
    suspend fun deleteReview(review: Review)

    @Query("DELETE FROM reviews WHERE placeId = :placeId")
    suspend fun deleteReviewsForPlace(placeId: String)

    @Query("UPDATE reviews SET helpfulCount = helpfulCount + 1 WHERE id = :reviewId")
    suspend fun incrementHelpfulCount(reviewId: String)
}