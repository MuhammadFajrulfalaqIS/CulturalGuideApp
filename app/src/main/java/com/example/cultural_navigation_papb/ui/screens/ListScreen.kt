// File: ui/screens/ListScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text

@Composable
fun ListScreen(
    // ⭐ Tambahkan parameter detail
    onNavigateToDetail: (placeId: String) -> Unit
) {
    Text(text = "Layar List - Siap Ditambahkan Katalog Candi")
}