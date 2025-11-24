package com.example.cultural_navigation_papb.data.viewmodels

import androidx.lifecycle.ViewModel
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.models.allPrambananPlaces
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * ViewModel untuk menampilkan data candi-candi Prambanan
 * Hanya READ-ONLY (untuk user melihat data)
 */
@HiltViewModel
class PlaceViewModel @Inject constructor() : ViewModel() {

    // State untuk menyimpan list places
    private val _places = MutableStateFlow<List<Place>>(allPrambananPlaces)
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    // State untuk loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Helper function to find a place by its ID
     */
    fun getPlaceById(placeId: String): Place? {
        return _places.value.find { it.id == placeId }
    }
}
