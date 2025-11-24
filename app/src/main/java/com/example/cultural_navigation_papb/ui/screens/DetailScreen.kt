package com.example.cultural_navigation_papb.ui.screens

// --- Impor untuk Composable & Preview ---
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.example.cultural_navigation_papb.data.viewmodels.PlaceViewModel
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme


// Diubah dari 'class' menjadi 'Composable fun'
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    placeId: String,
    onNavigateBack: () -> Unit,
    viewModel: PlaceViewModel = hiltViewModel()
) {
    // Ambil data place berdasarkan ID
    // Karena data statis, kita bisa langsung mengambilnya
    val place = remember(placeId) { viewModel.getPlaceById(placeId) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detail Lokasi") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        if (place != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // 1. Header Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    val painter = rememberAsyncImagePainter(model = place.imageUrl)
                    Image(
                        painter = painter,
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // 2. Content Section
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Chip
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Wisata Sejarah") }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Overview Section
                    SectionTitle("Tentang")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = place.detailedDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Historical Information Section
                    if (place.historicalInfo.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionTitle("Sejarah")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = place.historicalInfo,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Architecture Section
                    if (place.architectureInfo.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionTitle("Arsitektur")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = place.architectureInfo,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Visiting Information Section
                    if (place.visitingInfo.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SectionTitle("Informasi Kunjungan")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = place.visitingInfo,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        } else {
            // Error State - Place not found
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Data lokasi tidak ditemukan",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onNavigateBack) {
                        Text("Kembali")
                    }
                }
            }
        }
    }
}

// Helper Composable for Section Titles
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}


@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DetailScreenPreview() {
    CulturalnavigationpapbTheme {
        DetailScreen(
            placeId = "sample-id",
            onNavigateBack = {}
        )
    }
}
//