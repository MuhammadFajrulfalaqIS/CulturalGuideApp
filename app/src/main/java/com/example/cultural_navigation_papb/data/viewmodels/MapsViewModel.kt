package com.example.cultural_navigation_papb.data.viewmodels

// File: data/viewmodels/MapViewModel.kt
import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.maps.android.compose.CameraPositionState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import dagger.hilt.android.qualifiers.ApplicationContext
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.repository.PlaceRepository

// HiltViewModel menandai kelas ini agar Hilt dapat menyuntikkan dependensinya
@HiltViewModel
class MapsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val placeRepository: PlaceRepository
) : ViewModel() {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Perbarui lokasi user dengan fungsi baru
                updateUserLocation(LatLng(location.latitude, location.longitude))
                println("Lokasi Realtime Diterima: ${location.latitude}, ${location.longitude}")
            }
        }
    }
    // ⭐ 1. STATE UNTUK SEARCH BAR
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // ⭐ 2. STATE UNTUK POSISI PENGGUNA
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

    // ⭐ 3. STATE UNTUK LIST TEMPAT (dari database)
    private val _nearbyPlaces = MutableStateFlow<List<Place>>(emptyList())
    val nearbyPlaces: StateFlow<List<Place>> = _nearbyPlaces.asStateFlow()

    // ⭐ 4. STATE UNTUK PATH YANG DIREKOMENDASIKAN
    private val _recommendedPath = MutableStateFlow<List<Place>>(emptyList())
    val recommendedPath: StateFlow<List<Place>> = _recommendedPath.asStateFlow()

    // ⭐ 5. STATE UNTUK SELECTED PLACE (yang dipilih user)
    private val _selectedPlace = MutableStateFlow<Place?>(null)
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()

    // ⭐ 6. STATE UNTUK SHOW/HIDE PATH
    private val _showPath = MutableStateFlow(false)
    val showPath: StateFlow<Boolean> = _showPath.asStateFlow()

    // ⭐ 7. STATE UNTUK SELECTED ROUTE
    private val _selectedRoute = MutableStateFlow<List<LatLng>>(emptyList())
    val selectedRoute: StateFlow<List<LatLng>> = _selectedRoute.asStateFlow()

    fun onZoomChange(newZoom: Float, cameraPositionState: CameraPositionState) {
        // Luncurkan Coroutine untuk melakukan animasi secara non-blocking
        viewModelScope.launch {
            cameraPositionState.animate(
                update = CameraUpdateFactory.zoomTo(newZoom),
                durationMs = 500 // Durasi animasi 0.5 detik
            )
        }
    }
    // Fungsi untuk memperbarui Search Text (Event/Action)
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    // Fungsi untuk memicu Logika Bisnis (Action)
    fun searchLocation(query: String) {
        viewModelScope.launch {
            try {
                val searchResults = placeRepository.searchPlaces(query)
                _nearbyPlaces.value = searchResults.take(10) // Batasi hasil pencarian
            } catch (e: Exception) {
                // Handle error
                _nearbyPlaces.value = emptyList()
            }
        }
    }

    // ⭐ FUNGSI BARU UNTUK DATABASE INTEGRATION

    // Inisialisasi database dan load data
    fun initializeApp() {
        viewModelScope.launch {
            try {
                // Inisialisasi database dengan data candi
                placeRepository.initializeDatabase()

                // Load tempat terdekat dari database
                loadNearbyPlaces()
            } catch (e: Exception) {
                println("Error initializing app: ${e.message}")
            }
        }
    }

    // Load tempat terdekat dari lokasi user saat ini
    fun loadNearbyPlaces() {
        viewModelScope.launch {
            _userLocation.value?.let { location ->
                try {
                    val nearby = placeRepository.getNearbyPlaces(location, 15)
                    _nearbyPlaces.value = nearby
                } catch (e: Exception) {
                    println("Error loading nearby places: ${e.message}")
                    _nearbyPlaces.value = emptyList()
                }
            }
        }
    }

    // Set recommended path untuk tour
    fun setRecommendedPath(duration: String = "pendek") {
        viewModelScope.launch {
            _userLocation.value?.let { location ->
                try {
                    val path = placeRepository.getRecommendedPath(location, duration)
                    _recommendedPath.value = path
                    _showPath.value = true
                } catch (e: Exception) {
                    println("Error setting recommended path: ${e.message}")
                    _recommendedPath.value = emptyList()
                }
            }
        }
    }

    // Toggle show/hide path
    fun togglePathVisibility() {
        _showPath.value = !_showPath.value
    }

    // Select place untuk detail
    fun selectPlace(place: Place) {
        _selectedPlace.value = place
    }

    // Clear selected place
    fun clearSelectedPlace() {
        _selectedPlace.value = null
    }

    // Get semua tempat (untuk debugging atau admin)
    fun loadAllPlaces() {
        viewModelScope.launch {
            try {
                placeRepository.getAvailablePlaces().collect { places ->
                    _nearbyPlaces.value = places
                }
            } catch (e: Exception) {
                println("Error loading all places: ${e.message}")
                _nearbyPlaces.value = emptyList()
            }
        }
    }

    // Update lokasi user (dipanggil saat ada update lokasi)
    private fun updateUserLocation(location: LatLng) {
        val oldLocation = _userLocation.value
        _userLocation.value = location

        // Load nearby places jika lokasi berubah signifikan
        if (oldLocation == null || calculateDistance(oldLocation, location) > 100) { // 100 meter
            loadNearbyPlaces()
        }
    }

    // Helper untuk menghitung jarak
    private fun calculateDistance(from: LatLng, to: LatLng): Float {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            from.latitude, from.longitude,
            to.latitude, to.longitude,
            results
        )
        return results[0]
    }

    // Implementasi inisialisasi
    init {
        // Set lokasi default Prambanan
        _userLocation.value = LatLng(-7.7520, 110.4891)

        // Inisialisasi aplikasi
        initializeApp()
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000L
        )
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000L)
            .build()

        // Meminta update lokasi dari klien
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // ⭐ ROUTING FUNCTIONS
    fun generateRouteToPlace(userLocation: LatLng, destination: Place) {
        viewModelScope.launch {
            try {
                // Generate realistic walking path within Prambanan complex
                val route = generateRealisticWalkingPath(userLocation, destination.getLatLng())
                _selectedRoute.value = route
                _showPath.value = true
            } catch (e: Exception) {
                println("Error generating route: ${e.message}")
                // Fallback to straight line if path generation fails
                _selectedRoute.value = listOf(userLocation, destination.getLatLng())
                _showPath.value = true
            }
        }
    }

    // Generate realistic walking path with curves and intermediate points
    private fun generateRealisticWalkingPath(start: LatLng, end: LatLng): List<LatLng> {
        val pathPoints = mutableListOf<LatLng>()

        // Add starting point
        pathPoints.add(start)

        // Calculate intermediate points for a natural walking path
        val midPoint = LatLng(
            (start.latitude + end.latitude) / 2,
            (start.longitude + end.longitude) / 2
        )

        // Add curve to simulate walking paths around temple structures
        val distance = calculateDistance(start, end)

        if (distance > 50) { // Only add curves for longer distances
            // Create a curved path with 2-3 intermediate points
            val curveOffset = distance * 0.0001f // Adjust curve intensity based on distance

            // First curve point (slightly offset from direct path)
            val curve1 = LatLng(
                midPoint.latitude + curveOffset * 0.5f,
                midPoint.longitude - curveOffset * 0.3f
            )
            pathPoints.add(curve1)

            // Second curve point for longer distances
            if (distance > 100) {
                val curve2 = LatLng(
                    midPoint.latitude - curveOffset * 0.3f,
                    midPoint.longitude + curveOffset * 0.4f
                )
                pathPoints.add(curve2)
            }

            // Approach point near destination
            val approachPoint = LatLng(
                (end.latitude + midPoint.latitude) / 2,
                (end.longitude + midPoint.longitude) / 2
            )
            pathPoints.add(approachPoint)
        } else {
            // For short distances, just add one intermediate point
            pathPoints.add(midPoint)
        }

        // Add final destination point
        pathPoints.add(end)

        return pathPoints
    }

    fun clearRoute() {
        _selectedRoute.value = emptyList()
        _showPath.value = false
    }

    // --- ONCLEARED (KODE LENGKAP) ---
// Dipanggil saat ViewModel dihancurkan (misalnya, saat MapScreen tidak lagi di layar)
    override fun onCleared() {
        super.onCleared()
        // Menghentikan permintaan update lokasi untuk menghemat baterai
        fusedLocationClient.removeLocationUpdates(locationCallback)
        println("Location updates dihentikan saat ViewModel di-clear.")
    }
}


