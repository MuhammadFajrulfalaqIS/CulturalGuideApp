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
import kotlinx.coroutines.flow.combine
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
import com.example.cultural_navigation_papb.data.api.DirectionsApiService
import com.example.cultural_navigation_papb.data.api.DirectionResult

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

    // ✅ NEW: State untuk route info (distance & duration)
    private val _routeInfo = MutableStateFlow<RouteInfo?>(null)
    val routeInfo: StateFlow<RouteInfo?> = _routeInfo.asStateFlow()

    // ⭐ 8. ADDITIONAL STATE FOR GEOFENCING (for GeofenceMapScreen compatibility)
    private val _isGeofencingActive = MutableStateFlow(false)
    val isGeofencingActive: StateFlow<Boolean> = _isGeofencingActive.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Combined UI state for GeofenceMapScreen
    data class MapUiState(
        val places: List<Place> = emptyList(),
        val userLocation: LatLng? = null,
        val selectedPlace: Place? = null,
        val isLoading: Boolean = false,
        val isGeofencingActive: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    // Update UI state when individual states change
    init {
        // Set lokasi default Prambanan
        _userLocation.value = LatLng(-7.7520, 110.4891)

        // Inisialisasi aplikasi
        initializeApp()
    }

    // Manual update function to sync state changes
    private fun syncUiState() {
        _uiState.value = MapUiState(
            places = _nearbyPlaces.value,
            userLocation = _userLocation.value,
            selectedPlace = _selectedPlace.value,
            isLoading = _isLoading.value,
            isGeofencingActive = _isGeofencingActive.value,
            error = _error.value
        )
    }

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
                    // ✅ FIX: Perbesar radius ke 50 km untuk memastikan semua candi Prambanan termuat
                    // Candi Sewu dan Bubrah berjarak sekitar 800-1000 meter dari pusat
                    val nearby = placeRepository.getNearbyPlaces(location, 50)
                    _nearbyPlaces.value = nearby

                    // Log untuk debugging
                    android.util.Log.d("MapsViewModel", "📍 Loaded ${nearby.size} places within 50km radius")
                    nearby.forEach { place ->
                        android.util.Log.d("MapsViewModel", "  - ${place.name} (${place.category}, visited: ${place.isVisited})")
                    }

                    syncUiState()
                } catch (e: Exception) {
                    android.util.Log.e("MapsViewModel", "Error loading nearby places: ${e.message}")
                    _nearbyPlaces.value = emptyList()
                    syncUiState()
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

    // ✅ NEW: Directions API Service
    private val directionsApiService = DirectionsApiService()

    // ⭐ ROUTING FUNCTIONS
    /**
     * ✅ NEW: Generate route menggunakan REAL Google Directions API
     * Ini akan menghasilkan rute walking yang akurat sesuai jalan yang ada
     */
    fun generateRouteToPlace(userLocation: LatLng, destination: Place) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                android.util.Log.d("MapsViewModel", "🗺️ Requesting walking route from Directions API...")

                // ✅ Call REAL Directions API
                val result = directionsApiService.getWalkingDirections(userLocation, destination.getLatLng())

                when (result) {
                    is DirectionResult.Success -> {
                        _selectedRoute.value = result.path
                        _showPath.value = true
                        _routeInfo.value = RouteInfo(
                            distance = result.distance,
                            duration = result.duration,
                            steps = result.steps
                        )
                        android.util.Log.d("MapsViewModel", "✅ Route loaded: ${result.distance}, ${result.duration}, ${result.path.size} points")
                    }
                    is DirectionResult.Error -> {
                        android.util.Log.e("MapsViewModel", "❌ Directions API error: ${result.message}")
                        // Fallback to simple curved path
                        _selectedRoute.value = generateFallbackPath(userLocation, destination.getLatLng())
                        _showPath.value = true
                        _routeInfo.value = null
                        _error.value = "Tidak dapat memuat rute. Menampilkan jalur sederhana."
                    }
                }

                _isLoading.value = false
            } catch (e: Exception) {
                android.util.Log.e("MapsViewModel", "❌ Error generating route", e)
                // Fallback to simple curved path
                _selectedRoute.value = generateFallbackPath(userLocation, destination.getLatLng())
                _showPath.value = true
                _routeInfo.value = null
                _error.value = "Tidak dapat memuat rute: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    /**
     * Fallback path generator jika Directions API gagal
     * Membuat simple curved path
     */
    private fun generateFallbackPath(start: LatLng, end: LatLng): List<LatLng> {
        val pathPoints = mutableListOf<LatLng>()
        pathPoints.add(start)

        val midPoint = LatLng(
            (start.latitude + end.latitude) / 2,
            (start.longitude + end.longitude) / 2
        )
        pathPoints.add(midPoint)
        pathPoints.add(end)

        return pathPoints
    }

    fun clearRoute() {
        _selectedRoute.value = emptyList()
        _showPath.value = false
        _routeInfo.value = null
    }

    // Geofencing control methods
    fun setGeofencingActive(active: Boolean) {
        _isGeofencingActive.value = active
    }

    fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    fun setError(error: String?) {
        _error.value = error
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

/**
 * ✅ NEW: Data class untuk menyimpan info route (distance, duration, steps)
 */
data class RouteInfo(
    val distance: String,
    val duration: String,
    val steps: List<com.example.cultural_navigation_papb.data.api.DirectionStep>
)
