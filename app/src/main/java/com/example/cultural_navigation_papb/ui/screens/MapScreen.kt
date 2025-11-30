package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cultural_navigation_papb.data.viewmodels.MapsViewModel
import com.example.cultural_navigation_papb.ui.components.DraggableBottomSheetAlt
import com.example.cultural_navigation_papb.ui.components.LocationFilter
import com.example.cultural_navigation_papb.ui.components.PlaceDetailPopup
import com.example.cultural_navigation_papb.data.models.Place
import android.Manifest
import android.content.Context
import android.graphics.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.maps.android.compose.MapProperties
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onNavigateToDetail: (placeId: String) -> Unit,
    onBackClick: () -> Unit = {},
    viewModel: MapsViewModel = hiltViewModel<MapsViewModel>()
) {
    val context = LocalContext.current

    // ✅ Simple way: Create GeofenceManager directly with context
    val geofenceManager = remember {
        com.example.cultural_navigation_papb.geofence.GeofenceManager(context)
    }

    // State dari ViewModel
    val searchText by viewModel.searchText.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val nearbyPlaces by viewModel.nearbyPlaces.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()

    // ✅ FIX: Get visited places from actual Place data, not hardcoded empty set
    val visitedPlaces = remember(nearbyPlaces) {
        val visited = nearbyPlaces.filter { it.isVisited }.map { it.id }.toSet()
        android.util.Log.d("MapScreen", "🔄 Visited places updated: ${visited.size} places")
        visited.forEach { id ->
            val place = nearbyPlaces.find { it.id == id }
            android.util.Log.d("MapScreen", "  ✅ Visited: ${place?.name} (${place?.visitCount} times)")
        }
        visited
    }

    // ✅ FIX: Reload places periodically to reflect database changes
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000) // Wait 1 second
        while (true) {
            android.util.Log.d("MapScreen", "🔄 Reloading places from database...")
            viewModel.loadNearbyPlaces() // Reload to get latest data
            kotlinx.coroutines.delay(5000) // Refresh every 5 seconds
        }
    }

    // UI State
    val scope = rememberCoroutineScope()

    // State for alternative draggable bottom sheet
    var selectedLocationIndex by remember { mutableStateOf(-1) }
    var currentLocationFilter by remember { mutableStateOf(LocationFilter.ALL) }

    // State for popup dialog
    var showPlacePopup by remember { mutableStateOf(false) }
    var popupPlace by remember { mutableStateOf<Place?>(null) }

    // Permission handling
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Prambanan coordinates
    val prambananCenter = LatLng(-7.752008, 110.491825)
    val initialLocation = userLocation ?: prambananCenter

    // Camera position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 16f)
    }

    // ✅ REGISTER GEOFENCES - INI YANG PENTING!
    LaunchedEffect(nearbyPlaces, locationPermissionState.status) {
        if (nearbyPlaces.isNotEmpty() && locationPermissionState.status.isGranted) {
            android.util.Log.d("MapScreen", "📍 Registering geofences for ${nearbyPlaces.size} places")
            geofenceManager.registerGeofences(
                places = nearbyPlaces,
                onSuccess = {
                    android.util.Log.d("MapScreen", "✅ Geofences registered successfully!")
                },
                onError = { error ->
                    android.util.Log.e("MapScreen", "❌ Failed to register geofences: $error")
                }
            )
        }
    }

    // Request location permission and start updates
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }

        if (locationPermissionState.status.isGranted) {
            viewModel.startLocationUpdates()
        }
    }

    // Animate to selected place
    LaunchedEffect(selectedPlace) {
        selectedPlace?.let { place ->
            scope.launch {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(
                        LatLng(place.latitude, place.longitude),
                        17f
                    ),
                    durationMs = 1000
                )
            }
        }
    }

    // Clean Google Maps style with brown theme
    val mapProperties = MapProperties(
        isMyLocationEnabled = locationPermissionState.status.isGranted,
        mapStyleOptions = MapStyleOptions(
            """
            [
                {
                    "featureType": "all",
                    "elementType": "geometry.fill",
                    "stylers": [
                        {"color": "#f8f4e1"}
                    ]
                },
                {
                    "featureType": "water",
                    "elementType": "geometry",
                    "stylers": [
                        {"color": "#bcaaa4"}
                    ]
                },
                {
                    "featureType": "road",
                    "elementType": "geometry",
                    "stylers": [
                        {"color": "#8d6e63"}
                    ]
                },
                {
                    "featureType": "road.highway",
                    "elementType": "geometry.stroke",
                    "stylers": [
                        {"color": "#6d4c41"}
                    ]
                },
                {
                    "featureType": "poi",
                    "elementType": "geometry",
                    "stylers": [
                        {"color": "#a1887f"}
                    ]
                },
                {
                    "featureType": "administrative",
                    "elementType": "geometry.stroke",
                    "stylers": [
                        {"color": "#a1887f"}
                    ]
                }
            ]
            """
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            onMapClick = {
                viewModel.clearSelectedPlace()
            }
        ) {
            // User location marker
            userLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Your Location",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                )
            }

            // Cultural site markers
            nearbyPlaces.forEachIndexed { index, place ->
                val isVisited = visitedPlaces.contains(place.id)
                val isSelected = selectedPlace?.id == place.id

                Marker(
                    state = MarkerState(position = LatLng(place.latitude, place.longitude)),
                    title = place.name,
                    snippet = place.category,
                    icon = createNumberedMarker(context, nearbyPlaces.indexOf(place) + 1, isVisited),
                    onClick = {
                        // Show popup dialog and sync with draggable list
                        val placeIndex = nearbyPlaces.indexOf(place)
                        selectedLocationIndex = placeIndex
                        popupPlace = place
                        showPlacePopup = true
                        true
                    }
                )
            }
        }

        // Top controls
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .zIndex(2f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            FloatingActionButton(
                onClick = onBackClick,
                modifier = Modifier.size(48.dp),
                containerColor = Color.White,
                contentColor = Color(0xFF3E2723)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            // Search bar
            OutlinedTextField(
                value = searchText,
                onValueChange = { viewModel.searchLocation(it) },
                placeholder = {
                    Text(
                        text = "Cari destinasi...",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedBorderColor = Color(0xFF8D6E63),
                    unfocusedBorderColor = Color(0xFFBCAAA4),
                    cursorColor = Color(0xFF3E2723)
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF8D6E63),
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }

        // Zoom controls - Bottom Right (near current location button)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 280.dp)
                .zIndex(1f), // Lower z-index so it appears behind popup
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.zoomTo(currentZoom + 1f),
                            durationMs = 300
                        )
                    }
                },
                modifier = Modifier.size(36.dp), // Smaller size
                containerColor = Color.White,
                contentColor = Color(0xFF3E2723)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Zoom In",
                    modifier = Modifier.size(20.dp) // Smaller icon
                )
            }

            FloatingActionButton(
                onClick = {
                    scope.launch {
                        val currentZoom = cameraPositionState.position.zoom
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.zoomTo(currentZoom - 1f),
                            durationMs = 300
                        )
                    }
                },
                modifier = Modifier.size(36.dp), // Smaller size
                containerColor = Color.White,
                contentColor = Color(0xFF3E2723)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomOut,
                    contentDescription = "Zoom Out",
                    modifier = Modifier.size(20.dp) // Smaller icon
                )
            }
        }

        // Current location button - Bottom Right
        FloatingActionButton(
            onClick = {
                userLocation?.let { location ->
                    scope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(location, 16f),
                            durationMs = 1000
                        )
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 200.dp)
                .zIndex(1f), // Lower z-index so it appears behind popup
            containerColor = Color.White,
            contentColor = Color(0xFF3E2723)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "My Location",
                modifier = Modifier.size(18.dp) // Smaller icon
            )
        }

        // Alternative Draggable Bottom Sheet - properly positioned at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        ) {
            DraggableBottomSheetAlt(
                locations = nearbyPlaces,
                selectedIndex = selectedLocationIndex,
                visitedPlaceIds = visitedPlaces,
                onLocationClick = { index ->
                    val place = nearbyPlaces[index]
                    selectedLocationIndex = index
                    popupPlace = place
                    showPlacePopup = true
                },
                onFilterChanged = { filter ->
                    currentLocationFilter = filter
                },
                currentFilter = currentLocationFilter
            )
        }

        // Place Detail Popup with higher z-index
        if (showPlacePopup && popupPlace != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f) // Higher z-index to appear on top of controls
            ) {
                PlaceDetailPopup(
                    place = popupPlace!!,
                    onDismiss = {
                        showPlacePopup = false
                        popupPlace = null
                    },
                    onViewDetail = { placeId ->
                        showPlacePopup = false
                        popupPlace = null
                        onNavigateToDetail(placeId)
                    },
                    onGetDirections = { place ->
                        // Handle navigation to place
                        scope.launch {
                            cameraPositionState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(
                                    LatLng(place.latitude, place.longitude),
                                    18f
                                ),
                                durationMs = 1500
                            )
                        }
                        showPlacePopup = false
                        popupPlace = null
                    }
                )
            }
        }
    }
}

// Helper function untuk membuat marker dengan nomor dan status
private fun createNumberedMarker(context: Context, number: Int, isVisited: Boolean): BitmapDescriptor {
    val backgroundColor = if (isVisited) android.graphics.Color.GREEN else android.graphics.Color.rgb(255, 111, 0)
    val textColor = android.graphics.Color.WHITE

    // Create custom marker with Canvas
    val size = 80 // Size in pixels
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Background circle
    val paint = Paint().apply {
        color = backgroundColor
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

    // Number text
    val textPaint = Paint().apply {
        color = textColor
        textSize = 32f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        typeface = Typeface.DEFAULT_BOLD
    }
    canvas.drawText(
        number.toString(),
        size / 2f,
        size / 2f + textPaint.textSize / 3,
        textPaint
    )

    // Convert to BitmapDescriptor
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}