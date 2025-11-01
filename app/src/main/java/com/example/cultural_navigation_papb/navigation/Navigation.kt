// File: com.example.cultural_navigation_papb/Navigation.kt

package com.example.cultural_navigation_papb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cultural_navigation_papb.ui.screens.*

// Definisikan rute/destinasi aplikasi agar tidak ada hardcoding string
object Destinations {
    const val HOME = "home"
    const val MAP = "map"
    const val LIST = "list"
    const val PROFILE = "profile"
    const val DETAIL = "detail/{placeId}"
}

@Composable
fun CultureGuideNavHost() {
    // rememberNavController mempertahankan state NavController di seluruh siklus hidup Composable
    val navController = rememberNavController()

    // 

    NavHost(
        navController = navController,
        startDestination = Destinations.HOME
    ) {
        // Slide 1: Home
        composable(Destinations.HOME) {
            // Memberikan lambda untuk fungsi navigasi
            HomeScreen(
                onNavigateToMap = { navController.navigate(Destinations.MAP) },
                onNavigateToList = { navController.navigate(Destinations.LIST) },
                onNavigateToProfile = { navController.navigate(Destinations.PROFILE) }
            )
        }

        // Slide 2: Map/Eksplorasi Lokasi
        composable(Destinations.MAP) {
            MapScreen(
                onNavigateToDetail = { placeId -> navController.navigate("detail/$placeId") }
                // Navigasi Bottom Bar/Kembali akan kita tangani di Composable Frame selanjutnya
            )
        }

        // Slide 3: Katalog dan Daftar
        composable(Destinations.LIST) {
            ListScreen(
                onNavigateToDetail = { placeId -> navController.navigate("detail/$placeId") }
            )
        }

        // Target navigasi lain (Profile dan Detail)
        composable(Destinations.PROFILE) { ProfileScreen() }
        composable(Destinations.DETAIL) { DetailScreen() }
    }
}