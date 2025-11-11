package com.example.cultural_navigation_papb.ui.screens

// --- Impor untuk Composable & Preview ---
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme


// Diubah dari 'class' menjadi 'Composable fun'
@Composable
fun ProfileScreen() {
    Scaffold(
        topBar = {
            Text(text = "Profil Pengguna")
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Ini adalah Halaman Profil")
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ProfileScreenPreview() {
    CulturalnavigationpapbTheme {
        ProfileScreen()
    }
}