// File: com.example.cultural_navigation_papb/MainActivity.kt

package com.example.cultural_navigation_papb


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme
import dagger.hilt.android.AndroidEntryPoint
import com.example.cultural_navigation_papb.navigation.CultureGuideNavHost


// AndroidEntryPoint memungkinkan injeksi dependensi ke MainActivity
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CulturalnavigationpapbTheme() {
                // Panggil composable NavHost utama di sini
                CultureGuideNavHost()
            }
        }
    }
}