// File: ui/screens/ListScreen.kt
package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
// --- Impor untuk Preview ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cultural_navigation_papb.R
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.viewmodels.PlaceViewModel
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme

/**
 * ListScreen - Menampilkan daftar candi-candi di Prambanan
 * READ-ONLY: User hanya bisa melihat dan klik untuk detail
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onNavigateToDetail: (placeId: String) -> Unit,
    viewModel: PlaceViewModel = hiltViewModel()
) {
    // Ambil data dari ViewModel
    val places by viewModel.places.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Candi-Candi di Kompleks Prambanan & Lokasi-Lokasi Lainnya",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3E2723), // Dark brown from image
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFF5F5F5) // Light gray background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                places.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Tidak ada data candi",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(places) { place ->
                            PlaceCard(
                                place = place,
                                onClick = { onNavigateToDetail(place.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card untuk menampilkan informasi candi
 * Hanya bisa diklik untuk melihat detail, tanpa tombol hapus
 */
@Composable
fun PlaceCard(
    place: Place,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Image on the left
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            ) {
                Image(
                    painter = painterResource(id = place.imageUrl),
                    contentDescription = place.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Text content on the right with brown background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFC9A882), // Tan/brown color from image
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF3E2723), // Dark brown text
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = place.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF5D4037), // Medium brown text
                            maxLines = 3
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun ListScreenPreview() {
    CulturalnavigationpapbTheme {
        // Preview dengan data dummy
        val dummyPlaces = listOf(
            Place("1", "Candi Siwa", "Candi utama dan tertinggi (47m) yang didedikasikan untuk Dewa Siwa", R.drawable.arcasiwa),
            Place("2", "Candi Wisnu", "Didedikasikan untuk Dewa Wisnu sang pemelihara alam semesta", R.drawable.arcawisnu),
            Place("3", "Candi Brahma", "Didedikasikan untuk Dewa Brahma sang pencipta alam semesta", R.drawable.arcabrahma)
        )

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Candi-Candi di Kompleks Prambanan & Lokasi-Lokasi Lainnya",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF4A3428), // Dark brown from image
                        titleContentColor = Color.White
                    )
                )
            },
            containerColor = Color(0xFFF5F5F5) // Light gray background
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(dummyPlaces) { place ->
                    PlaceCard(
                        place = place,
                        onClick = {}
                    )
                }
            }
        }
    }
}

