package com.example.cultural_navigation_papb.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cultural_navigation_papb.data.models.Review
import com.example.cultural_navigation_papb.data.repository.PlaceRepository
import com.example.cultural_navigation_papb.data.models.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * ViewModel untuk mengelola review dan rating
 */
@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    // State untuk review form
    private val _rating = MutableStateFlow(0f)
    val rating: StateFlow<Float> = _rating.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    private val _photos = MutableStateFlow<List<String>>(emptyList())
    val photos: StateFlow<List<String>> = _photos.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _submitError = MutableStateFlow<String?>(null)
    val submitError: StateFlow<String?> = _submitError.asStateFlow()

    // State untuk review list
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    private val _currentPlace = MutableStateFlow<Place?>(null)
    val currentPlace: StateFlow<Place?> = _currentPlace.asStateFlow()

    /**
     * Load reviews untuk tempat tertentu
     */
    fun loadReviewsForPlace(placeId: String) {
        viewModelScope.launch {
            try {
                placeRepository.getReviewsForPlace(placeId).collect { reviewList ->
                    _reviews.value = reviewList
                }

                // Load place info
                placeRepository.getPlaceById(placeId)?.let { place ->
                    _currentPlace.value = place
                }
            } catch (e: Exception) {
                _submitError.value = "Gagal memuat review: ${e.message}"
            }
        }
    }

    /**
     * Update rating
     */
    fun updateRating(newRating: Float) {
        _rating.value = newRating
    }

    /**
     * Update comment
     */
    fun updateComment(newComment: String) {
        _comment.value = newComment
    }

    /**
     * Update photos
     */
    fun updatePhotos(newPhotos: List<String>) {
        _photos.value = newPhotos
    }

    /**
     * Add photo
     */
    fun addPhoto(photoUrl: String) {
        _photos.value = _photos.value + photoUrl
    }

    /**
     * Remove photo
     */
    fun removePhoto(photoUrl: String) {
        _photos.value = _photos.value.filter { it != photoUrl }
    }

    /**
     * Submit review baru
     */
    fun submitReview(
        review: Review,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (review.rating == 0f) {
            onError("Silakan berikan rating")
            return
        }

        if (review.comment.isBlank()) {
            onError("Silakan tulis komentar")
            return
        }

        _isSubmitting.value = true
        _submitError.value = null

        viewModelScope.launch {
            try {
                placeRepository.addReview(review)

                // Reset form
                _rating.value = 0f
                _comment.value = ""
                _photos.value = emptyList()

                onSuccess()

            } catch (e: Exception) {
                _submitError.value = "Gagal submit review: ${e.message}"
                onError(e.message ?: "Gagal submit review")
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    /**
     * Submit review baru (legacy method)
     */
    fun submitReview(userId: String, userName: String) {
        val currentPlace = _currentPlace.value ?: run {
            _submitError.value = "Tidak ada tempat yang dipilih"
            return
        }

        if (_rating.value == 0f) {
            _submitError.value = "Silakan berikan rating"
            return
        }

        if (_comment.value.isBlank()) {
            _submitError.value = "Silakan tulis komentar"
            return
        }

        _isSubmitting.value = true
        _submitError.value = null

        viewModelScope.launch {
            try {
                val review = Review(
                    id = UUID.randomUUID().toString(),
                    placeId = currentPlace.id,
                    userId = userId,
                    userName = userName,
                    rating = _rating.value,
                    comment = _comment.value,
                    photos = _photos.value
                )

                placeRepository.addReview(review)

                // Reset form
                _rating.value = 0f
                _comment.value = ""
                _photos.value = emptyList()

            } catch (e: Exception) {
                _submitError.value = "Gagal submit review: ${e.message}"
            } finally {
                _isSubmitting.value = false
            }
        }
    }

    /**
     * Mark review sebagai helpful
     */
    fun markReviewHelpful(reviewId: String) {
        viewModelScope.launch {
            try {
                placeRepository.markReviewHelpful(reviewId)
            } catch (e: Exception) {
                _submitError.value = "Gagal menandai review helpful"
            }
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _submitError.value = null
    }

    /**
     * Reset form
     */
    fun resetForm() {
        _rating.value = 0f
        _comment.value = ""
        _photos.value = emptyList()
        _submitError.value = null
        _isSubmitting.value = false
    }

    /**
     * Get average rating dari reviews yang dimuat
     */
    fun getAverageRating(): Float {
        val reviewList = _reviews.value
        return if (reviewList.isNotEmpty()) {
            reviewList.map { it.rating }.average().toFloat()
        } else {
            0f
        }
    }

    /**
     * Get rating distribution
     */
    fun getRatingDistribution(): Map<Int, Int> {
        val reviewList = _reviews.value
        val distribution = mutableMapOf<Int, Int>()

        for (i in 1..5) {
            distribution[i] = reviewList.count { it.rating >= i && it.rating < i + 1 }
        }

        return distribution
    }
}