// File: ui/screens/ListScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
// --- Impor untuk Preview ---
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme

@Composable
fun ListScreen(
    // ⭐ Tambahkan parameter detail
    onNavigateToDetail: (placeId: String) -> Unit
) {
    // Kita buat lebih baik dari sekadar Text
    Scaffold(
        topBar = {
            // (Nanti bisa diganti TopAppBar sungguhan)
            Text(text = "Katalog Candi")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Layar List - Siap Ditambahkan Katalog Candi")
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ListScreenPreview() {
    CulturalnavigationpapbTheme {
        ListScreen(
            onNavigateToDetail = {}
        )
    }
}