package com.example.cultural_navigation_papb.data.viewmodels

// File: data/viewmodels/MapViewModel.kt
import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.lifecycle.viewModelScope // Import untuk Coroutine
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.maps.android.compose.CameraPositionState // Import state kamera
import com.google.android.gms.maps.CameraUpdateFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch

// HiltViewModel menandai kelas ini agar Hilt dapat menyuntikkan dependensinya
@HiltViewModel
class MapViewModel @Inject constructor(
    @ApplicationContext private val context: Context
    // Kita akan menyuntikkan Repository di sini nanti, tapi untuk sekarang kita biarkan kosong
) : ViewModel() {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                // Perbarui StateFlow userLocation
                _userLocation.value = LatLng(location.latitude, location.longitude)
                println("Lokasi Realtime Diterima: ${location.latitude}, ${location.longitude}")
            }
        }
    }
    // ⭐ 1. STATE UNTUK SEARCH BAR
    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    // ⭐ 2. STATE UNTUK POSISI PENGGUNA (Placeholder)
    // Di masa depan, ini akan di-update oleh Location Service
    private val _userLocation = MutableStateFlow<LatLng?>(null)
    val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()

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
        // DI SINI ADALAH TEMPAT LOGIKA PENCARIAN & RETROFIT
        println("ViewModel melakukan pencarian untuk: $query")
        // Misalnya: viewModel.repository.findPlace(query)
    }

    // Implementasi placeholder untuk simulasi posisi
    init {
        // Simulasi inisialisasi lokasi pengguna (Anda bisa menggantinya nanti)
        _userLocation.value = LatLng(-7.7520, 110.4891) // Contoh: Prambanan
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

    // --- ONCLEARED (KODE LENGKAP) ---
// Dipanggil saat ViewModel dihancurkan (misalnya, saat MapScreen tidak lagi di layar)
    override fun onCleared() {
        super.onCleared()
        // Menghentikan permintaan update lokasi untuk menghemat baterai
        fusedLocationClient.removeLocationUpdates(locationCallback)
        println("Location updates dihentikan saat ViewModel di-clear.")
    }
}


