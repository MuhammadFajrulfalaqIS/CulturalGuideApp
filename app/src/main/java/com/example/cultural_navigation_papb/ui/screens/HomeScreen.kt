// File: ui/screens/HomeScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(

    onNavigateToMap: () -> Unit,
    onNavigateToList: () -> Unit,
    onNavigateToProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Tampilan placeholder:
    Text(text = "Layar Home - Siap Didesain!")

    // (Anda dapat menguji navigasi dengan menambahkan tombol sementara di sini)
    /* Button(onClick = onNavigateToMap) {
        Text("Go to Map")
    }
    */
}