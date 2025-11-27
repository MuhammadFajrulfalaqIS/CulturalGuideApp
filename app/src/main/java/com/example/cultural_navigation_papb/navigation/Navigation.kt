package com.example.cultural_navigation_papb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cultural_navigation_papb.ui.screens.*

object Destinations {
    // Tambahkan rute baru untuk autentikasi
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"

    const val HOME = "home"
    const val MAP = "map"
    const val LIST = "list"
    const val PROFILE = "profile"
    const val DETAIL = "detail/{placeId}"
}

@Composable
fun CultureGuideNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // UBAH startDestination menjadi SIGN_IN
        startDestination = Destinations.SIGN_IN
    ) {
        // --- 1. Halaman Sign In (Awal) ---
        composable(Destinations.SIGN_IN) {
            SignInScreen(
                onSignInSuccess = {
                    // Jika login berhasil, pindah ke Home
                    // popUpTo(Destinations.SIGN_IN) { inclusive = true }
                    // artinya: Hapus halaman login dari riwayat back stack
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.SIGN_IN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Destinations.SIGN_UP)
                }
            )
        }

        // --- 2. Halaman Sign Up ---
        composable(Destinations.SIGN_UP) {
            SignUpScreen(
                onNavigateToSignIn = {
                    // Kembali ke login (pop screen sign up)
                    navController.popBackStack()
                }
            )
        }

        // --- 3. Halaman Home ---
        composable(Destinations.HOME) {
            HomeScreen(
                onNavigateToMap = { navController.navigate(Destinations.MAP) },
                onNavigateToList = { navController.navigate(Destinations.LIST) },
                onNavigateToProfile = { navController.navigate(Destinations.PROFILE) }
            )
        }

        // --- 4. Halaman Profile ---
        composable(Destinations.PROFILE) {
            ProfileScreen(
                onSignOutSuccess = {
                    // Jika logout, kembali ke Sign In dan hapus semua riwayat
                    navController.navigate(Destinations.SIGN_IN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // --- Halaman Lainnya (Map, List, Detail) ---
        composable(Destinations.MAP) {
            MapScreen(
                onNavigateToDetail = { placeId -> navController.navigate("detail/$placeId") }
            )
        }

        composable(Destinations.LIST) {
            ListScreen(
                onNavigateToDetail = { placeId -> navController.navigate("detail/$placeId") }
            )
        }

        composable(
            route = Destinations.DETAIL,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId")
            if (placeId != null) {
                DetailScreen(
                    placeId = placeId,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}