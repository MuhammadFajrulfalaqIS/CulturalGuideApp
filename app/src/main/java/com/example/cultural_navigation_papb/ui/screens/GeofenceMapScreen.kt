package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.viewmodels.MapsViewModel
import com.example.cultural_navigation_papb.geofence.GeofenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeofenceMapScreen(
    onNavigateToDetail: (placeId: String) -> Unit,
    onNavigateToReview: (placeId: String) -> Unit,
    onBackClick: () -> Unit = {},
    viewModel: MapsViewModel = hiltViewModel(),
    geofenceManager: GeofenceManager = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    // Prambanan center coordinates
    val prambananCenter = LatLng(-7.752008, 110.491825)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(prambananCenter, 15f)
    }

    // Initialize geofences when places are loaded
    LaunchedEffect(uiState.places) {
        if (uiState.places.isNotEmpty()) {
            geofenceManager.registerGeofences(
                places = uiState.places,
                onSuccess = {
                    // Geofences registered successfully
                },
                onError = { error ->
                    // Handle error - could show a snackbar or log
                }
            )
        }
    }

    // Clean up geofences when leaving the screen
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                geofenceManager.removeGeofences(
                    onSuccess = { /* Geofences removed */ },
                    onError = { error -> /* Handle error */ }
                )
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cultural Guide Map",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Geofence status indicator
                    if (uiState.isGeofencingActive) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.Green, shape = RoundedCornerShape(4.dp))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Active",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = true,
                    isBuildingEnabled = false, // Better performance
                    isIndoorEnabled = false
                ),
                uiSettings = MapUiSettings(
                    compassEnabled = true,
                    mapToolbarEnabled = false,
                    myLocationButtonEnabled = true,
                    zoomControlsEnabled = false // Custom zoom controls
                )
            ) {
                // Show user location
                uiState.userLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                        title = "Your Location",
                        snippet = "You are here",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }

                // Show place markers with visited status
                uiState.places.forEach { place ->
                    Marker(
                        state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                        title = place.name,
                        snippet = if (place.isVisited) {
                            "✅ Visited ${place.visitCount} times - Tap to review"
                        } else {
                            "📍 Not visited yet - Tap for details"
                        },
                        // Change marker color based on visited status
                        icon = if (place.isVisited) {
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN) // Green for visited
                        } else {
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) // Red for not visited
                        },
                        onClick = {
                            if (place.isVisited) {
                                onNavigateToReview(place.id)
                            } else {
                                onNavigateToDetail(place.id)
                            }
                            true // Consume click
                        }
                    )
                }
            }

            // Custom zoom controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .zIndex(1f)
            ) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val currentZoom = cameraPositionState.position.zoom
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(
                                    cameraPositionState.position.target,
                                    currentZoom + 1f
                                ),
                                durationMs = 300
                            )
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Filled.ZoomIn,
                        contentDescription = "Zoom In",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val currentZoom = cameraPositionState.position.zoom
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(
                                    cameraPositionState.position.target,
                                    currentZoom - 1f
                                ),
                                durationMs = 300
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        Icons.Filled.ZoomOut,
                        contentDescription = "Zoom Out",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            // Loading indicator
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.isGeofencingActive)
                                    "Setting up location alerts..."
                                else
                                    "Loading map...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            // Error message
            uiState.error?.let { error ->
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .zIndex(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }
    }
}