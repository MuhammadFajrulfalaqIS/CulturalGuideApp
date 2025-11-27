package com.example.cultural_navigation_papb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cultural_navigation_papb.ui.screens.*

object Destinations {
    // Tambah rute baru
    const val ONBOARDING = "onboarding"

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
        // UBAH startDestination menjadi ONBOARDING
        startDestination = Destinations.ONBOARDING
    ) {
        // --- 0. Onboarding Screen ---
        composable(Destinations.ONBOARDING) {
            OnboardingScreen(
                onFinishOnboarding = {
                    // Pindah ke Sign In dan hapus Onboarding dari riwayat (biar ga bisa di-back)
                    navController.navigate(Destinations.SIGN_IN) {
                        popUpTo(Destinations.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // --- 1. Sign In Screen ---
        composable(Destinations.SIGN_IN) {
            SignInScreen(
                onSignInSuccess = {
                    navController.navigate(Destinations.HOME) {
                        popUpTo(Destinations.SIGN_IN) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Destinations.SIGN_UP)
                }
            )
        }

        // --- 2. Sign Up Screen ---
        composable(Destinations.SIGN_UP) {
            SignUpScreen(
                onNavigateToSignIn = {
                    navController.popBackStack()
                }
            )
        }

        // --- 3. Home Screen ---
        composable(Destinations.HOME) {
            HomeScreen(
                onNavigateToMap = { navController.navigate(Destinations.MAP) },
                onNavigateToList = { navController.navigate(Destinations.LIST) },
                onNavigateToProfile = { navController.navigate(Destinations.PROFILE) }
            )
        }

        // --- Halaman Lainnya ---
        composable(Destinations.PROFILE) {
            ProfileScreen(
                onSignOutSuccess = {
                    navController.navigate(Destinations.SIGN_IN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

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