package com.example.cultural_navigation_papb.data.viewmodels

import androidx.lifecycle.ViewModel
import com.example.cultural_navigation_papb.data.models.Place
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
    private val _places = MutableStateFlow<List<Place>>(getStaticPlaces())
    val places: StateFlow<List<Place>> = _places.asStateFlow()

    // State untuk loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Data statis candi-candi di kompleks Prambanan
     * Tidak perlu database karena data tidak berubah
     */
    private fun getStaticPlaces() = listOf(
        Place(
            id = "1",
            name = "Candi Siwa (Loro Jonggrang)",
            description = "Candi utama dan tertinggi (47m) yang didedikasikan untuk Dewa Siwa. Berisi relief kisah Ramayana yang sangat indah.",
            imageUrl = "https://example.com/siwa.jpg",
            latitude = -7.7520,
            longitude = 110.4891,
            category = "candi_utama"
        ),
        Place(
            id = "2",
            name = "Candi Wisnu",
            description = "Berada di sebelah utara Candi Siwa, didedikasikan untuk Dewa Wisnu sang pemelihara alam semesta.",
            imageUrl = "https://example.com/wisnu.jpg",
            latitude = -7.7515,
            longitude = 110.4896,
            category = "candi_utama"
        ),
        Place(
            id = "3",
            name = "Candi Brahma",
            description = "Berada di sebelah selatan Candi Siwa, didedikasikan untuk Dewa Brahma sang pencipta alam semesta.",
            imageUrl = "https://example.com/brahma.jpg",
            latitude = -7.7525,
            longitude = 110.4886,
            category = "candi_utama"
        ),
        Place(
            id = "4",
            name = "Candi Nandi",
            description = "Candi yang berisi arca Nandi, kendaraan Dewa Siwa. Terletak berhadapan dengan Candi Siwa.",
            imageUrl = "https://example.com/nandi.jpg",
            latitude = -7.7522,
            longitude = 110.4894,
            category = "candi_perwara"
        ),
        Place(
            id = "5",
            name = "Candi Angsa",
            description = "Candi yang berisi arca angsa, kendaraan Dewa Brahma. Terletak berhadapan dengan Candi Brahma.",
            imageUrl = "https://example.com/angsa.jpg",
            latitude = -7.7528,
            longitude = 110.4883,
            category = "candi_perwara"
        ),
        Place(
            id = "6",
            name = "Candi Garuda",
            description = "Candi yang berisi arca Garuda, kendaraan Dewa Wisnu. Terletak berhadapan dengan Candi Wisnu.",
            imageUrl = "https://example.com/garuda.jpg",
            latitude = -7.7512,
            longitude = 110.4899,
            category = "candi_perwara"
        ),
        Place(
            id = "7",
            name = "Candi Plaosan",
            description = "Kompleks candi Buddha kembar yang indah, terletak sekitar 1 km dari Prambanan. Dibangun pada abad ke-9.",
            imageUrl = "https://example.com/plaosan.jpg",
            latitude = -7.7428,
            longitude = 110.5021,
            category = "others"
        ),
        Place(
            id = "8",
            name = "Candi Sewu",
            description = "Kompleks candi Buddha terbesar kedua di Jawa Tengah setelah Borobudur. Memiliki 249 candi.",
            imageUrl = "https://example.com/sewu.jpg",
            latitude = -7.7447,
            longitude = 110.4987,
            category = "others"
        )
    )
}
