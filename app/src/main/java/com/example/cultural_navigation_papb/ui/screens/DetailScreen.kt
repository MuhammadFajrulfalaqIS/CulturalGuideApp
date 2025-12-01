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
import coil.compose.AsyncImage
import com.example.cultural_navigation_papb.data.viewmodels.InboxViewModel
import com.example.cultural_navigation_papb.data.viewmodels.PlaceViewModel
import com.example.cultural_navigation_papb.data.viewmodels.ReviewViewModel
import com.example.cultural_navigation_papb.ui.theme.CulturalnavigationpapbTheme
import com.example.cultural_navigation_papb.ui.components.ReviewSection
import com.example.cultural_navigation_papb.ui.components.ImprovedReviewDialog

// Helper Composable for Section Titles
@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF4A3428) // Dark brown
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    placeId: String,
    onNavigateBack: () -> Unit,
    openReviewDialogOnStart: Boolean = false, // ✅ NEW: Parameter untuk auto-open review dialog dari notifikasi
    userId: String = "current_user_id", // TODO: Get from Auth
    userName: String = "User Name", // TODO: Get from Auth
    // ViewModel untuk mengambil data tempat (Data Asli)
    viewModel: PlaceViewModel = hiltViewModel(),
    // ViewModel untuk fitur download offline (Database Lokal)
    inboxViewModel: InboxViewModel = viewModel(),
    // ViewModel untuk review system
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    // 1. Ambil data place berdasarkan ID dengan fallback ke default place
    val place by remember(placeId) {
        derivedStateOf {
            viewModel.getPlaceById(placeId)
        }
    }

    // Debug logging untuk melihat apakah placeId diterima dengan benar
    android.util.Log.d("DetailScreen", "Received placeId: $placeId")
    android.util.Log.d("DetailScreen", "Found place: ${place?.name}")

    // Loading state dengan timeout
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(place) {
        val currentPlace = place
        if (currentPlace != null) {
            isLoading = false
        } else {
            // Timeout setelah 3 detik untuk fallback ke default data
            kotlinx.coroutines.delay(3000)
            if (place == null) {
                isLoading = false
            }
        }
    }

    // 2. Cek apakah tempat ini sudah didownload sebelumnya
    // 'collectAsState' akan memantau perubahan database secara real-time
    val isDownloaded by inboxViewModel.isDownloaded(placeId).collectAsState(initial = false)

    // 3. Load reviews untuk tempat ini
    LaunchedEffect(placeId) {
        reviewViewModel.loadReviewsForPlace(placeId)
    }

    // 4. Review state management
    val reviews by reviewViewModel.reviews.collectAsState()
    val averageRating by remember(reviews) { derivedStateOf { reviewViewModel.getAverageRating() } }
    val ratingDistribution by remember(reviews) { derivedStateOf { reviewViewModel.getRatingDistribution() } }
    val isSubmitting by reviewViewModel.isSubmitting.collectAsState()
    val submitError by reviewViewModel.submitError.collectAsState()

    // 5. Dialog state for adding review
    var showAddReviewDialog by remember { mutableStateOf(false) }

    // ✅ NEW: Auto-open review dialog jika dipanggil dari notifikasi
    LaunchedEffect(openReviewDialogOnStart) {
        if (openReviewDialogOnStart) {
            android.util.Log.d("DetailScreen", "📝 Auto-opening review dialog from notification for place: $placeId")
            kotlinx.coroutines.delay(500) // Delay sedikit agar UI sudah loaded
            showAddReviewDialog = true
        }
    }

    // 6. Show success/error messages
    LaunchedEffect(isSubmitting) {
        if (!isSubmitting && submitError == null) {
            reviewViewModel.resetForm()
        }
    }

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
            val fabPlace = place
            if (fabPlace != null) {
                FloatingActionButton(
                    onClick = {
                        if (isDownloaded) {
                            // Jika sudah ada, hapus dari inbox
                            inboxViewModel.removePlace(fabPlace.id)
                        } else {
                            // Jika belum, simpan ke inbox
                            inboxViewModel.downloadPlace(
                                id = fabPlace.id,
                                name = fabPlace.name,
                                desc = fabPlace.detailedDescription, // Simpan deskripsi lengkap
                                imageResId = fabPlace.imageUrl // Simpan ID gambar
                            )
                        }
                    },
                    // Ubah warna: Hijau jika tersimpan, Dark brown jika belum
                    containerColor = if (isDownloaded) Color(0xFF4CAF50) else Color(0xFF4A3428),
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
        if (isLoading) {
            // Loading or error state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4A3428)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Memuat data destinasi...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF4A3428)
                    )
                }
            }
        } else {
            val currentPlace = place
            if (currentPlace != null) {
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
                    AsyncImage(
                        model = currentPlace.imageUrl,
                        contentDescription = currentPlace.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // --- 2. Content Section ---
                Column(modifier = Modifier.padding(16.dp)) {
                    // Title
                    Text(
                        text = currentPlace.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A3428) // Dark brown
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Chip
                    SuggestionChip(
                        onClick = { },
                        label = { Text("Wisata Sejarah") },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = Color(0xFFC9A882), // Light brown
                            labelColor = Color(0xFF4A3428) // Dark brown text
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Overview Section
                    SectionTitle("Tentang")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentPlace.detailedDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Historical Information Section
                    if (currentPlace.historicalInfo.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionTitle("Sejarah")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentPlace.historicalInfo,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Architecture Section
                    if (currentPlace.architectureInfo.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(24.dp))
                        SectionTitle("Arsitektur")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentPlace.architectureInfo,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Visiting Information Section
                    if (currentPlace.visitingInfo.isNotEmpty()) {
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
                                    text = currentPlace.visitingInfo,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 22.sp,
                                    color = Color(0xFF4A3428) // Dark brown text
                                )
                            }
                        }
                    }

                    // Reviews Section
                    ReviewSection(
                        reviews = reviews,
                        averageRating = averageRating,
                        ratingDistribution = ratingDistribution,
                        onAddReview = { showAddReviewDialog = true },
                        onHelpfulClick = { reviewId ->
                            reviewViewModel.markReviewHelpful(reviewId)
                        }
                    )

                    // Tambahan Spacer di bawah agar konten tidak tertutup FAB
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Add Review Dialog
    val dialogPlace = place
    if (showAddReviewDialog && dialogPlace != null) {
        ImprovedReviewDialog(
            place = dialogPlace,
            onDismiss = { showAddReviewDialog = false }
        )
    }
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
}
