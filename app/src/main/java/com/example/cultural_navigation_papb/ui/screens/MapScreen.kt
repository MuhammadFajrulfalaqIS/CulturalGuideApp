// File: ui/screens/MapScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text

@Composable
fun MapScreen(
    // ⭐ Tambahkan parameter detail
    onNavigateToDetail: (placeId: String) -> Unit
) {
    Text(text = "Layar Map - Siap Ditambahkan Google Maps SDK")
}