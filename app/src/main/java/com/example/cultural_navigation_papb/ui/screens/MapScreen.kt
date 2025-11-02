// File: ui/screens/MapScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cultural_navigation_papb.data.viewmodels.MapViewModel
import android.Manifest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.maps.android.compose.MapProperties


@OptIn(ExperimentalPermissionsApi::class) // Diperlukan untuk Accompanist
@Composable
fun MapScreen(
    onNavigateToDetail: (placeId: String) -> Unit,
    viewModel: MapViewModel = hiltViewModel<MapViewModel>()
) {
    // 1. KONSUMSI STATE DARI VIEWMODEL (TETAP SAMA)
    val searchText by viewModel.searchText.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    // ⭐ PERUBAHAN A: Dapatkan Status Izin Lokasi
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // 2. Koordinat dan Camera State (TETAP SAMA)
    val prambanan = LatLng(-7.7520, 110.4891)
    val initialLocation = userLocation ?: prambanan

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 15f)
    }

    // ⭐ PERUBAHAN B: Efek Samping untuk Meminta Izin dan Memulai Location Updates
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (!locationPermissionState.status.isGranted) {
            // Minta izin jika belum diberikan saat Composable pertama kali muncul
            locationPermissionState.launchPermissionRequest()
        }

        if (locationPermissionState.status.isGranted) {
            // Panggil ViewModel untuk memulai pembaruan lokasi (Hanya jika izin diberikan)
            viewModel.startLocationUpdates()
        }
    }

    // ⭐ PERUBAHAN C: Konfigurasi MapProperties
    // Aktifkan My Location Layer (Titik Biru) HANYA jika izin sudah diberikan
    val mapProperties = MapProperties(
        isMyLocationEnabled = locationPermissionState.status.isGranted
    )

    Scaffold(
        topBar = { MapTopBar() },
        bottomBar = { MapBottomNavBar() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Komponen Peta Utama
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false),
                // ⭐ MASUKKAN PROPERTI BARU DI SINI
                properties = mapProperties
            ) {
                // ... Marker Utama Candi Prambanan
                // ... Marker Candi Sewu
            }

            // ... ZoomControls (TETAP SAMA)
            ZoomControls(
                onZoomIn = {
                    val newZoom = (cameraPositionState.position.zoom + 1f).coerceAtMost(20f)
                    viewModel.onZoomChange(newZoom, cameraPositionState)
                },
                onZoomOut = {
                    val newZoom = (cameraPositionState.position.zoom - 1f).coerceAtLeast(5f)
                    viewModel.onZoomChange(newZoom, cameraPositionState)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 120.dp, end = 16.dp)
            )

            // ... SearchBarMap (TETAP SAMA)
            SearchBarMap(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                onSearch = viewModel::searchLocation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

// Komponen TopBar dan Search Bar Sederhana
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTopBar() {
    TopAppBar(
        title = { Text("Eksplorasi Lokasi") }
        // Di sini nanti akan ditambahkan Tombol Kembali dan Search Input
    )
}

@Composable
fun SearchBarMap(
    // ⭐ Tambahkan state (value) dan fungsi update (onValueChange)
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value, // ⭐ Menggunakan state yang dilewatkan
        onValueChange = onValueChange, // ⭐ Memperbarui state saat diketik
        label = { Text("Cari Lokasi Tujuan (misal: Candi Plaosan)") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            // Tombol Search (opsional, bisa juga via keyboard)
            IconButton(onClick = { onSearch(value) }) {
                Icon(Icons.Default.Search, contentDescription = "Lakukan Pencarian")
            }
        },
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
    )
}

// Komponen Bottom Navigation Bar (Placeholder)
@Composable
fun MapBottomNavBar() {
    // Placeholder untuk Bottom Navigation (Home/Profile)
    // Implementasi riil akan di buat di Scaffold Utama untuk konsistensi
    BottomAppBar {
        Text(text = "Bottom Nav (Home | Profile) - Dibuat Konsisten")
    }
}

@Composable
fun ZoomControls(onZoomIn: () -> Unit, onZoomOut: () -> Unit, modifier: Modifier = Modifier) {
    //
    Column(modifier = modifier) {
        FloatingActionButton(
            onClick = onZoomIn,
            modifier = Modifier.size(40.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Icon(Icons.Default.Add, contentDescription = "Zoom In")
        }
        Spacer(Modifier.height(8.dp))
        FloatingActionButton(
            onClick = onZoomOut,
            modifier = Modifier.size(40.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
        }
    }
}