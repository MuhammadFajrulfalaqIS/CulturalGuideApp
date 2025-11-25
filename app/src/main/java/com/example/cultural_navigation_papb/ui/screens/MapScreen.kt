// File: ui/screens/MapScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import androidx.compose.ui.graphics.toArgb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.zIndex
import androidx.compose.ui.text.style.TextOverflow
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Dash
import com.google.android.gms.maps.model.Gap
import com.google.maps.android.compose.*
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cultural_navigation_papb.data.viewmodels.MapsViewModel
import com.example.cultural_navigation_papb.data.models.Place
import android.Manifest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.maps.android.compose.MapProperties
import com.google.android.gms.maps.model.MapStyleOptions
import androidx.compose.foundation.interaction.MutableInteractionSource

// Define earth tone colors
val EarthBrown = Color(0xFF3E2723)
val LightBrown = Color(0xFF6D4C41)
val OrangeAccent = Color(0xFFFF6F00)
val Beige = Color(0xFFFFF3E0)
val WarmWhite = Color(0xFFFFFEF7)


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    onNavigateToDetail: (placeId: String) -> Unit,
    onBackClick: () -> Unit = {},
    viewModel: MapsViewModel = hiltViewModel<MapsViewModel>()
) {
    // 1. KONSUMSI STATE DARI VIEWMODEL
    val searchText by viewModel.searchText.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val nearbyPlaces by viewModel.nearbyPlaces.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()
    val routeToPlace by viewModel.selectedRoute.collectAsState()

    // State for UI
    var selectedLocationIndex by remember { mutableIntStateOf(-1) }

    // Get location permission status
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    // Precise coordinates for Prambanan Temple area
    val prambananCenter = LatLng(-7.752008, 110.491825)
    val initialLocation = userLocation ?: prambananCenter

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 16f)
    }

    // Efek Samping untuk Meminta Izin dan Memulai Location Updates
    LaunchedEffect(locationPermissionState.status.isGranted) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }

        if (locationPermissionState.status.isGranted) {
            viewModel.startLocationUpdates()
        }
    }

    // Konfigurasi MapProperties dengan tema earth tone untuk situs budaya
    val mapProperties = MapProperties(
        isMyLocationEnabled = locationPermissionState.status.isGranted,
        mapStyleOptions = MapStyleOptions(
            """
            [
                {
                    "featureType": "all",
                    "elementType": "geometry",
                    "stylers": [
                        {
                            "color": "#f5f3f0"
                        }
                    ]
                },
                {
                    "featureType": "all",
                    "elementType": "labels.text.fill",
                    "stylers": [
                        {
                            "color": "#3e2723"
                        }
                    ]
                },
                {
                    "featureType": "all",
                    "elementType": "labels.text.stroke",
                    "stylers": [
                        {
                            "color": "#ffffff"
                        },
                        {
                            "lightness": 13
                        }
                    ]
                },
                {
                    "featureType": "administrative",
                    "elementType": "geometry.fill",
                    "stylers": [
                        {
                            "color": "#d7ccc8"
                        }
                    ]
                },
                {
                    "featureType": "administrative",
                    "elementType": "geometry.stroke",
                    "stylers": [
                        {
                            "color": "#bcaaa4"
                        }
                    ]
                },
                {
                    "featureType": "landscape",
                    "elementType": "geometry",
                    "stylers": [
                        {
                            "color": "#efebe9"
                        }
                    ]
                },
                {
                    "featureType": "poi",
                    "elementType": "geometry",
                    "stylers": [
                        {
                            "color": "#d7ccc8"
                        },
                        {
                            "visibility": "on"
                        }
                    ]
                },
                {
                    "featureType": "poi",
                    "elementType": "labels",
                    "stylers": [
                        {
                            "visibility": "on"
                        }
                    ]
                },
                {
                    "featureType": "poi",
                    "elementType": "labels.text.fill",
                    "stylers": [
                        {
                            "color": "#5d4037"
                        }
                    ]
                },
                {
                    "featureType": "poi.park",
                    "elementType": "geometry",
                    "stylers": [
                        {
                            "color": "#c8e6c9"
                        },
                        {
                            "visibility": "on"
                        }
                    ]
                },
                {
                    "featureType": "poi.park",
                    "elementType": "labels.text.fill",
                    "stylers": [
                        {
                            "color": "#2e7d32"
                        }
                    ]
                },
                {
                    "featureType": "road",
                    "elementType": "geometry",
                    "stylers": [
                        {
                            "color": "#ffffff"
                        },
                        {
                            "visibility": "on"
                        }
                    ]
                },
                {
                    "featureType": "road",
                    "elementType": "geometry.fill",
                    "stylers": [
                        {
                            "color": "#ffffff"
                        }
                    ]
                },
                {
                    "featureType": "road",
                    "elementType": "geometry.stroke",
                    "stylers": [
                        {
                            "color": "#bcaaa4"
                        },
                        {
                            "lightness": 25
                        }
                    ]
                },
                {
                    "featureType": "road",
                    "elementType": "labels",
                    "stylers": [
                        {
                            "visibility": "on"
                        }
                    ]
                },
                {
                    "featureType": "road",
                    "elementType": "labels.text.fill",
                    "stylers": [
                        {
                            "color": "#5d4037"
                        }
                    ]
                },
                {
                    "featureType": "road.arterial",
                    "elementType": "geometry",
                    "stylers": [
                        {
                            "color": "#ffffff"
                        }
                    ]
                },
                {
                    "featureType": "road.highway",
                    "elementType": "geometry",
                    "stylers": [
                        {
                            "color": "#f5f3f0"
                        }
                    ]
                },
                {
                    "featureType": "road.highway",
                    "elementType": "geometry.stroke",
                    "stylers": [
                        {
                            "color": "#bcaaa4"
                        }
                    ]
                },
                {
                    "featureType": "road.local",
                    "elementType": "geometry",
                    "stylers": [
                        {
                            "color": "#ffffff"
                        }
                    ]
                },
                {
                    "featureType": "water",
                    "elementType": "geometry",
                    "stylers": [
                        {
                            "color": "#b3d9ff"
                        },
                        {
                            "lightness": 17
                        }
                    ]
                },
                {
                    "featureType": "water",
                    "elementType": "labels.text.fill",
                    "stylers": [
                        {
                            "color": "#0277bd"
                        }
                    ]
                }
            ]
            """.trimIndent()
        )
    )

    // Presisi koordinat yang lebih akurat untuk lokasi-lokasi di Prambanan dan sekitarnya
    val prambananLocations = listOf(
        // Candi Utama (more accurate coordinates)
        Place(
            id = "1",
            name = "Candi Siwa Mahadeva",
            description = "Candi utama dan tertinggi, dedicated to Lord Shiva",
            imageUrl = "",
            latitude = -7.752128,
            longitude = 110.491732,
            category = "candi_utama",
            rating = 4.9f,
            reviewCount = 3250
        ),
        Place(
            id = "2",
            name = "Candi Wisnu",
            description = "Candi utara, dedicated to Lord Vishnu the preserver",
            imageUrl = "",
            latitude = -7.751489,
            longitude = 110.492237,
            category = "candi_utama",
            rating = 4.8f,
            reviewCount = 2980
        ),
        Place(
            id = "3",
            name = "Candi Brahma",
            description = "Candi selatan, dedicated to Lord Brahma the creator",
            imageUrl = "",
            latitude = -7.752767,
            longitude = 110.491278,
            category = "candi_utama",
            rating = 4.7f,
            reviewCount = 2760
        ),
        // Candi Perwara (more accurate positions)
        Place(
            id = "4",
            name = "Candi Nandi",
            description = "Candi pelana (vahana) di depan Candi Siwa",
            imageUrl = "",
            latitude = -7.752988,
            longitude = 110.491602,
            category = "candi_perwara",
            rating = 4.6f,
            reviewCount = 1840
        ),
        Place(
            id = "5",
            name = "Candi Wahana",
            description = "Candi pelana di depan Candi Wisnu",
            imageUrl = "",
            latitude = -7.751278,
            longitude = 110.492581,
            category = "candi_perwara",
            rating = 4.5f,
            reviewCount = 1520
        ),
        Place(
            id = "6",
            name = "Candi Angsa",
            description = "Candi pelana di depan Candi Brahma",
            imageUrl = "",
            latitude = -7.753307,
            longitude = 110.490658,
            category = "candi_perwara",
            rating = 4.4f,
            reviewCount = 1290
        ),
        Place(
            id = "7",
            name = "Candi Apit",
            description = "Candi pendamping kecil di antara candi utama",
            imageUrl = "",
            latitude = -7.751812,
            longitude = 110.491404,
            category = "candi_perwara",
            rating = 4.3f,
            reviewCount = 980
        ),
        // Candi Keliling (Peripheral temples)
        Place(
            id = "8",
            name = "Candi Kelir Utara",
            description = "Candi pagar di sisi utara kompleks",
            imageUrl = "",
            latitude = -7.750987,
            longitude = 110.493012,
            category = "candi_perwara",
            rating = 4.2f,
            reviewCount = 650
        ),
        Place(
            id = "9",
            name = "Candi Kelir Selatan",
            description = "Candi pagar di sisi selatan kompleks",
            imageUrl = "",
            latitude = -7.753269,
            longitude = 110.490548,
            category = "candi_perwara",
            rating = 4.2f,
            reviewCount = 590
        ),
        // Fasilitas Rekreasi dan Wisata
        Place(
            id = "10",
            name = "Museum Taman Wisnu",
            description = "Museum arkeologi dengan artefak Prambanan",
            imageUrl = "",
            latitude = -7.750587,
            longitude = 110.493589,
            category = "museum",
            rating = 4.6f,
            reviewCount = 1420
        ),
        Place(
            id = "11",
            name = "Taman Wisnu",
            description = "Taman hijau dengan view istimewa",
            imageUrl = "",
            latitude = -7.750723,
            longitude = 110.493421,
            category = "taman",
            rating = 4.5f,
            reviewCount = 890
        ),
        Place(
            id = "12",
            name = "Area Parkir Utama",
            description = "Area parkir utama pengunjung",
            imageUrl = "",
            latitude = -7.753789,
            longitude = 110.492145,
            category = "fasilitas",
            rating = 4.1f,
            reviewCount = 430
        ),
        Place(
            id = "13",
            name = "Pusat Informasi Pengunjung",
            description = "Center untuk informasi dan tiket",
            imageUrl = "",
            latitude = -7.753456,
            longitude = 110.491987,
            category = "fasilitas",
            rating = 4.3f,
            reviewCount = 670
        ),
        Place(
            id = "14",
            name = "Warung Budaya",
            description = "Area kuliner tradisional dan souvenir",
            imageUrl = "",
            latitude = -7.752345,
            longitude = 110.492876,
            category = "kuliner",
            rating = 4.4f,
            reviewCount = 1120
        ),
        // Situs Bersejarah Terdekat
        Place(
            id = "15",
            name = "Candi Sewu",
            description = "Candi Buddha terdekat, 500m dari Prambanan",
            imageUrl = "",
            latitude = -7.746890,
            longitude = 110.492123,
            category = "situs_buddha",
            rating = 4.7f,
            reviewCount = 2130
        ),
        Place(
            id = "16",
            name = "Candi Bubrah",
            description = "Candi Hindu kecil dekat Prambanan",
            imageUrl = "",
            latitude = -7.756234,
            longitude = 110.490876,
            category = "situs_hindu",
            rating = 4.3f,
            reviewCount = 560
        )
    )

    // Function to handle location selection
    fun selectLocation(index: Int) {
        selectedLocationIndex = if (selectedLocationIndex == index) -1 else index
        val place = prambananLocations[index]
        viewModel.selectPlace(place)

        // Generate route from user location to selected place
        userLocation?.let { userLoc ->
            viewModel.generateRouteToPlace(userLoc, place)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Top bar with back button and search
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .background(Color.White, CircleShape)
                    .shadow(elevation = 8.dp, shape = CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = EarthBrown
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Search Bar
            ExpandedSearchBar(
                value = searchText,
                onValueChange = viewModel::onSearchTextChange,
                onSearch = viewModel::searchLocation,
                modifier = Modifier.weight(1f)
            )
        }

        // Map takes up 2/3 of the screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled = false
                ),
                properties = mapProperties
            ) {
                // Show numbered markers for each Prambanan location
                prambananLocations.forEachIndexed { index, place ->
                    Marker(
                        state = MarkerState(position = place.getLatLng()),
                        title = "${index + 1}. ${place.name}",
                        snippet = "Rating: ${place.rating} • ${place.reviewCount} reviews",
                        icon = if (selectedLocationIndex == index) createSelectedMarker(index + 1) else createNumberedMarker(index + 1),
                        onClick = {
                            selectLocation(index)
                            true
                        }
                    )
                }

                // Draw route if available
                routeToPlace?.let { route ->
                    Polyline(
                        points = route,
                        color = OrangeAccent,
                        width = 6f,
                        pattern = listOf(Dash(10f), Gap(5f))
                    )
                }

                // Draw user location marker if available
                userLocation?.let { loc ->
                    Marker(
                        state = MarkerState(position = loc),
                        title = "Your Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                    )
                }
            }
        }

        // Bottom Location List (1/3 of screen)
        LocationListBottom(
            locations = prambananLocations,
            selectedIndex = selectedLocationIndex,
            onLocationClick = { index -> selectLocation(index) },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }

    // Show selected place dialog
    selectedPlace?.let { place ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    // Close when clicking outside
                    viewModel.clearSelectedPlace()
                    selectedLocationIndex = -1
                },
            contentAlignment = Alignment.Center
        ) {
            SelectedPlaceDialog(
                place = place,
                onClose = {
                    viewModel.clearSelectedPlace()
                    selectedLocationIndex = -1
                },
                onNavigate = { onNavigateToDetail(place.id) }
            )
        }
    }
}

// ========================================
// NEW MAP DESIGN COMPONENTS
// ========================================

@Composable
fun ExpandedSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text("Cari lokasi candi...", color = LightBrown) },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search",
                tint = EarthBrown
            )
        },
        shape = RoundedCornerShape(28.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = EarthBrown,
            unfocusedBorderColor = LightBrown.copy(alpha = 0.5f),
            cursorColor = EarthBrown,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        modifier = modifier.fillMaxWidth()
    )
}

// Helper Functions for creating numbered markers
fun createNumberedMarker(number: Int): BitmapDescriptor {
    val size = 60 // Reduced size for temple markers (was 120)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Subtle background circle with earth tone for temples
    val backgroundPaint = Paint().apply {
        color = AndroidColor.parseColor("#FF8D6E63") // Earth brown color
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, backgroundPaint)

    // White inner circle
    val innerPaint = Paint().apply {
        color = AndroidColor.WHITE
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, innerPaint)

    // Number text (smaller and more subtle)
    val textPaint = Paint().apply {
        color = AndroidColor.parseColor("#FF5D4037") // Darker earth tone
        textSize = 20f // Reduced text size (was 48f)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = true
    }

    // Draw number
    canvas.drawText(
        number.toString(),
        size / 2f,
        size / 2f + 7f, // Adjusted for smaller size
        textPaint
    )

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

// Function to create selected marker (highlighted)
fun createSelectedMarker(number: Int): BitmapDescriptor {
    val size = 70 // Reduced size for selected temple markers (was 140)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    // Subtle background circle with darker earth tone for selected temples
    val backgroundPaint = Paint().apply {
        color = AndroidColor.parseColor("#FF6D4C41") // Darker earth brown for selected
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, backgroundPaint)

    // White inner circle
    val innerPaint = Paint().apply {
        color = AndroidColor.WHITE
        isAntiAlias = true
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 5f, innerPaint)

    // Number text (appropriate size for temple markers)
    val textPaint = Paint().apply {
        color = AndroidColor.parseColor("#FF4E342E") // Even darker earth tone for text
        textSize = 24f // Reduced text size (was 56f)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        isFakeBoldText = true
    }

    // Draw number
    canvas.drawText(
        number.toString(),
        size / 2f,
        size / 2f + 8f, // Adjusted for smaller size
        textPaint
    )

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun LocationListBottom(
    locations: List<Place>,
    selectedIndex: Int,
    onLocationClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = "Lokasi Tujuan",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = EarthBrown,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Scrollable Location List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(locations) { index, place ->
                    LocationListItem(
                        place = place,
                        number = index + 1,
                        isSelected = selectedIndex == index,
                        onClick = { onLocationClick(index) }
                    )
                }
            }
        }
    }
}

@Composable
fun LocationListItem(
    place: Place,
    number: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) OrangeAccent.copy(alpha = 0.1f) else Color.White
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, OrangeAccent) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Number Circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        if (isSelected) OrangeAccent else LightBrown.copy(alpha = 0.8f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number.toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Place Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EarthBrown
                )
                Text(
                    text = place.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐ ${place.rating}",
                        fontSize = 12.sp,
                        color = OrangeAccent,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${place.reviewCount} ulasan)",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            // Navigation Icon
            Icon(
                Icons.Default.Directions,
                contentDescription = "Navigate",
                tint = if (isSelected) OrangeAccent else LightBrown,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SelectedPlaceDialog(
    place: Place,
    onClose: () -> Unit,
    onNavigate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Header with icon and title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Temple icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            LightBrown.copy(alpha = 0.2f),
                            RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏛️",
                        fontSize = 24.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = place.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EarthBrown,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Category badge
                    Text(
                        text = when (place.category) {
                            "candi_utama" -> "Candi Utama"
                            "candi_perwara" -> "Candi Perwara"
                            "museum" -> "Museum"
                            "fasilitas" -> "Fasilitas"
                            else -> "Situs Bersejarah"
                        },
                        fontSize = 12.sp,
                        color = OrangeAccent,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(OrangeAccent.copy(alpha = 0.1f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                // Close button
                IconButton(onClick = onClose) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = place.description,
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = Color.DarkGray,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Rating and reviews row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Rating stars
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⭐",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", place.rating),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EarthBrown
                    )
                    if (place.reviewCount > 0) {
                        Text(
                            text = " (${place.reviewCount} ulasan)",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Opening status
                if (place.isOpen()) {
                    Text(
                        text = "🟢 Buka",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF4CAF50)
                    )
                } else {
                    Text(
                        text = "🔴 Tutup",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFF44336)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Detail button
                Button(
                    onClick = onNavigate,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EarthBrown,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp
                    )
                ) {
                    Text(
                        text = "Lihat Detail",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Close button
                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier
                        .weight(0.6f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, EarthBrown.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = EarthBrown
                    )
                ) {
                    Text(
                        text = "Tutup",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// Helper Functions for New Design
fun getOrangeMarker(): BitmapDescriptor {
    return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
}

// Legacy helper function for backward compatibility
fun getIconByCategory(category: String): BitmapDescriptor {
    return when (category) {
        "candi_utama" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
        "candi_perwara" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
        "candi_buddha" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
        "museum" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
        "rekreasi" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
        "kuliner" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        "situs_suci" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)
        else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
    }
}