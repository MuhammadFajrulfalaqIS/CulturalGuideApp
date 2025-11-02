// File: ui/screens/MapScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import androidx.compose.material.icons.filled.Search

@Composable
fun MapScreen(
    onNavigateToDetail: (placeId: String) -> Unit
) {
    // Koordinat Candi Prambanan (Placeholder)
    val prambanan = LatLng(-7.7520, 110.4891)

    // Inisialisasi posisi kamera peta
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(prambanan, 15f) // Zoom 15 ideal untuk area candi
    }

    Scaffold(
        topBar = { MapTopBar() }, // Header dan Tombol Kembali/Search
        bottomBar = { MapBottomNavBar() } // Bottom Nav (Home/Profile)
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
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                // Marker Utama Candi Prambanan
                Marker(
                    state = rememberMarkerState(position = prambanan),
                    title = "Candi Prambanan",
                    snippet = "Kompleks candi Hindu terbesar di Indonesia.",
                    onClick = {
                        // Ketika marker diklik, navigasi ke Detail Candi Siwa (placeholder ID)
                        onNavigateToDetail("candi_siwa")
                        true // Mengonsumsi event klik
                    }
                )

                // Placeholder Marker Lokasi Lain (misalnya Candi Sewu)
                Marker(
                    state = rememberMarkerState(position = LatLng(-7.7562, 110.4901)),
                    title = "Candi Sewu",
                    snippet = "Kompleks candi Buddha di dekat Prambanan."
                )
            }

            // Tambahkan Search Bar dan Fungsi Navigasi Cepat di atas peta (PRD: butuh search function)
            SearchBarMap(
                onSearch = { query -> /* Lakukan pencarian */ },
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
fun SearchBarMap(onSearch: (String) -> Unit, modifier: Modifier = Modifier) {
    // Implementasi Search Bar sederhana
    OutlinedTextField(
        value = "", // State input akan ditambahkan di ViewModel nanti
        onValueChange = { /* Update state */ },
        label = { Text("Cari Lokasi Tujuan (misal: Candi Plaosan)") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
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