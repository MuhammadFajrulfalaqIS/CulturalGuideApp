package com.example.cultural_navigation_papb.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cultural_navigation_papb.ui.screens.*

object Destinations {
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
        startDestination = Destinations.HOME
    ) {
        // Slide 1: Home
        composable(Destinations.HOME) {
            HomeScreen(
                onNavigateToMap = { navController.navigate(Destinations.MAP) },
                onNavigateToList = { navController.navigate(Destinations.LIST) },
                onNavigateToProfile = { navController.navigate(Destinations.PROFILE) }
            )
        }

        // Slide 2: Map
        composable(Destinations.MAP) {
            MapScreen(
                onNavigateToDetail = { placeId -> navController.navigate("detail/$placeId") }
            )
        }

        // Slide 3: List
        composable(Destinations.LIST) {
            ListScreen(
                onNavigateToDetail = { placeId -> navController.navigate("detail/$placeId") }
            )
        }

        composable(Destinations.PROFILE) { ProfileScreen() }

        // Target navigasi Detail dengan Argument
        composable(
            route = Destinations.DETAIL,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            // Ambil ID dari argument navigasi
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