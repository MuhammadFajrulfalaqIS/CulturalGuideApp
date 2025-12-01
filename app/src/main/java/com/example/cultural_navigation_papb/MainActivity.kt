// File: com.example.cultural_navigation_papb/MainActivity.kt

package com.example.cultural_navigation_papb

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cultural_navigation_papb.navigation.CultureGuideNavHost
import com.example.cultural_navigation_papb.navigation.Destinations
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme
import dagger.hilt.android.AndroidEntryPoint

// AndroidEntryPoint memungkinkan injeksi dependensi ke MainActivity
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "MainActivity onCreate with intent: $intent")

        setContent {
            CulturalnavigationpapbTheme {
                // Create navigation controller and handle deep links
                val navController = rememberNavController()

                // Handle deep links when activity starts
                LaunchedEffect(Unit) {
                    handleDeepLink(intent, navController)
                }

                CultureGuideNavHost(navController)
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        Log.d(TAG, "MainActivity onNewIntent: $intent")

        // Handle deep links when app is already running
        // Note: This would require exposing navController from CultureGuideNavHost
        // For now, the deep link handling will work when the app starts fresh
    }

    private fun handleDeepLink(intent: android.content.Intent, navController: NavHostController) {
        val uri = intent.data

        // ✅ Handle notification action from Geofence
        if (intent.action == "OPEN_REVIEW_DIALOG" || intent.action == "REVIEW_DESTINATION") {
            val placeId = intent.getStringExtra("placeId")
            val openReviewDialog = intent.getBooleanExtra("openReviewDialog", false)

            if (!placeId.isNullOrEmpty()) {
                Log.d(TAG, "📍 Navigating to detail screen for placeId: $placeId (openReviewDialog: $openReviewDialog)")

                // Navigate to detail screen
                // The review dialog will be opened automatically in DetailScreen if openReviewDialog is true
                navController.navigate("${Destinations.DETAIL.replace("{placeId}", placeId)}?openReviewDialog=$openReviewDialog")
                return
            }
        }

        // Handle URI deep links
        uri?.let { handleUriDeepLink(it, navController) }
    }

    private fun handleUriDeepLink(uri: Uri, navController: NavHostController) {
        Log.d(TAG, "Handling deep link URI: $uri")

        when {
            uri.scheme == "culturalguide" || uri.host == "culturalguide.app" -> {
                when {
                    uri.path?.startsWith("/review") == true -> {
                        val placeId = uri.lastPathSegment ?: ""
                        if (placeId.isNotEmpty()) {
                            Log.d(TAG, "Navigating to review for placeId: $placeId")
                            navController.navigate(Destinations.REVIEW.replace("{placeId}", placeId))
                        }
                    }
                    uri.path?.startsWith("/destination") == true -> {
                        val placeId = uri.lastPathSegment ?: ""
                        if (placeId.isNotEmpty()) {
                            Log.d(TAG, "Navigating to destination detail for placeId: $placeId")
                            navController.navigate(Destinations.DETAIL.replace("{placeId}", placeId))
                        }
                    }
                }
            }
        }
    }
}