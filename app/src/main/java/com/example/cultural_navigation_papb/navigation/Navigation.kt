package com.example.cultural_navigation_papb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cultural_navigation_papb.ui.screens.*

object Destinations {
    // Rute Aplikasi
    const val ONBOARDING = "onboarding"
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"

    const val HOME = "home"
    const val MAP = "map"
    const val LIST = "list"
    const val DETAIL = "detail/{placeId}"

    const val PROFILE = "profile"
    const val INBOX = "inbox" // Rute baru untuk Inbox Offline
}

@Composable
fun CultureGuideNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.ONBOARDING // Mulai dari Onboarding
    ) {
        // --- 1. Onboarding (Layar Sambutan) ---
        composable(Destinations.ONBOARDING) {
            OnboardingScreen(
                onFinishOnboarding = {
                    // Setelah selesai onboarding, masuk ke Sign In
                    // dan hapus onboarding dari riwayat (biar tidak bisa kembali)
                    navController.navigate(Destinations.SIGN_IN) {
                        popUpTo(Destinations.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // --- 2. Sign In (Login) ---
        composable(Destinations.SIGN_IN) {
            SignInScreen(
                onSignInSuccess = {
                    // Jika login sukses, masuk ke Home
                    // dan hapus halaman login dari riwayat
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.SIGN_IN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Destinations.SIGN_UP)
                }
            )
        }

        // --- 3. Sign Up (Daftar) ---
        composable(Destinations.SIGN_UP) {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.popBackStack() // Kembali ke layar login
                }
            )
        }

        // --- 4. Home (Beranda) ---
        composable(Destinations.HOME) {
            HomeScreen(
                onNavigateToMap = { navController.navigate(Destinations.MAP) },
                onNavigateToList = { navController.navigate(Destinations.LIST) },
                onNavigateToProfile = { navController.navigate(Destinations.PROFILE) }
            )
        }

        // --- 5. Profile & Inbox ---
        composable(Destinations.PROFILE) {
            ProfileScreen(
                onSignOutSuccess = {
                    // Jika logout, kembali ke Sign In dan bersihkan semua riwayat
                    navController.navigate(Destinations.SIGN_IN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToInbox = {
                    // Navigasi ke halaman Inbox Offline
                    navController.navigate(Destinations.INBOX)
                }
            )
        }

        composable(Destinations.INBOX) {
            InboxScreen(
                onNavigateBack = { navController.navigateUp() },
                // Tambahkan aksi ini: Pergi ke DetailScreen membawa ID
                onNavigateToDetail = { placeId ->
                    navController.navigate("detail/$placeId")
                }
            )
        }

        // --- 6. Fitur Utama (Map, List, Detail) ---
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