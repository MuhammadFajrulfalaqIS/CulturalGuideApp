package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cultural_navigation_papb.data.models.Place
import com.example.cultural_navigation_papb.data.viewmodels.ReviewViewModel
import com.example.cultural_navigation_papb.ui.components.ReviewForm
import com.example.cultural_navigation_papb.ui.components.ReviewList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    placeId: String? = null,
    navController: NavController? = null,
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    // Untuk sekarang, kita gunakan data dummy jika placeId null
    // Dalam implementasi nyata, placeId akan berasal dari navigation
    val currentPlace = remember(placeId) {
        if (placeId != null) {
            // Load dari database di sini nanti
            getDummyPlace(placeId)
        } else {
            getDummyPlace("candi_siwa")
        }
    }

    // Load reviews saat screen dimuat
    LaunchedEffect(placeId) {
        placeId?.let { reviewViewModel.loadReviewsForPlace(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Candi") },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Image
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box {
                        AsyncImage(
                            model = currentPlace.imageUrl,
                            contentDescription = currentPlace.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )

                        // Overlay Rating Badge
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(12.dp),
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = String.format("%.1f", currentPlace.rating),
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Basic Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = currentPlace.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = currentPlace.category.replace("_", " "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.typography.bodyMedium.color.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        Text(
                            text = currentPlace.description,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Quick Info Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Location Card
                    InfoCard(
                        icon = Icons.Default.LocationOn,
                        title = "Lokasi",
                        value = "${String.format("%.4f", currentPlace.latitude)}, ${String.format("%.4f", currentPlace.longitude)}",
                        modifier = Modifier.weight(1f)
                    )

                    // Open Time Card
                    InfoCard(
                        icon = Icons.Default.Schedule,
                        title = "Jam Buka",
                        value = "${currentPlace.openTime} - ${currentPlace.closeTime}",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Ticket Price
            if (currentPlace.ticketPrice > 0) {
                item {
                    InfoCard(
                        icon = Icons.Default.ConfirmationNumber,
                        title = "Harga Tiket",
                        value = "Rp ${currentPlace.ticketPrice}",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Review Form
            item {
                ReviewForm(
                    userId = "user_123", // Dalam implementasi nyata, dari user session
                    userName = "Wisatawan",
                    onReviewSubmitted = {
                        // Refresh reviews setelah submit
                        placeId?.let { reviewViewModel.loadReviewsForPlace(it) }
                    }
                )
            }

            // Reviews List
            item {
                ReviewList()
            }

            // Spacer untuk bottom padding
            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Helper function untuk dummy data
private fun getDummyPlace(placeId: String): Place {
    return when (placeId) {
        "candi_siwa" -> Place(
            id = "candi_siwa",
            name = "Candi Siwa",
            description = "Candi utama dan tertinggi di kompleks Prambanan, didedikasikan untuk Dewa Siwa. Candi ini memiliki tinggi 47 meter dan merupakan mahakarya arsitektur Hindu. Relief di dinding candi menceritakan kisah Ramayana.",
            imageUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800&h=400&fit=crop",
            latitude = -7.7520,
            longitude = 110.4891,
            category = "candi_utama",
            isAvailable = true,
            openTime = "06:00",
            closeTime = "17:00",
            ticketPrice = 50000,
            rating = 4.5f,
            reviewCount = 234
        )
        "candi_wisnu" -> Place(
            id = "candi_wisnu",
            name = "Candi Wisnu",
            description = "Candi di sebelah utara Candi Siwa, didedikasikan untuk Dewa Wisnu sebagai pemelihara alam semesta dalam trinitas Hindu. Relief di candi ini menggambarkan kisah Krisna.",
            imageUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800&h=400&fit=crop",
            latitude = -7.7515,
            longitude = 110.4895,
            category = "candi_utama",
            isAvailable = true,
            openTime = "06:00",
            closeTime = "17:00",
            ticketPrice = 0,
            rating = 4.3f,
            reviewCount = 156
        )
        "candi_sewu" -> Place(
            id = "candi_sewu",
            name = "Candi Sewu",
            description = "Kompleks candi Buddha terbesar kedua setelah Borobudur, terletak sekitar 800 meter selatan Candi Prambanan. Nama aslinya adalah Manjusrigrha. Kompleks ini memiliki 249 candi perwara.",
            imageUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800&h=400&fit=crop",
            latitude = -7.7600,
            longitude = 110.4920,
            category = "candi_buddha",
            isAvailable = true,
            openTime = "06:00",
            closeTime = "17:00",
            ticketPrice = 30000,
            rating = 4.6f,
            reviewCount = 189
        )
        else -> Place(
            id = "default",
            name = "Candi Prambanan",
            description = "Kompleks candi Hindu terbesar di Indonesia yang dibangun pada abad ke-9. Terdiri dari tiga candi utama yang didedikasikan untuk Trimurti: Brahma, Wisnu, dan Siwa.",
            imageUrl = "https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=800&h=400&fit=crop",
            latitude = -7.7520,
            longitude = 110.4891,
            category = "candi_utama",
            isAvailable = true,
            openTime = "06:00",
            closeTime = "17:00",
            ticketPrice = 50000,
            rating = 4.7f,
            reviewCount = 1245
        )
    }
}