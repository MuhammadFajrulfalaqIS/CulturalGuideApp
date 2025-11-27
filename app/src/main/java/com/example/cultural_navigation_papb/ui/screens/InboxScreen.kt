package com.example.cultural_navigation_papb.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cultural_navigation_papb.data.viewmodels.InboxViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InboxScreen(
    onNavigateBack: () -> Unit,
    // Tambahkan parameter navigasi ke detail
    onNavigateToDetail: (String) -> Unit,
    viewModel: InboxViewModel = viewModel()
) {
    val savedPlaces by viewModel.savedPlaces.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inbox Offline") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (savedPlaces.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada data yang didownload.", color = MaterialTheme.colorScheme.secondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(savedPlaces) { place ->
                    Card(
                        elevation = CardDefaults.cardElevation(4.dp),
                        // Tambahkan modifier clickable di sini
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Saat diklik, pergi ke detail membawa ID
                                onNavigateToDetail(place.id)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                        ) {
                            // Gambar
                            Image(
                                painter = painterResource(id = place.imageResId),
                                contentDescription = place.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(100.dp)
                                    .fillMaxHeight()
                            )

                            // Teks & Tombol Hapus
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = place.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = place.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Tombol Hapus
                            IconButton(
                                onClick = { viewModel.removePlace(place.id) },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}