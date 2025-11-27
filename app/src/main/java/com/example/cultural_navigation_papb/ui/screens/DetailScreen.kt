package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cultural_navigation_papb.data.viewmodels.InboxViewModel
import com.example.cultural_navigation_papb.data.viewmodels.PlaceViewModel
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    placeId: String,
    onNavigateBack: () -> Unit,
    // ViewModel untuk mengambil data tempat (Data Asli)
    viewModel: PlaceViewModel = hiltViewModel(),
    // ViewModel untuk fitur download offline (Database Lokal)
    inboxViewModel: InboxViewModel = viewModel()
) {
    // 1. Ambil data place berdasarkan ID
    val place = remember(placeId) { viewModel.getPlaceById(placeId) }

    // 2. Cek apakah tempat ini sudah didownload sebelumnya
    // 'collectAsState' akan memantau perubahan database secara real-time
    val isDownloaded by inboxViewModel.isDownloaded(placeId).collectAsState(initial = false)

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
        },
        // 3. Tambahkan Tombol Aksi (FAB) untuk Download
        floatingActionButton = {
            if (place != null) {
                FloatingActionButton(
                    onClick = {
                        if (isDownloaded) {
                            // Jika sudah ada, hapus dari inbox
                            inboxViewModel.removePlace(place.id)
                        } else {
                            // Jika belum, simpan ke inbox
                            inboxViewModel.downloadPlace(
                                id = place.id,
                                name = place.name,
                                desc = place.detailedDescription, // Simpan deskripsi lengkap
                                imageResId = place.imageUrl // Simpan ID gambar
                            )
                        }
                    },
                    // Ubah warna: Hijau jika tersimpan, Primary jika belum
                    containerColor = if (isDownloaded) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    // Ubah ikon: Ceklis jika tersimpan, Panah bawah jika belum
                    Icon(
                        imageVector = if (isDownloaded) Icons.Default.Check else Icons.Default.Download,
                        contentDescription = if (isDownloaded) "Hapus dari Offline" else "Simpan Offline"
                    )
                }
            }
        }
    ) { paddingValues ->
        if (place != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- 1. Header Image ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Image(
                        painter = painterResource(id = place.imageUrl),
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // --- 2. Content Section ---
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title
                    Text(
                        text = place.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A3428) // Dark brown
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
                                containerColor = Color(0xFFC9A882) // Light brown
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                SectionTitle("Informasi Kunjungan")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = place.visitingInfo,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 22.sp,
                                    color = Color(0xFF4A3428) // Dark brown text
                                )
                            }
                        }
                    }

                    // Tambahan Spacer di bawah agar konten tidak tertutup FAB
                    Spacer(modifier = Modifier.height(80.dp))
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
        color = Color(0xFF4A3428) // Dark brown
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